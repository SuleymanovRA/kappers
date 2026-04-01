package ru.kappers.exceptions;

import org.junit.jupiter.api.Test;
import ru.kappers.UnitTest;

import static org.assertj.core.api.Assertions.assertThat;


class UnirestAPIExceptionTest extends UnitTest {

    @Test
    void constructorWithMessageOnly() {
        final String testMessage = "test message";
        var unirestAPIException = new UnirestAPIException(testMessage);
        assertThat(unirestAPIException)
                .hasMessage(testMessage)
                .hasNoCause();
    }

    @Test
    void constructorWithExceptionOnly() {
        final var exception = new Exception();
        var unirestAPIException = new UnirestAPIException(exception);
        assertThat(unirestAPIException)
                .hasMessage(UnirestAPIException.ERROR_TEXT)
                .hasCause(exception);
    }

    @Test
    void constructorWithMessageAndException() {
        final String testMessage = "test message2";
        final var exception = new Exception();
        var unirestAPIException = new UnirestAPIException(testMessage, exception);
        assertThat(unirestAPIException)
                .hasMessage(testMessage)
                .hasCause(exception);
    }
}