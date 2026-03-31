package ru.kappers.exceptions;

import org.junit.jupiter.api.Test;
import ru.kappers.UnitTest;

import static org.assertj.core.api.Assertions.assertThat;


class CurrencyRateGettingExceptionTest extends UnitTest {
    @Test
    void constructorWithMessageOnly() {
        final String testMessage = "test message";
        var currencyRateGettingException = new CurrencyRateGettingException(testMessage);
        assertThat(currencyRateGettingException)
                .hasMessage(testMessage)
                .hasNoCause();
    }

    @Test
    void constructorWithMessageAndException() {
        final String testMessage = "test message2";
        final var exception = new Exception();
        var currencyRateGettingException = new CurrencyRateGettingException(testMessage, exception);
        assertThat(currencyRateGettingException)
                .hasMessage(testMessage)
                .hasCause(exception);
    }
}