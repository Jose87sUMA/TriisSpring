package com.example.application.exceptions;

public class UserException  extends RuntimeException{
    public UserException() {
        super();
    }
    public UserException(String message) {
        super(message);
    }
}
