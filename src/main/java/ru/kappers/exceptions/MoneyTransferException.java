package ru.kappers.exceptions;


public class MoneyTransferException extends RuntimeException {
    protected static final String DEFAULT_MESSAGE = "Money transfer Exception";

    public MoneyTransferException(Exception e) {
        super(DEFAULT_MESSAGE, e);
    }
}
