package com.banter.api.service.plaid;

public class PlaidExecuteExchangePublicTokenException extends Exception {
    public PlaidExecuteExchangePublicTokenException(String message) {
        super("There was an exception exchanging the Plaid public token: "+message);
    }
}
