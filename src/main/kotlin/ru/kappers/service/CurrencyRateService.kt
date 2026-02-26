package ru.kappers.service

import ru.kappers.model.CurrencyRate
import java.time.LocalDate

/**
 * Интерфейс сервиса курсов валют
 */
interface CurrencyRateService {
    fun save(rate: CurrencyRate): CurrencyRate
    fun isExist(date: LocalDate, currLiteral: String): Boolean
    fun getCurrByDate(date: LocalDate, currLiteral: String): CurrencyRate?
    fun clear()
    fun update(rate: CurrencyRate): CurrencyRate
    fun getToday(literal: String): CurrencyRate?
    fun getAllToday(): MutableList<CurrencyRate>
    fun getAllByDate(date: LocalDate): MutableList<CurrencyRate>
}