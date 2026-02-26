package ru.kappers.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.joda.money.CurrencyUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kappers.config.KappersProperties;
import ru.kappers.exceptions.CurrRateGettingException;
import ru.kappers.model.CurrencyRate;
import ru.kappers.service.CurrRateService;
import ru.kappers.service.CurrencyService;
import ru.kappers.service.MessageTranslator;
import ru.kappers.service.parser.CBRFDailyCurrencyRatesParser;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

/**
 * Реализация сервиса валют
 */
@Slf4j
@Service
@Transactional
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrRateService currRateService;
    private final CBRFDailyCurrencyRatesParser currencyRatesParser;
    private final KappersProperties kappersProperties;
    private final MessageTranslator translator;

    @Autowired
    public CurrencyServiceImpl(CurrRateService currRateService, CBRFDailyCurrencyRatesParser currencyRatesParser, KappersProperties kappersProperties, MessageTranslator translator) {
        this.currRateService = currRateService;
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
            throw new CurrRateGettingException(msg, ex);
        }
    }

    private void refreshCurrencyRatesForToday() {
        log.info(translator.byCode("currencyRates.refreshBegin"));
        final List<CurrencyRate> currencyRates = currencyRatesParser.parseFromCBRF();
        currencyRates.stream()
                .filter(currencyRate -> !currRateService.isExist(currencyRate.getDate(), currencyRate.getCharCode()))
                .forEach(currRateService::save);
        log.info(translator.byCode("currencyRates.refreshEnd"));
    }

    /**
     * Обновить курсы валют на сегодня (по расписанию планировщика)
     */
    @Scheduled(cron = "${kappers.currency-rates.refresh-cron}")
    public void refreshCurrencyRatesForTodayByScheduler() {
        if (kappersProperties.getCurrencyRates().isRefreshCronEnabled()) {
            log.debug("refreshCurrencyRatesForTodayByScheduler()...");
        } else {
            log.debug("disabled call refreshCurrencyRatesForTodayByScheduler()");
            return;
        }
        // метод вызываемый планировщиком не должен выбрасывать исключения, иначе возможно не сработает по расписанию
        try {
            tryRefreshCurrencyRatesForToday();
        } catch (Exception e) {
            log.error("refreshCurrencyRatesForTodayByScheduler() error", e);
        }
    }

    public LocalDate getActualCurrencyRateDate(LocalDate date, String fromCurr, String toCurr, boolean currRatesGotToday) {
        log.debug("getActualCurrencyRateDate(date: {}, fromCurr: {}, toCurr: {}, currRatesGotToday: {})...",
                date, fromCurr, toCurr, currRatesGotToday);
        boolean todaysCurrRatesGot = currRatesGotToday;
        if (currRateService.isExist(date, fromCurr) && currRateService.isExist(date, toCurr))
            return date;
        else {
            if (!todaysCurrRatesGot) {
                tryRefreshCurrencyRatesForToday();
                todaysCurrRatesGot = true;
            }
            if (!currRateService.isExist(date, fromCurr) || !currRateService.isExist(date, toCurr)) {
                LocalDate localDate = date.minusDays(1);
                date = getActualCurrencyRateDate(localDate, fromCurr, toCurr, todaysCurrRatesGot);
            }
        }
        return date;
    }

    @Override
    public BigDecimal exchange(CurrencyUnit fromCurr, CurrencyUnit toCurr, BigDecimal amount) {
        return exchange(fromCurr.getCode(), toCurr.getCode(), amount);
    }

    @Override
    public BigDecimal exchange(String fromCurr, String toCurr, BigDecimal amount) {
        log.debug("exchange(fromCurr: {}, toCurr: {}, amount: {})...", fromCurr, toCurr, amount);
        if (fromCurr.equals(toCurr)) {
            return amount;
        }
        LocalDate date = getActualCurrencyRateDate(LocalDate.now(), fromCurr, toCurr, false);
        final RoundingMode roundingMode = kappersProperties.getBigDecimalRoundingMode();
        final String rubCurrencyCode = kappersProperties.getRubCurrencyCode();
        if (fromCurr.equals(rubCurrencyCode)) {
            CurrencyRate rate = currRateService.getCurrByDate(date, toCurr);
            return amount.divide(rate.getValue(), roundingMode)
                    .multiply(BigDecimal.valueOf(rate.getNominal()));
        } else if (toCurr.equals(rubCurrencyCode)) {
            CurrencyRate rate = currRateService.getCurrByDate(date, fromCurr);
            return amount.multiply(rate.getValue())
                    .multiply(BigDecimal.valueOf(rate.getNominal()));
        }
        CurrencyRate from = currRateService.getCurrByDate(date, fromCurr);
        CurrencyRate to = currRateService.getCurrByDate(date, toCurr);
        BigDecimal amountInRub = amount.multiply(from.getValue())
                .multiply(BigDecimal.valueOf(from.getNominal()));
        return amountInRub.divide(to.getValue(), roundingMode)
                .multiply(BigDecimal.valueOf(to.getNominal()));
    }

}

