package ru.kappers.assertion;

import lombok.experimental.UtilityClass;
import ru.kappers.model.Role;
import ru.kappers.model.User;

@UtilityClass
public class Assertions {
    public static UserAssert assertThat(User actual) {
        return new UserAssert(actual);
    }

    public static RoleAssert assertThat(Role role) {
        return new RoleAssert(role);
    }
}
