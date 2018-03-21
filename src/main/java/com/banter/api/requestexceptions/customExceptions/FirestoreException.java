package com.banter.api.requestexceptions.customExceptions;

public class FirestoreException extends  Exception {

    public FirestoreException(String message) {
        super(message);
    }
}
