package ru.kappers.assertion;

import org.assertj.core.api.AbstractAssert;
import ru.kappers.model.Role;

public class RoleAssert extends AbstractAssert<RoleAssert, Role> {
    protected RoleAssert(Role role) {
        super(role, RoleAssert.class);
    }

    public RoleAssert hasName(final String name) {
        isNotNull();
        if (!nameEquals(name)) {
            failWithMessage("Expected name \"%s\" but was \"%s\"", name, actual.getName());
        }
        return this;
    }

    private boolean nameEquals(String name) {
        return actual.getName().equals(name);
    }

    public RoleAssert hasNoName(final String name) {
        isNotNull();
        if (nameEquals(name)) {
            failWithMessage("Expected name was not \"%s\" but was", name);
        }
        return this;
    }
}
