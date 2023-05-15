package com.example.application.data.exceptions;

public class MakePostException extends RuntimeException{
    public MakePostException() {
        super();
    }
    public MakePostException(String message) {
        super(message);
    }
}
