package ru.kappers.assertion;

import lombok.experimental.UtilityClass;
import ru.kappers.model.*;

@UtilityClass
public class Assertions {
    public static UserAssert assertThat(User user) {
        return new UserAssert(user);
    }

    public static RoleAssert assertThat(Role role) {
        return new RoleAssert(role);
    }

    public static KapperInfoAssert assertThat(KapperInfo kapperInfo) {
        return new KapperInfoAssert(kapperInfo);
    }

    public static CurrencyRateAssert assertThat(CurrencyRate currencyRate) {
        return new CurrencyRateAssert(currencyRate);
    }

    public static FixtureAssert assertThat(Fixture fixture) {
        return new FixtureAssert(fixture);
    }
}
