package com.banter.api.requestexceptions.customExceptions;

public class UnsupportedWebhookTypeException extends Exception{

    public UnsupportedWebhookTypeException(String message) {
        super(message);
    }
}
