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
     * @param targetCurrency целевая валюта
     * @param sourceAmount сумма в исходной валюте
     * @return сумма в целевой валюте
     */
    BigDecimal exchange(CurrencyUnit sourceCurrency, CurrencyUnit targetCurrency, BigDecimal sourceAmount);

    /**
     * Конвертировать сумму из исходной валюты в целевую
     * @param sourceCurrency исходная валюта
     * @param targetCurrency целевая валюта
     * @param sourceAmount сумма в исходной валюте
     * @return сумма в целевой валюте
     */
    BigDecimal exchange(String sourceCurrency, String targetCurrency, BigDecimal sourceAmount);
}
