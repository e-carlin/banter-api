package com.banter.api.requestexceptions.customExceptions;

public class FirestoreQueryException extends  Exception {

    public FirestoreQueryException(String message) {
        super(message);
    }
}
