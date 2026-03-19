package ru.kappers.assertion;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.RecursiveComparisonAssert;
import ru.kappers.model.Role;
import ru.kappers.model.User;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class UserAssert extends AbstractAssert<UserAssert, User> {
    protected UserAssert(User user) {
        super(user, UserAssert.class);
    }

    public UserAssert hasUserName(final String userName) {
        if (!userNameEquals(userName)) {
            failWithMessage("Expected userName \"%s\" but was \"%s\"", userName, actual.getUserName());
        }
        return this;
    }

    private boolean userNameEquals(String userName) {
        return Objects.equals(actual.getUserName(), userName);
    }

    public UserAssert hasNoUserName(final String userName) {
        if (userNameEquals(userName)) {
            failWithMessage("Expected userName was not \"%s\" but was", userName);
        }
        return this;
    }

    public UserAssert hasEmail(final String email) {
        if (!emailEquals(email)) {
            failWithMessage("Expected email \"%s\" but was \"%s\"", email, actual.getEmail());
        }
        return this;
    }

    private boolean emailEquals(String email) {
        return Objects.equals(actual.getEmail(), email);
    }

    public UserAssert hasNoEmail(final String email) {
        if (emailEquals(email)) {
            failWithMessage("Expected email was not \"%s\" but was", email);
        }
        return this;
    }

    public UserAssert hasRole(final Role role) {
        roleAssertion().isEqualTo(role);
        return this;
    }

    private AbstractObjectAssert<?, Role> roleAssertion() {
        return assertThat(actual)
                .extracting(User::getRole)
                .as("role");
    }

    public UserAssert hasNoRole(final Role role) {
        roleAssertion().isNotEqualTo(role);
        return this;
    }

    public RoleAssert roleAssert() {
        return Assertions.assertThat(actual.getRole());
    }

    public KapperInfoAssert kapperInfoAssert() {
        return Assertions.assertThat(actual.getKapperInfo());
    }

    @Override
    public RecursiveComparisonAssert<?> usingRecursiveComparison() {
        return super.usingRecursiveComparison();
    }
}
