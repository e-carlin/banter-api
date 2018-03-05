package com.banter.api.requestexceptions;

public class PlaidExchangePublicTokenException extends Exception {
    public PlaidExchangePublicTokenException(String message) {
        super("There was an exception exchanging the Plaid public token: "+message);
    }
}
