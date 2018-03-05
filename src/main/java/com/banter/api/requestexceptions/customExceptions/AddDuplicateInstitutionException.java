package com.banter.api.requestexceptions.customExceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddDuplicateInstitutionException extends Exception {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public AddDuplicateInstitutionException(String message) {
        super("Tried to add duplicate institution: "+message);
        logger.warn("Tried to add duplicate institution. Message: "+message);
    }
}
