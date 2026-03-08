package ru.kappers.service

import ru.kappers.model.CurrencyRate
import java.time.LocalDate

/**
 * Интерфейс сервиса курсов валют
 */
interface CurrencyRateService {
    fun save(rate: CurrencyRate): CurrencyRate
    fun isExist(date: LocalDate, currencyLiteral: String): Boolean
    fun currencyRateByDate(date: LocalDate, currencyLiteral: String): CurrencyRate?
    fun clear()
    fun update(rate: CurrencyRate): CurrencyRate
    fun currencyRateToday(currencyLiteral: String): CurrencyRate?
    fun allCurrencyRatesToday(): MutableList<CurrencyRate>
    fun allCurrencyRatesByDate(date: LocalDate): MutableList<CurrencyRate>
}