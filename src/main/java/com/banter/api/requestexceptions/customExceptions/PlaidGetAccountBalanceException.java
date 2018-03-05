package com.banter.api.requestexceptions.customExceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlaidGetAccountBalanceException extends Exception {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public PlaidGetAccountBalanceException(String message) {
        super("There was an exception getting account balances from Plaid: "+message);
        logger.warn("Exception encountered when trying to get account balances from Plaid. Message: "+message);
    }
}
