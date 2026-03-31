package ru.kappers.exceptions;


public class UnirestAPIException extends RuntimeException {
    protected static final String ERROR_TEXT = "Ошибка получения данных по Rapid API";
    public UnirestAPIException(String message, Exception e){
        super(message, e);
    }
    public UnirestAPIException(String message){
        super(message);
    }
    public UnirestAPIException(Exception e){
        super(ERROR_TEXT, e);
    }
}
