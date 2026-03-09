package ru.kappers.service;

import org.joda.money.CurrencyUnit;
import ru.kappers.exceptions.CurrencyRateGettingException;

import java.math.BigDecimal;

/**
 * Интерфейс сервиса валют
 */
public interface CurrencyService {
    /**
     * Попробовать обновить курсы валют на сегодня
     * @throws CurrencyRateGettingException если не удалось обновить курсы валют
     */
    void tryRefreshCurrencyRatesForToday();

    /**
     * Конвертировать сумму из исходной валюты в целевую
     * @param sourceCurrency исходная валюта
     * @param sourceAmount   сумма в исходной валюте
     * @param targetCurrency целевая валюта
     * @return сумма в целевой валюте
     */
    BigDecimal exchange(CurrencyUnit sourceCurrency, BigDecimal sourceAmount, CurrencyUnit targetCurrency);

    /**
     * Конвертировать сумму из исходной валюты в целевую
     * @param sourceCurrency исходная валюта
     * @param sourceAmount   сумма в исходной валюте
     * @param targetCurrency целевая валюта
     * @return сумма в целевой валюте
     */
    BigDecimal exchange(String sourceCurrency, BigDecimal sourceAmount, String targetCurrency);
}
