package ru.kappers.exceptions;

public class BetParserException extends RuntimeException{
    public BetParserException(String message, Exception e){
        super(message, e);
    }
    public BetParserException(String message){
        super(message);
    }
}

