package ru.kappers.service.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.kappers.config.KappersProperties
import ru.kappers.model.CurrencyRate
import ru.kappers.repository.CurrRateRepository
import ru.kappers.service.CurrencyRateService
import java.time.LocalDate

@Service
@Transactional
open class CurrencyRateServiceImpl(private val repository: CurrRateRepository,
                                   private val kappersProperties: KappersProperties): CurrencyRateService {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun save(rate: CurrencyRate): CurrencyRate {
        log.debug("save(rate: {})...", rate)
        return update(rate)
    }

    @Transactional(readOnly = true)
    override fun isExist(date: LocalDate, currencyLiteral: String): Boolean {
        log.debug("isExist(date: {}, currencyLiteral: {})...", date, currencyLiteral)
        return currencyLiteral == kappersProperties.rubCurrencyCode || currencyRateByDate(date, currencyLiteral) != null
    }

    @Transactional(readOnly = true)
    override fun currencyRateByDate(date: LocalDate, currencyLiteral: String): CurrencyRate? {
        log.debug("currencyRateByDate(date: {}, currencyLiteral: {})...", date, currencyLiteral)
        return repository.getCurrencyRateByDateAndCharCode(date, currencyLiteral).orElse(null)
    }

    override fun clear() {
        log.debug("clear()...")
        repository.deleteAll()
    }

    override fun update(rate: CurrencyRate): CurrencyRate {
        log.debug("update(rate: {})...", rate)
        val cRate = currencyRateByDate(rate.date, rate.charCode)
        if (cRate != null) {
            cRate.value = rate.value
            return repository.save(cRate)
        }
        return repository.save(rate)
    }

    @Transactional(readOnly = true)
    override fun currencyRateToday(currencyLiteral: String): CurrencyRate? {
        log.debug("currencyRateToday(currencyLiteral: {})...", currencyLiteral)
        return currencyRateByDate(LocalDate.now(), currencyLiteral)
    }

    @Transactional(readOnly = true)
    override fun allCurrencyRatesToday(): MutableList<CurrencyRate> {
        log.debug("allCurrencyRatesToday()...")
        return allCurrencyRatesByDate(LocalDate.now())
    }

    @Transactional(readOnly = true)
    override fun allCurrencyRatesByDate(date: LocalDate): MutableList<CurrencyRate> {
        log.debug("allCurrencyRatesByDate(date: {})...", date)
        return repository.getAllByDate(date)
    }
}