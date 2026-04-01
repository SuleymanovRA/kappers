package ru.kappers.exceptions;

import org.junit.jupiter.api.Test;
import ru.kappers.UnitTest;

import static org.assertj.core.api.Assertions.assertThat;


class BetParserExceptionTest extends UnitTest {
    @Test
    void constructorWithMessageOnly() {
        final String testMessage = "test message";
        var betParserException = new BetParserException(testMessage);
        assertThat(betParserException)
                .hasMessage(testMessage)
                .hasNoCause();
    }

    @Test
    void constructorWithMessageAndException() {
        final String testMessage = "test message2";
        final var exception = new Exception();
        var betParserException = new BetParserException(testMessage, exception);
        assertThat(betParserException)
                .hasMessage(testMessage)
                .hasCause(exception);
    }
}