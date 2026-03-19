package ru.kappers.service;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import ru.kappers.AbstractDatabaseTest;
import ru.kappers.config.KappersProperties;
import ru.kappers.model.CurrencyRate;
import ru.kappers.repository.CurrRateRepository;
import ru.kappers.service.impl.CurrencyRateServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DatabaseSetup("/data/CurrRateServiceImplTest-currrates.xml")
@ContextConfiguration(classes = CurrencyRateServiceImplTest.Configuration.class)
public class CurrencyRateServiceImplTest extends AbstractDatabaseTest {
    @Autowired
    private CurrencyRateService currencyRateService;

    @Test
    public void save() {
        final var currencyRate = newCurrencyRate();
        CurrencyRate result = currencyRateService.save(currencyRate);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotEqualTo(0);
        assertThat(result).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(currencyRate);
    }

    private CurrencyRate newCurrencyRate() {
        return CurrencyRate.builder()
                .nominal(1)
                .value(new BigDecimal("4650.00"))
                .date(LocalDate.parse("2018-11-23"))
                .charCode("BTC")
                .numCode("000")
                .name("Bitcoin")
                .build();
    }

    @Test
    public void isExist() {
        assertThat(currencyRateService.isExist(LocalDate.parse("2018-11-21"), "GLD")).isTrue();
    }

    @Test
    public void currencyRateByDate() {
        CurrencyRate gld = currencyRateService.currencyRateByDate(LocalDate.parse("2018-11-21"), "GLD");
        assertThat(gld).isNotNull();
        assertThat(gld.getNumCode()).isEqualTo("999");
    }

    @Test
    public void update() {
        CurrencyRate gld = currencyRateService.currencyRateByDate(LocalDate.parse("2018-11-21"), "GLD");
        assertThat(gld).isNotNull();
        assertThat(gld.getValue()).isEqualTo(new BigDecimal("2000.0000"));

        final BigDecimal expectedValue = BigDecimal.valueOf(3000);
        gld.setValue(expectedValue);
        CurrencyRate update = currencyRateService.update(gld);
        assertThat(update.getValue()).isEqualTo(expectedValue);
    }

    @Test
    public void currencyRateToday() {
        String charCode = "TST";
        CurrencyRate currencyRate = currencyRateService.update(CurrencyRate.builder()
                .charCode(charCode)
                .date(LocalDate.now())
                .value(BigDecimal.ONE)
                .build());

        CurrencyRate result = currencyRateService.currencyRateToday(charCode);

        assertThat(result).usingRecursiveComparison()
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .isEqualTo(currencyRate);
    }

    @Test
    @Ignore("Реализовать тест")
    public void allCurrencyRatesToday() {
        //TODO Написать тест
    }

    @Test
    public void allCurrencyRatesByDate() {
        List<CurrencyRate> allByDate = currencyRateService.allCurrencyRatesByDate(LocalDate.parse("2018-11-21"));
        CurrencyRate gld = currencyRateService.currencyRateByDate(LocalDate.parse("2018-11-21"), "GLD");
        assertThat(allByDate).contains(gld);
    }

    @TestConfiguration
    public static class Configuration {
        @Bean
        public CurrencyRateService currencyRateService(CurrRateRepository repository, KappersProperties properties) {
            return new CurrencyRateServiceImpl(repository, properties);
        }
    }
}