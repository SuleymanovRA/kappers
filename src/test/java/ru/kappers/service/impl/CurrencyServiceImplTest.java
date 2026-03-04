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

public class CurrencyServiceImplTest extends UnitTest {
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
    public void refreshCurrencyRatesForTodayMustSaveRateIfTableHasNotRate() {
        final LocalDate currentDate = LocalDate.now();
        final String usdCharCode = CurrencyUnit.USD.getCode();
        final CurrencyRate currencyRate = currencyRateWithDateAndCharCode(currentDate, usdCharCode);
        final List<CurrencyRate> currencyRates = Collections.singletonList(currencyRate);
        when(currencyRatesParser.parseFromCBRF()).thenReturn(currencyRates);
        when(currencyRateService.isExist(currentDate, usdCharCode)).thenReturn(false);

        currencyService.tryRefreshCurrencyRatesForToday();

        verify(currencyRatesParser).parseFromCBRF();
        verify(currencyRateService).isExist(currentDate, usdCharCode);
        verify(currencyRateService).save(currencyRate);
    }

    private CurrencyRate currencyRateWithDateAndCharCode(LocalDate date, String charCode) {
        return CurrencyRate.builder()
                .date(date)
                .charCode(charCode)
                .build();
    }

    @Test
    public void refreshCurrencyRatesForTodayIfTableHasRate() {
        final LocalDate currentDate = LocalDate.now();
        final String usdCharCode = CurrencyUnit.USD.getCode();
        final CurrencyRate currencyRate = currencyRateWithDateAndCharCode(currentDate, usdCharCode);
        final List<CurrencyRate> currencyRates = Collections.singletonList(currencyRate);
        when(currencyRatesParser.parseFromCBRF()).thenReturn(currencyRates);
        when(currencyRateService.isExist(currentDate, usdCharCode)).thenReturn(true);

        currencyService.tryRefreshCurrencyRatesForToday();

        verify(currencyRatesParser).parseFromCBRF();
        verify(currencyRateService).isExist(currentDate, usdCharCode);
        verify(currencyRateService, never()).save(currencyRate);
    }

    @Test
    public void refreshCurrencyRatesForTodayMustThrowExceptionIfParserThrowAnyException() {
        when(currencyRatesParser.parseFromCBRF()).thenThrow(RuntimeException.class);
        assertThatThrownBy(() -> currencyService.tryRefreshCurrencyRatesForToday())
                .isInstanceOf(CurrencyRateGettingException.class);
    }

    @Test
    public void refreshCurrencyRatesForTodayBySchedulerIfRefreshEnabled() {
        KappersProperties.CurrencyRates currencyRatesProp = mock(KappersProperties.CurrencyRates.class);
        when(kappersProperties.getCurrencyRates()).thenReturn(currencyRatesProp);
        when(currencyRatesProp.isRefreshCronEnabled()).thenReturn(true);
        currencyService = spy(currencyService);

        currencyService.refreshCurrencyRatesForTodayByScheduler();

        verify(currencyService).tryRefreshCurrencyRatesForToday();
    }

    @Test
    public void refreshCurrencyRatesForTodayBySchedulerIfRefreshDisabled() {
        KappersProperties.CurrencyRates currencyRatesProp = mock(KappersProperties.CurrencyRates.class);
        when(kappersProperties.getCurrencyRates()).thenReturn(currencyRatesProp);
        when(currencyRatesProp.isRefreshCronEnabled()).thenReturn(false);
        currencyService = spy(currencyService);

        currencyService.refreshCurrencyRatesForTodayByScheduler();

        verify(currencyService, never()).tryRefreshCurrencyRatesForToday();
    }

    @Test
    public void refreshCurrencyRatesForTodayBySchedulerIfRefreshEnabledAndFail() {
        KappersProperties.CurrencyRates currencyRatesProp = mock(KappersProperties.CurrencyRates.class);
        when(kappersProperties.getCurrencyRates()).thenReturn(currencyRatesProp);
        when(currencyRatesProp.isRefreshCronEnabled()).thenReturn(true);
        currencyService = spy(currencyService);
        doThrow(RuntimeException.class).when(currencyService).tryRefreshCurrencyRatesForToday();

        currencyService.refreshCurrencyRatesForTodayByScheduler();

        verify(currencyService).tryRefreshCurrencyRatesForToday();
    }

    @Test
    public void getActualCurrencyRateDateMustReturnDateFromParameterIfBothCurrencyExists() {
        final LocalDate date = LocalDate.now();
        final String source = CurrencyUnit.EUR.getCode();
        final String target = CurrencyUnit.USD.getCode();
        final boolean currRatesGotToday = true;
        when(currencyRateService.isExist(date, source)).thenReturn(true);
        when(currencyRateService.isExist(date, target)).thenReturn(true);
        currencyService = spy(currencyService);

        final LocalDate result = currencyService.getActualCurrencyRateDate(date, source, target, currRatesGotToday);

        assertThat(result).isEqualTo(date);
        verify(currencyRateService).isExist(date, source);
        verify(currencyRateService).isExist(date, target);
        verify(currencyService, never()).tryRefreshCurrencyRatesForToday();
    }

    @Test
    public void getActualCurrencyRateDateMustThrowExceptionIfCurrencyNotExistsAndRefreshFail() {
        final LocalDate date = LocalDate.now();
        final String source = CurrencyUnit.EUR.getCode();
        final String target = CurrencyUnit.USD.getCode();
        final boolean currRatesGotToday = false;
        when(currencyRateService.isExist(date, source)).thenReturn(false);
        currencyService = spy(currencyService);
        doThrow(CurrencyRateGettingException.class).when(currencyService).tryRefreshCurrencyRatesForToday();

        assertThatThrownBy(() -> currencyService.getActualCurrencyRateDate(date, source, target, currRatesGotToday))
                .isInstanceOf(CurrencyRateGettingException.class);

        verify(currencyRateService).isExist(date, source);
        verify(currencyRateService, never()).isExist(date, target);
        verify(currencyService).tryRefreshCurrencyRatesForToday();
    }

    @Test
    public void getActualCurrencyRateDateMustReturnDateFromParameterIfCurrencyNotExistsAndRefreshSuccess() {
        final LocalDate now = LocalDate.now();
        final LocalDate date2 = now.minusDays(1);
        final String source = CurrencyUnit.EUR.getCode();
        final String target = CurrencyUnit.USD.getCode();
        final boolean currRatesGotToday = false;
        when(currencyRateService.isExist(now, source)).thenReturn(false);
        when(currencyRateService.isExist(date2, source)).thenReturn(true);
        when(currencyRateService.isExist(date2, target)).thenReturn(true);
        currencyService = spy(currencyService);

        final LocalDate result = currencyService.getActualCurrencyRateDate(now, source, target, currRatesGotToday);

        assertThat(result).isEqualTo(date2);
        verify(currencyRateService, times(2)).isExist(now, source);
        verify(currencyRateService, never()).isExist(now, target);
        verify(currencyRateService).isExist(date2, source);
        verify(currencyRateService).isExist(date2, target);
        verify(currencyService).tryRefreshCurrencyRatesForToday();
        verify(currencyService).getActualCurrencyRateDate(date2, source, target, true);
        verify(currencyService, times(2)).getActualCurrencyRateDate(any(), eq(source), eq(target), anyBoolean());
    }

    @Test
    public void exchangeWithCurrencyUnitParameterTypes() {
        final var source = CurrencyUnit.EUR;
        final var target = CurrencyUnit.USD;
        final BigDecimal amount = BigDecimal.ONE;
        final BigDecimal expectedValue = new BigDecimal("1.35");
        currencyService = spy(currencyService);
        doReturn(expectedValue).when(currencyService).exchange(source.getCode(), target.getCode(), amount);

        final BigDecimal result = currencyService.exchange(source, target, amount);

        assertThat(result).isEqualTo(expectedValue);
        verify(currencyService).exchange(source.getCode(), target.getCode(), amount);
    }

    @Test
    public void exchangeStringCurrencyParametersAreEqual() {
        final String source = CurrencyUnit.EUR.getCode();
        final BigDecimal amount = BigDecimal.TEN;
        currencyService = spy(currencyService);

        final BigDecimal result = currencyService.exchange(source, source, amount);

        assertThat(result).isEqualTo(amount);
        verify(currencyService, never()).getActualCurrencyRateDate(any(), any(), any(), anyBoolean());
        verify(currencyRateService, never()).getCurrByDate(any(), any());
    }

    @Test
    public void exchangeOneEURToUSD() {
        final LocalDate date = LocalDate.now();
        final String source = CurrencyUnit.EUR.getCode();
        final String target = CurrencyUnit.USD.getCode();
        final BigDecimal amount = BigDecimal.ONE;
        final CurrencyRate crFrom = currencyRateWithValue(new BigDecimal("72.6993"));
        final CurrencyRate crTo = currencyRateWithValue(new BigDecimal("64.4326"));
        when(currencyRateService.getCurrByDate(date, source)).thenReturn(crFrom);
        when(currencyRateService.getCurrByDate(date, target)).thenReturn(crTo);
        currencyService = spy(currencyService);
        doReturn(date).when(currencyService).getActualCurrencyRateDate(any(), eq(source), eq(target), eq(false));

        final BigDecimal result = currencyService.exchange(source, target, amount);

        assertThat(result).isEqualTo(amount.multiply(new BigDecimal("1.1283")));
        verify(currencyService).getActualCurrencyRateDate(any(), eq(source), eq(target), eq(false));
        verify(currencyRateService).getCurrByDate(date, source);
        verify(currencyRateService).getCurrByDate(date, target);
    }

    private CurrencyRate currencyRateWithValue(BigDecimal value) {
        return CurrencyRate.builder()
                .value(value)
                .nominal(1)
                .build();
    }

    @Test
    public void exchangeOneEURToRUB() {
        final LocalDate date = LocalDate.now();
        final String source = CurrencyUnit.EUR.getCode();
        final String target = kappersProperties.getRubCurrencyCode();
        final BigDecimal amount = BigDecimal.ONE;
        final CurrencyRate crFrom = currencyRateWithValue(new BigDecimal("72.6993"));
        when(currencyRateService.getCurrByDate(date, source)).thenReturn(crFrom);
        currencyService = spy(currencyService);
        doReturn(date).when(currencyService).getActualCurrencyRateDate(any(), eq(source), eq(target), eq(false));

        final BigDecimal result = currencyService.exchange(source, target, amount);

        assertThat(result).isEqualTo(amount.multiply(crFrom.getValue()));
        verify(currencyService).getActualCurrencyRateDate(any(), eq(source), eq(target), eq(false));
        verify(currencyRateService).getCurrByDate(date, source);
        verify(currencyRateService, never()).getCurrByDate(date, target);
    }

    @Test
    public void exchangeOneRUBToUSD() {
        final LocalDate date = LocalDate.now();
        final String source = kappersProperties.getRubCurrencyCode();
        final String target = CurrencyUnit.USD.getCode();
        final BigDecimal amount = BigDecimal.ONE;
        final CurrencyRate crTo = currencyRateWithValue(new BigDecimal("64.4326"));
        when(currencyRateService.getCurrByDate(date, target)).thenReturn(crTo);
        currencyService = spy(currencyService);
        doReturn(date).when(currencyService).getActualCurrencyRateDate(any(), eq(source), eq(target), eq(false));

        final BigDecimal result = currencyService.exchange(source, target, amount);

        assertThat(result).isEqualTo(amount.divide(crTo.getValue(), kappersProperties.getBigDecimalRoundingMode()));
        verify(currencyService).getActualCurrencyRateDate(any(), eq(source), eq(target), eq(false));
        verify(currencyRateService, never()).getCurrByDate(date, source);
        verify(currencyRateService).getCurrByDate(date, target);
    }
}