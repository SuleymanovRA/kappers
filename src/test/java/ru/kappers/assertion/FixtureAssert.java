package ru.kappers.assertion;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.RecursiveComparisonAssert;
import ru.kappers.model.Fixture;
import ru.kappers.model.Fixture.Status;

public class FixtureAssert extends AbstractAssert<FixtureAssert, Fixture> {
    protected FixtureAssert(Fixture fixture) {
        super(fixture, FixtureAssert.class);
    }

    @Override
    public RecursiveComparisonAssert<?> usingRecursiveComparison() {
        return super.usingRecursiveComparison();
    }

    public FixtureAssert hasId(Integer id) {
        idAssertion().isEqualTo(id);
        return this;
    }

    private AbstractObjectAssert<?, Integer> idAssertion() {
        return Assertions.assertThat(actual)
                .extracting(Fixture::getId)
                .as("id");
    }

    public FixtureAssert hasNoId(Integer id) {
        idAssertion().isNotEqualTo(id);
        return this;
    }

    public FixtureAssert hasStatus(Status status) {
        statusAssertion().isEqualTo(status);
        return this;
    }

    private AbstractObjectAssert<?, Status> statusAssertion() {
        return Assertions.assertThat(actual)
                .extracting(Fixture::getStatus)
                .as("status");
    }

    public FixtureAssert hasNoStatus(Status status) {
        statusAssertion().isNotEqualTo(status);
        return this;
    }
}
