package ru.kappers.exceptions;

import org.junit.jupiter.api.Test;
import ru.kappers.UnitTest;

import static org.assertj.core.api.Assertions.assertThat;

class MoneyTransferExceptionTest extends UnitTest {

    @Test
    void constructorWithExceptionOnly() {
        final var exception = new Exception("test");
        var moneyTransferException = new MoneyTransferException(exception);
        assertThat(moneyTransferException)
                .hasMessage(MoneyTransferException.DEFAULT_MESSAGE)
                .getCause()
                .hasMessage(exception.getMessage());
    }
}