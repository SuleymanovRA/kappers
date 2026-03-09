package ru.kappers.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.joda.money.CurrencyUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kappers.config.KappersProperties;
import ru.kappers.exceptions.CurrencyRateGettingException;
import ru.kappers.model.CurrencyRate;
import ru.kappers.service.CurrencyRateService;
import ru.kappers.service.CurrencyService;
import ru.kappers.service.MessageTranslator;
import ru.kappers.service.parser.CBRFDailyCurrencyRatesParser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Реализация сервиса валют
 */
@Slf4j
@Service
@Transactional
public class CurrencyServiceImpl implements CurrencyService {
    private final CurrencyRateService currencyRateService;
    private final CBRFDailyCurrencyRatesParser currencyRatesParser;
    private final KappersProperties kappersProperties;
    private final MessageTranslator translator;

    @Autowired
    public CurrencyServiceImpl(CurrencyRateService currencyRateService, CBRFDailyCurrencyRatesParser currencyRatesParser,
                               KappersProperties kappersProperties, MessageTranslator translator) {
        this.currencyRateService = currencyRateService;
        this.currencyRatesParser = currencyRatesParser;
        this.kappersProperties = kappersProperties;
        this.translator = translator;
    }

    @Override
    public void tryRefreshCurrencyRatesForToday() {
        log.debug("tryRefreshCurrencyRatesForToday()...");
        try {
            refreshCurrencyRatesForToday();
        } catch (Exception ex) {
            final String msg = translator.byCode("currencyRates.refreshFailed", ex.getMessage());
            log.error(msg, ex);
            throw new CurrencyRateGettingException(msg, ex);
        }
    }

    private void refreshCurrencyRatesForToday() {
        log.info(translator.byCode("currencyRates.refreshBegin"));
        final List<CurrencyRate> currencyRates = currencyRatesParser.parseFromCBRF();
        currencyRates.stream()
                .filter(currencyRate -> !currencyRateService.isExist(currencyRate.getDate(), currencyRate.getCharCode()))
                .forEach(currencyRateService::save);
        log.info(translator.byCode("currencyRates.refreshEnd"));
    }

    /**
     * Обновить курсы валют на сегодня (по расписанию планировщика)
     */
    @Scheduled(cron = "${kappers.currency-rates.refresh-cron}")
    public void refreshCurrencyRatesForTodayByScheduler() {
        if (!kappersProperties.getCurrencyRates().isRefreshCronEnabled()) {
            log.debug("disabled call refreshCurrencyRatesForTodayByScheduler()");
            return;
        }
        log.debug("refreshCurrencyRatesForTodayByScheduler()...");
        // метод вызываемый планировщиком не должен выбрасывать исключения, иначе возможно не сработает по расписанию
        try {
            tryRefreshCurrencyRatesForToday();
        } catch (Exception e) {
            log.error("refreshCurrencyRatesForTodayByScheduler() error", e);
        }
    }

    @Override
    public BigDecimal exchange(CurrencyUnit sourceCurrency, CurrencyUnit targetCurrency, BigDecimal sourceAmount) {
        return exchange(sourceCurrency.getCode(), targetCurrency.getCode(), sourceAmount);
    }

    @Override
    public BigDecimal exchange(String sourceCurrency, String targetCurrency, BigDecimal sourceAmount) {
        log.debug("exchange(sourceCurrency: {}, targetCurrency: {}, sourceAmount: {})...",
                sourceCurrency, targetCurrency, sourceAmount);
        if (sourceCurrency.equals(targetCurrency)) {
            return sourceAmount;
        }
        LocalDate date = getActualCurrencyRateDate(LocalDate.now(), sourceCurrency, targetCurrency, false);
        if (isRubCurrency(sourceCurrency)) {
            return amountInTargetCurrencyFromRub(targetCurrency, sourceAmount, date);
        } else if (isRubCurrency(targetCurrency)) {
            return amountInRubFromSourceCurrency(sourceCurrency, sourceAmount, date);
        }
        BigDecimal amountInRub = amountInRubFromSourceCurrency(sourceCurrency, sourceAmount, date);
        return amountInTargetCurrencyFromRub(targetCurrency, amountInRub, date);
    }

    private boolean isRubCurrency(String sourceCurrency) {
        return sourceCurrency.equals(kappersProperties.getRubCurrencyCode());
    }

    @NotNull
    private BigDecimal amountInTargetCurrencyFromRub(String targetCurrency, BigDecimal amountInRub, LocalDate date) {
        CurrencyRate targetCurrencyRate = currencyRateService.currencyRateByDate(date, targetCurrency);
        return amountInRub.divide(targetCurrencyRate.getValue(), kappersProperties.getBigDecimalRoundingMode())
                .multiply(BigDecimal.valueOf(targetCurrencyRate.getNominal()));
    }

    @NotNull
    private BigDecimal amountInRubFromSourceCurrency(String sourceCurrency, BigDecimal sourceAmount, LocalDate date) {
        CurrencyRate sourceCurrencyRate = currencyRateService.currencyRateByDate(date, sourceCurrency);
        return sourceAmount.multiply(sourceCurrencyRate.getValue())
                .multiply(BigDecimal.valueOf(sourceCurrencyRate.getNominal()));
    }

    public LocalDate getActualCurrencyRateDate(LocalDate date, String sourceCurrency, String targetCurrency, boolean currRatesGotToday) {
        log.debug("getActualCurrencyRateDate(date: {}, sourceCurrency: {}, targetCurrency: {}, currRatesGotToday: {})...",
                date, sourceCurrency, targetCurrency, currRatesGotToday);
        boolean todaysCurrRatesGot = currRatesGotToday;
        if (currencyRateService.isExist(date, sourceCurrency) && currencyRateService.isExist(date, targetCurrency))
            return date;
        else {
            if (!todaysCurrRatesGot) {
                tryRefreshCurrencyRatesForToday();
                todaysCurrRatesGot = true;
            }
            if (!currencyRateService.isExist(date, sourceCurrency) || !currencyRateService.isExist(date, targetCurrency)) {
                LocalDate localDate = date.minusDays(1);
                date = getActualCurrencyRateDate(localDate, sourceCurrency, targetCurrency, todaysCurrRatesGot);
            }
        }
        return date;
    }
}

