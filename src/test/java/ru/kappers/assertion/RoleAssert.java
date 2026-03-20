package ru.kappers.assertion;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.RecursiveComparisonAssert;
import ru.kappers.model.Role;

import java.util.Objects;

public class RoleAssert extends AbstractAssert<RoleAssert, Role> {
    protected RoleAssert(Role role) {
        super(role, RoleAssert.class);
    }

    public RoleAssert hasName(final String name) {
        if (!nameEquals(name)) {
            failWithMessage("Expected name \"%s\" but was \"%s\"", name, actual.getName());
        }
        return this;
    }

    private boolean nameEquals(String name) {
        return Objects.equals(actual.getName(), name);
    }

    public RoleAssert hasNoName(final String name) {
        if (nameEquals(name)) {
            failWithMessage("Expected name was not \"%s\" but was", name);
        }
        return this;
    }

    public RoleAssert hasId(int id) {
        idAssertion().isEqualTo(id);
        return this;
    }

    private AbstractObjectAssert<?, Integer> idAssertion() {
        return Assertions.assertThat(actual)
                .extracting(Role::getId)
                .as("id");
    }

    public RoleAssert hasNoId(int id) {
        idAssertion().isNotEqualTo(id);
        return this;
    }

    @Override
    public RecursiveComparisonAssert<?> usingRecursiveComparison() {
        return super.usingRecursiveComparison();
    }
}
