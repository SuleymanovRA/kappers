package ru.kappers.exceptions;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

public class CurrencyRateGettingExceptionTest {

    @Test
    public void constructorWithMessageOnly() {
        final String testMessage = "test message";

        CurrencyRateGettingException exception = new CurrencyRateGettingException(testMessage);

        assertThat(exception.getMessage(), is(testMessage));
        assertThat(exception.getCause(), is(nullValue()));
    }

    @Test
    public void constructorWithMessageAndException() {
        final String testMessage = "test message2";
        final Exception testException = new Exception();

        CurrencyRateGettingException exception = new CurrencyRateGettingException(testMessage, testException);

        assertThat(exception.getMessage(), is(testMessage));
        assertThat(exception.getCause(), is(testException));
    }
}