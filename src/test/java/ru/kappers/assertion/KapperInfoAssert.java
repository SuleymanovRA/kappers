package ru.kappers.assertion;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.RecursiveComparisonAssert;
import ru.kappers.model.KapperInfo;
import ru.kappers.model.User;

import static org.assertj.core.api.Assertions.assertThat;

public class KapperInfoAssert extends AbstractAssert<KapperInfoAssert, KapperInfo> {
    protected KapperInfoAssert(KapperInfo kapperInfo) {
        super(kapperInfo, KapperInfoAssert.class);
    }

    public KapperInfoAssert hasTokens(Integer tokens) {
        tokensAssertion().isEqualTo(tokens);
        return this;
    }

    private AbstractObjectAssert<?, Integer> tokensAssertion() {
        return assertThat(actual)
                .extracting(KapperInfo::getTokens)
                .as("tokens");
    }

    public KapperInfoAssert hasNoTokens(Integer tokens) {
        tokensAssertion().isNotEqualTo(tokens);
        return this;
    }

    public KapperInfoAssert hasBets(Integer bets) {
        betsAssertion().isEqualTo(bets);
        return this;
    }

    private AbstractObjectAssert<?, Integer> betsAssertion() {
        return assertThat(actual)
                .extracting(KapperInfo::getBets)
                .as("bets");
    }

    public KapperInfoAssert hasNoBets(Integer bets) {
        betsAssertion().isNotEqualTo(bets);
        return this;
    }

    public KapperInfoAssert hasBlockedTokens(Integer blockedTokens) {
        blockedTokensAssertion().isEqualTo(blockedTokens);
        return this;
    }

    private AbstractObjectAssert<?, Integer> blockedTokensAssertion() {
        return assertThat(actual)
                .extracting(KapperInfo::getBlockedTokens)
                .as("blockedTokens");
    }

    public KapperInfoAssert hasNoBlockedTokens(Integer blockedTokens) {
        blockedTokensAssertion().isNotEqualTo(blockedTokens);
        return this;
    }

    public KapperInfoAssert hasSuccessBets(Integer successBets) {
        successBetsAssertion().isEqualTo(successBets);
        return this;
    }

    private AbstractObjectAssert<?, Integer> successBetsAssertion() {
        return assertThat(actual)
                .extracting(KapperInfo::getSuccessBets)
                .as("successBets");
    }

    public KapperInfoAssert hasNoSuccessBets(Integer successBets) {
        successBetsAssertion().isNotEqualTo(successBets);
        return this;
    }

    public KapperInfoAssert hasUser(User user) {
        userAssertion().isEqualTo(user);
        return this;
    }

    private AbstractObjectAssert<?, User> userAssertion() {
        return assertThat(actual)
                .extracting(KapperInfo::getUser)
                .as("user");
    }

    public KapperInfoAssert hasNoUser(User user) {
        userAssertion().isNotEqualTo(user);
        return this;
    }

    @Override
    public RecursiveComparisonAssert<?> usingRecursiveComparison() {
        return super.usingRecursiveComparison();
    }
}
