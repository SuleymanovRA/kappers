package ru.kappers.assertion;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.RecursiveComparisonAssert;
import ru.kappers.model.CurrencyRate;

import java.math.BigDecimal;

public class CurrencyRateAssert extends AbstractAssert<CurrencyRateAssert, CurrencyRate> {
    protected CurrencyRateAssert(CurrencyRate currencyRate) {
        super(currencyRate, CurrencyRateAssert.class);
    }

    public CurrencyRateAssert hasId(int id) {
        idAssertion().isEqualTo(id);
        return this;
    }

    private AbstractObjectAssert<?, Integer> idAssertion() {
        return Assertions.assertThat(actual)
                .extracting(CurrencyRate::getId)
                .as("id");
    }

    public CurrencyRateAssert hasNoId(int id) {
        idAssertion().isNotEqualTo(id);
        return this;
    }

    @Override
    public RecursiveComparisonAssert<?> usingRecursiveComparison() {
        return super.usingRecursiveComparison()
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class);
    }

    public CurrencyRateAssert hasNumCode(String numCode) {
        numCodeAssertion().isEqualTo(numCode);
        return this;
    }

    private AbstractObjectAssert<?, String> numCodeAssertion() {
        return Assertions.assertThat(actual)
                .extracting(CurrencyRate::getNumCode)
                .as("numCode");
    }

    public CurrencyRateAssert hasNoNumCode(String numCode) {
        numCodeAssertion().isNotEqualTo(numCode);
        return this;
    }

    public CurrencyRateAssert hasValue(BigDecimal value) {
        valueAssertion().isEqualTo(value);
        return this;
    }

    private AbstractObjectAssert<?, BigDecimal> valueAssertion() {
        return Assertions.assertThat(actual)
                .extracting(CurrencyRate::getValue)
                .as("value");
    }

    public CurrencyRateAssert hasNoValue(BigDecimal value) {
        valueAssertion().isNotEqualTo(value);
        return this;
    }
}
