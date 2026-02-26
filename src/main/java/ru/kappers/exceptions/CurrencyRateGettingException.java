package ru.kappers.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CurrencyRateGettingException extends RuntimeException{
    public CurrencyRateGettingException(String message, Exception e){
        super(message, e);
        log.error(message + " "+e.getMessage());
    }
    public CurrencyRateGettingException(String message){
        super(message);
        log.error(message);
    }

}
