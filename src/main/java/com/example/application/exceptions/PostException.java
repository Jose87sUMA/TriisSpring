package com.example.application.exceptions;

public class PostException extends RuntimeException{
    public PostException() {
        super();
    }
    public PostException(String message) {
        super(message);
    }
}
