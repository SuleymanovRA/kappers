package ru.kappers.service.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.kappers.config.KappersProperties
import ru.kappers.model.CurrencyRate
import ru.kappers.repository.CurrRateRepository
import ru.kappers.service.CurrRateService
import java.time.LocalDate

@Service
@Transactional
open class CurrRateServiceImpl(private val repository: CurrRateRepository,
                               private val kappersProperties: KappersProperties): CurrRateService {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun save(rate: CurrencyRate): CurrencyRate {
        log.debug("save(rate: {})...", rate)
        return update(rate)
    }

    @Transactional(readOnly = true)
    override fun isExist(date: LocalDate, currLiteral: String): Boolean {
        log.debug("isExist(date: {}, currLiteral: {})...", date, currLiteral)
        return currLiteral == kappersProperties.rubCurrencyCode || getCurrByDate(date, currLiteral) != null
    }

    @Transactional(readOnly = true)
    override fun getCurrByDate(date: LocalDate, currLiteral: String): CurrencyRate? {
        log.debug("getCurrByDate(date: {}, currLiteral: {})...", date, currLiteral)
        return repository.getCurrencyRateByDateAndCharCode(date, currLiteral).orElse(null)
    }

    override fun clear() {
        log.debug("clear()...")
        repository.deleteAll()
    }

    override fun update(rate: CurrencyRate): CurrencyRate {
        log.debug("update(rate: {})...", rate)
        val cRate = getCurrByDate(rate.date, rate.charCode)
        if (cRate != null) {
            cRate.value = rate.value
            return repository.save(cRate)
        }
        return repository.save(rate)
    }

    @Transactional(readOnly = true)
    override fun getToday(literal: String): CurrencyRate? {
        log.debug("getToday(literal: {})...", literal)
        return getCurrByDate(LocalDate.now(), literal)
    }

    @Transactional(readOnly = true)
    override fun getAllToday(): MutableList<CurrencyRate> {
        log.debug("getAllToday()...")
        return getAllByDate(LocalDate.now())
    }

    @Transactional(readOnly = true)
    override fun getAllByDate(date: LocalDate): MutableList<CurrencyRate> {
        log.debug("getAllByDate(date: {})...", date)
        return repository.getAllByDate(date)
    }
}