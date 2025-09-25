package com.fastshop.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L; // boa prática

    public ResourceNotFoundException(String message) {
        super(message);
    }
}

