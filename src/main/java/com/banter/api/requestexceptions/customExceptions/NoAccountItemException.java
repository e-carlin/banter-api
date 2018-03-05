package com.banter.api.requestexceptions.customExceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoAccountItemException extends Exception{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public NoAccountItemException(String message) {
        super(message);
        logger.warn("Tried to get account data for a user. That user does not have any account data. Message: "+message);
    }
}
