package ru.kappers.exceptions;


public class UserNotHaveKapperRoleException extends RuntimeException{
    protected static final String DEFAULT_MESSAGE = "User is not kapper, but tries to use kapper's action";

    public UserNotHaveKapperRoleException(Exception e) {
        super(DEFAULT_MESSAGE, e);
    }

    public UserNotHaveKapperRoleException(String message, Exception e){
        super(message, e);
    }

    public UserNotHaveKapperRoleException(String message){
        super(message);
    }
}
