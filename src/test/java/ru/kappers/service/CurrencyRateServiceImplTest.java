package ru.kappers.service;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import ru.kappers.KappersApplication;
import ru.kappers.model.CurrencyRate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ActiveProfiles("test")
@ContextConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {KappersApplication.class})
@TestExecutionListeners({DbUnitTestExecutionListener.class})
@DatabaseSetup("/data/CurrRateServiceImplTest-currrates.xml")
public class CurrencyRateServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests {
    @Autowired
    private CurrencyRateService service;

    @Test
    public void save() {
        CurrencyRate currencyRate = CurrencyRate.builder()
                .nominal(1)
                .value(new BigDecimal("4650.00"))
                .date(LocalDate.parse("2018-11-23"))
                .charCode("BTC")
                .numCode("000")
                .name("Bitcoin")
                .build();
        CurrencyRate result = service.save(currencyRate);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotEqualTo(0);
        assertThat(result).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(currencyRate);
    }

    @Test
    public void isExist() {
        assertThat(service.isExist(LocalDate.parse("2018-11-21"), "GLD")).isTrue();
    }

    @Test
    public void currencyRateByDate() {
        CurrencyRate gld = service.currencyRateByDate(LocalDate.parse("2018-11-21"), "GLD");
        assertThat(gld).isNotNull();
        assertThat(gld.getNumCode()).isEqualTo("999");
    }

    @Test
    public void update() {
        CurrencyRate gld = service.currencyRateByDate(LocalDate.parse("2018-11-21"), "GLD");
        assertThat(gld).isNotNull();
        assertThat(gld.getValue()).isEqualTo(new BigDecimal("2000.0000"));

        final BigDecimal expectedValue = BigDecimal.valueOf(3000);
        gld.setValue(expectedValue);
        CurrencyRate update = service.update(gld);
        assertThat(update.getValue()).isEqualTo(expectedValue);
    }

    @Test
    public void currencyRateToday() {
        String charCode = "TST";
        CurrencyRate currencyRate = service.update(CurrencyRate.builder()
                .charCode(charCode)
                .date(LocalDate.now())
                .value(BigDecimal.ONE)
                .build());

        CurrencyRate result = service.currencyRateToday(charCode);

        assertThat(result).usingRecursiveComparison()
                .isEqualTo(currencyRate);
    }

    @Test
    public void allCurrencyRatesToday() {
        //TODO Написать тест
    }

    @Test
    public void allCurrencyRatesByDate() {
        List<CurrencyRate> allByDate = service.allCurrencyRatesByDate(LocalDate.parse("2018-11-21"));
        CurrencyRate gld = service.currencyRateByDate(LocalDate.parse("2018-11-21"), "GLD");
        assertThat(allByDate).contains(gld);
    }
}