package ru.kappers.service.impl;

import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import ru.kappers.UnitTest;
import ru.kappers.config.KappersProperties;
import ru.kappers.exceptions.CurrencyRateGettingException;
import ru.kappers.model.CurrencyRate;
import ru.kappers.service.CurrencyRateService;
import ru.kappers.service.MessageTranslator;
import ru.kappers.service.parser.CBRFDailyCurrencyRatesParser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CurrencyServiceImplTest extends UnitTest {
    @InjectMocks
    private CurrencyServiceImpl currencyService;
    @Mock
    private CurrencyRateService currencyRateService;
    @Mock
    private CBRFDailyCurrencyRatesParser currencyRatesParser;
    @Spy
    private KappersProperties kappersProperties;
    @Mock
    private MessageTranslator translator;

    @Test
    void tryRefreshCurrencyRatesForTodayMustSaveRateIfTableHasNotRate() {
        final var currencyRate = currencyRateWithDateAndCharCode(LocalDate.now(), CurrencyUnit.USD.getCode());
        final List<CurrencyRate> currencyRates = Collections.singletonList(currencyRate);
        when(currencyRatesParser.parseFromCBRF()).thenReturn(currencyRates);
        when(currencyRateService.isExist(currencyRate.getDate(), currencyRate.getCharCode()))
                .thenReturn(false);

        currencyService.tryRefreshCurrencyRatesForToday();

        verify(currencyRatesParser).parseFromCBRF();
        verify(currencyRateService).isExist(currencyRate.getDate(), currencyRate.getCharCode());
        verify(currencyRateService).save(currencyRate);
    }

    private CurrencyRate currencyRateWithDateAndCharCode(LocalDate date, String charCode) {
        return CurrencyRate.builder()
                .date(date)
                .charCode(charCode)
                .build();
    }

    @Test
    void tryRefreshCurrencyRatesForTodayIfTableHasRate() {
        final var currencyRate = currencyRateWithDateAndCharCode(LocalDate.now(), CurrencyUnit.USD.getCode());
        final List<CurrencyRate> currencyRates = Collections.singletonList(currencyRate);
        when(currencyRatesParser.parseFromCBRF()).thenReturn(currencyRates);
        when(currencyRateService.isExist(currencyRate.getDate(), currencyRate.getCharCode()))
                .thenReturn(true);

        currencyService.tryRefreshCurrencyRatesForToday();

        verify(currencyRatesParser).parseFromCBRF();
        verify(currencyRateService).isExist(currencyRate.getDate(), currencyRate.getCharCode());
        verify(currencyRateService, never()).save(currencyRate);
    }

    @Test
    void tryRefreshCurrencyRatesForTodayMustThrowExceptionIfParserThrowAnyException() {
        when(currencyRatesParser.parseFromCBRF()).thenThrow(RuntimeException.class);
        assertThatThrownBy(() -> currencyService.tryRefreshCurrencyRatesForToday())
                .isInstanceOf(CurrencyRateGettingException.class);
    }

    @Test
    void refreshCurrencyRatesForTodayBySchedulerIfRefreshEnabled() {
        KappersProperties.CurrencyRates currencyRatesProperties = mock(KappersProperties.CurrencyRates.class);
        when(kappersProperties.getCurrencyRates()).thenReturn(currencyRatesProperties);
        when(currencyRatesProperties.isRefreshCronEnabled()).thenReturn(true);
        currencyService = spy(currencyService);

        currencyService.refreshCurrencyRatesForTodayByScheduler();

        verify(currencyService).tryRefreshCurrencyRatesForToday();
    }

    @Test
    void refreshCurrencyRatesForTodayBySchedulerIfRefreshDisabled() {
        KappersProperties.CurrencyRates currencyRatesProperties = mock(KappersProperties.CurrencyRates.class);
        when(kappersProperties.getCurrencyRates()).thenReturn(currencyRatesProperties);
        when(currencyRatesProperties.isRefreshCronEnabled()).thenReturn(false);
        currencyService = spy(currencyService);

        currencyService.refreshCurrencyRatesForTodayByScheduler();

        verify(currencyService, never()).tryRefreshCurrencyRatesForToday();
    }

    @Test
    void refreshCurrencyRatesForTodayBySchedulerIfRefreshEnabledAndFail() {
        KappersProperties.CurrencyRates currencyRatesProperties = mock(KappersProperties.CurrencyRates.class);
        when(kappersProperties.getCurrencyRates()).thenReturn(currencyRatesProperties);
        when(currencyRatesProperties.isRefreshCronEnabled()).thenReturn(true);
        currencyService = spy(currencyService);
        doThrow(RuntimeException.class).when(currencyService).tryRefreshCurrencyRatesForToday();

        currencyService.refreshCurrencyRatesForTodayByScheduler();

        verify(currencyService).tryRefreshCurrencyRatesForToday();
    }

    @Test
    void getActualCurrencyRateDateMustReturnDateFromParameterIfBothCurrencyExists() {
        final LocalDate date = LocalDate.now();
        final String sourceCode = CurrencyUnit.EUR.getCode();
        final String targetCode = CurrencyUnit.USD.getCode();
        final boolean currRatesGotToday = true;
        when(currencyRateService.isExist(date, sourceCode)).thenReturn(true);
        when(currencyRateService.isExist(date, targetCode)).thenReturn(true);
        currencyService = spy(currencyService);

        final LocalDate result = currencyService.getActualCurrencyRateDate(date, sourceCode, targetCode, currRatesGotToday);

        assertThat(result).isEqualTo(date);
        verify(currencyRateService).isExist(date, sourceCode);
        verify(currencyRateService).isExist(date, targetCode);
        verify(currencyService, never()).tryRefreshCurrencyRatesForToday();
    }

    @Test
    void getActualCurrencyRateDateMustThrowExceptionIfCurrencyNotExistsAndRefreshFail() {
        final LocalDate date = LocalDate.now();
        final String sourceCode = CurrencyUnit.EUR.getCode();
        final String targetCode = CurrencyUnit.USD.getCode();
        final boolean currRatesGotToday = false;
        when(currencyRateService.isExist(date, sourceCode)).thenReturn(false);
        currencyService = spy(currencyService);
        doThrow(CurrencyRateGettingException.class).when(currencyService).tryRefreshCurrencyRatesForToday();

        assertThatThrownBy(() -> currencyService.getActualCurrencyRateDate(date, sourceCode, targetCode, currRatesGotToday))
                .isInstanceOf(CurrencyRateGettingException.class);

        verify(currencyRateService).isExist(date, sourceCode);
        verify(currencyRateService, never()).isExist(date, targetCode);
        verify(currencyService).tryRefreshCurrencyRatesForToday();
    }

    @Test
    void getActualCurrencyRateDateMustReturnDateFromParameterIfCurrencyNotExistsAndRefreshSuccess() {
        final LocalDate now = LocalDate.now();
        final LocalDate date2 = now.minusDays(1);
        final String sourceCode = CurrencyUnit.EUR.getCode();
        final String targetCode = CurrencyUnit.USD.getCode();
        final boolean currRatesGotToday = false;
        when(currencyRateService.isExist(now, sourceCode)).thenReturn(false);
        when(currencyRateService.isExist(date2, sourceCode)).thenReturn(true);
        when(currencyRateService.isExist(date2, targetCode)).thenReturn(true);
        currencyService = spy(currencyService);

        final LocalDate result = currencyService.getActualCurrencyRateDate(now, sourceCode, targetCode, currRatesGotToday);

        assertThat(result).isEqualTo(date2);
        verify(currencyRateService, times(2)).isExist(now, sourceCode);
        verify(currencyRateService, never()).isExist(now, targetCode);
        verify(currencyRateService).isExist(date2, sourceCode);
        verify(currencyRateService).isExist(date2, targetCode);
        verify(currencyService).tryRefreshCurrencyRatesForToday();
        verify(currencyService).getActualCurrencyRateDate(date2, sourceCode, targetCode, true);
        verify(currencyService, times(2)).getActualCurrencyRateDate(any(), eq(sourceCode), eq(targetCode), anyBoolean());
    }

    @Test
    void exchangeWithCurrencyUnitParameterTypes() {
        final var source = CurrencyUnit.EUR;
        final var target = CurrencyUnit.USD;
        final BigDecimal sourceAmount = BigDecimal.ONE;
        final BigDecimal expectedValue = new BigDecimal("1.35");
        currencyService = spy(currencyService);
        doReturn(expectedValue).when(currencyService).exchange(source.getCode(), sourceAmount, target.getCode());

        final BigDecimal result = currencyService.exchange(source, sourceAmount, target);

        assertThat(result).isEqualTo(expectedValue);
        verify(currencyService).exchange(source.getCode(), sourceAmount, target.getCode());
    }

    @Test
    void exchangeStringCurrencyParametersAreEqual() {
        final String sourceCode = CurrencyUnit.EUR.getCode();
        final BigDecimal sourceAmount = BigDecimal.TEN;
        currencyService = spy(currencyService);

        final BigDecimal result = currencyService.exchange(sourceCode, sourceAmount, sourceCode);

        assertThat(result).isEqualTo(sourceAmount);
        verify(currencyService, never()).getActualCurrencyRateDate(any(), any(), any(), anyBoolean());
        verify(currencyRateService, never()).currencyRateByDate(any(), any());
    }

    @Test
    void exchangeOneEURToUSD() {
        final LocalDate date = LocalDate.now();
        final String sourceCode = CurrencyUnit.EUR.getCode();
        final String targetCode = CurrencyUnit.USD.getCode();
        final BigDecimal sourceAmount = BigDecimal.ONE;
        final CurrencyRate sourceCurrencyRate = currencyRateWithValue(new BigDecimal("72.6993"));
        final CurrencyRate targetCurrencyRate = currencyRateWithValue(new BigDecimal("64.4326"));
        when(currencyRateService.currencyRateByDate(date, sourceCode)).thenReturn(sourceCurrencyRate);
        when(currencyRateService.currencyRateByDate(date, targetCode)).thenReturn(targetCurrencyRate);
        currencyService = spy(currencyService);
        doReturn(date).when(currencyService).getActualCurrencyRateDate(any(), eq(sourceCode), eq(targetCode), eq(false));

        final BigDecimal result = currencyService.exchange(sourceCode, sourceAmount, targetCode);

        assertThat(result).isEqualTo(sourceAmount.multiply(new BigDecimal("1.1283")));
        verify(currencyService).getActualCurrencyRateDate(any(), eq(sourceCode), eq(targetCode), eq(false));
        verify(currencyRateService).currencyRateByDate(date, sourceCode);
        verify(currencyRateService).currencyRateByDate(date, targetCode);
    }

    private CurrencyRate currencyRateWithValue(BigDecimal value) {
        return CurrencyRate.builder()
                .value(value)
                .nominal(1)
                .build();
    }

    @Test
    void exchangeOneEURToRUB() {
        final LocalDate date = LocalDate.now();
        final String sourceCode = CurrencyUnit.EUR.getCode();
        final String targetCode = kappersProperties.getRubCurrencyCode();
        final BigDecimal sourceAmount = BigDecimal.ONE;
        final CurrencyRate sourceCurrencyRate = currencyRateWithValue(new BigDecimal("72.6993"));
        when(currencyRateService.currencyRateByDate(date, sourceCode)).thenReturn(sourceCurrencyRate);
        currencyService = spy(currencyService);
        doReturn(date).when(currencyService).getActualCurrencyRateDate(any(), eq(sourceCode), eq(targetCode), eq(false));

        final BigDecimal result = currencyService.exchange(sourceCode, sourceAmount, targetCode);

        assertThat(result).isEqualTo(sourceAmount.multiply(sourceCurrencyRate.getValue()));
        verify(currencyService).getActualCurrencyRateDate(any(), eq(sourceCode), eq(targetCode), eq(false));
        verify(currencyRateService).currencyRateByDate(date, sourceCode);
        verify(currencyRateService, never()).currencyRateByDate(date, targetCode);
    }

    @Test
    void exchangeOneRUBToUSD() {
        final LocalDate date = LocalDate.now();
        final String sourceCode = kappersProperties.getRubCurrencyCode();
        final String targetCode = CurrencyUnit.USD.getCode();
        final BigDecimal sourceAmount = BigDecimal.ONE;
        final CurrencyRate targetCurrencyRate = currencyRateWithValue(new BigDecimal("64.4326"));
        when(currencyRateService.currencyRateByDate(date, targetCode)).thenReturn(targetCurrencyRate);
        currencyService = spy(currencyService);
        doReturn(date).when(currencyService).getActualCurrencyRateDate(any(), eq(sourceCode), eq(targetCode), eq(false));

        final BigDecimal result = currencyService.exchange(sourceCode, sourceAmount, targetCode);

        assertThat(result).isEqualTo(sourceAmount.divide(targetCurrencyRate.getValue(), kappersProperties.getBigDecimalRoundingMode()));
        verify(currencyService).getActualCurrencyRateDate(any(), eq(sourceCode), eq(targetCode), eq(false));
        verify(currencyRateService, never()).currencyRateByDate(date, sourceCode);
        verify(currencyRateService).currencyRateByDate(date, targetCode);
    }
}