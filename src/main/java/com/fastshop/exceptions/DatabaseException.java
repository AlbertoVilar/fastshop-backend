package com.fastshop.exceptions;

public class DatabaseException extends RuntimeException{
    private static final long serialVersionUID = 1L; // boa prática

    public DatabaseException(String message) {
        super(message);
    }
}
