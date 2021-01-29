package com.charliechiang.wastesortinghelperserver;

public class ResourceConflictException extends RuntimeException {

    ResourceConflictException(String message) {
        super(message);
    }
}
