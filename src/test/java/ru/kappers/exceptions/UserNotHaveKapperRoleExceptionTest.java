package ru.kappers.exceptions;

import org.junit.jupiter.api.Test;
import ru.kappers.UnitTest;

import static org.assertj.core.api.Assertions.assertThat;


class UserNotHaveKapperRoleExceptionTest extends UnitTest {
    @Test
    void constructorWithMessageOnly() {
        final String testMessage = "test message";
        var resultException = new UserNotHaveKapperRoleException(testMessage);
        assertThat(resultException)
                .hasMessage(testMessage)
                .hasNoCause();
    }

    @Test
    void constructorWithExceptionOnly() {
        final var exception = new Exception();
        var resultException = new UserNotHaveKapperRoleException(exception);
        assertThat(resultException)
                .hasMessage(UserNotHaveKapperRoleException.DEFAULT_MESSAGE)
                .getCause()
                .isEqualTo(exception);
    }

    @Test
    void constructorWithMessageAndException() {
        final String testMessage = "test message2";
        final Exception testException = new Exception();
        var resultException = new UserNotHaveKapperRoleException(testMessage, testException);
        assertThat(resultException)
                .hasMessage(testMessage)
                .getCause()
                .isEqualTo(testException);
    }
}