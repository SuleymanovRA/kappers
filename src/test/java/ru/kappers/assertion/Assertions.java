package ru.kappers.assertion;

import lombok.experimental.UtilityClass;
import ru.kappers.model.CurrencyRate;
import ru.kappers.model.KapperInfo;
import ru.kappers.model.Role;
import ru.kappers.model.User;

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
}
