package ru.kappers.assertion;

import org.assertj.core.api.AbstractAssert;
import ru.kappers.model.Role;
import ru.kappers.model.User;

import static org.assertj.core.api.Assertions.assertThat;

public class UserAssert extends AbstractAssert<UserAssert, User> {
    protected UserAssert(User user) {
        super(user, UserAssert.class);
    }

    public UserAssert hasUserName(final String userName) {
        isNotNull();
        if (!userNameEquals(userName)) {
            failWithMessage("Expected userName \"%s\" but was \"%s\"", userName, actual.getUserName());
        }
        return this;
    }

    private boolean userNameEquals(String userName) {
        return actual.getUserName().equals(userName);
    }

    public UserAssert hasNoUserName(final String userName) {
        isNotNull();
        if (userNameEquals(userName)) {
            failWithMessage("Expected userName was not \"%s\" but was", userName);
        }
        return this;
    }

    public UserAssert hasEmail(final String email) {
        isNotNull();
        if (!emailEquals(email)) {
            failWithMessage("Expected email \"%s\" but was \"%s\"", email, actual.getEmail());
        }
        return this;
    }

    private boolean emailEquals(String email) {
        return actual.getEmail().equals(email);
    }

    public UserAssert hasNoEmail(final String email) {
        isNotNull();
        if (emailEquals(email)) {
            failWithMessage("Expected email was not \"%s\" but was", email);
        }
        return this;
    }

    public UserAssert hasRole(final Role role) {
        isNotNull();
        assertThat(actual)
                .extracting(User::getRole)
                .as("role")
                .isEqualTo(role);
        return this;
    }

    public UserAssert hasNoRole(final Role role) {
        isNotNull();
        assertThat(actual)
                .extracting(User::getRole)
                .as("role")
                .isNotEqualTo(role);
        return this;
    }

    public RoleAssert roleAssert() {
        isNotNull();
        return Assertions.assertThat(actual.getRole());
    }
}
