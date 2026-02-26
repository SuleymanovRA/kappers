package ru.kappers.service;

import org.joda.money.CurrencyUnit;
import ru.kappers.exceptions.CurrencyRateGettingException;

import java.math.BigDecimal;

/**
 * Интерфейс сервиса валют
 */
public interface CurrencyService {
    /**
     * Обновить курсы валют на сегодня
     * @throws CurrencyRateGettingException если не удалось обновить курсы валют
     */
    void tryRefreshCurrencyRatesForToday();

    /**
     * Конвертировать сумму из исходной валюты в целевую
     * @param fromCurr исходная валюта
     * @param toCurr целевая валюта
     * @param amount сумма в исходной валюте
     * @return сумма в целевой валюте
     */
    BigDecimal exchange(CurrencyUnit fromCurr, CurrencyUnit toCurr, BigDecimal amount);

    /**
     * Конвертировать сумму из исходной валюты в целевую
     * @param fromCurr исходная валюта
     * @param toCurr целевая валюта
     * @param amount сумма в исходной валюте
     * @return сумма в целевой валюте
     */
    BigDecimal exchange(String fromCurr, String toCurr, BigDecimal amount);
}
