package ru.kappers.exceptions;

public class CurrencyRateGettingException extends RuntimeException{
    public CurrencyRateGettingException(String message, Exception e){
        super(message, e);
    }
    public CurrencyRateGettingException(String message){
        super(message);
    }
}
