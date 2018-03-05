package com.banter.api.requestexceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlaidExchangePublicTokenException extends Exception {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public PlaidExchangePublicTokenException(String message) {
        super("There was an exception exchanging the Plaid public token: "+message);
        logger.warn("Exception encountered when exchanging plaid public token: "+message);
    }
}
