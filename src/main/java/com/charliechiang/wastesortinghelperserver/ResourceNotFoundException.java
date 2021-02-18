package com.charliechiang.wastesortinghelperserver;

public class ResourceNotFoundException extends RuntimeException {

    ResourceNotFoundException(String message) {
        super(message);
    }
}
