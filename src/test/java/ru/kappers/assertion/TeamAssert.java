package ru.kappers.assertion;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.RecursiveComparisonAssert;
import ru.kappers.model.catalog.Team;

public class TeamAssert extends AbstractAssert<TeamAssert, Team> {
    protected TeamAssert(Team team) {
        super(team, TeamAssert.class);
    }

    @Override
    public RecursiveComparisonAssert<?> usingRecursiveComparison() {
        return super.usingRecursiveComparison();
    }

    public TeamAssert hasId(Integer id) {
        idAssertion().isEqualTo(id);
        return this;
    }

    private AbstractObjectAssert<?, Integer> idAssertion() {
        return Assertions.assertThat(actual)
                .extracting(Team::getId)
                .as("id");
    }

    public TeamAssert hasNoId(Integer id) {
        idAssertion().isNotEqualTo(id);
        return this;
    }
}
