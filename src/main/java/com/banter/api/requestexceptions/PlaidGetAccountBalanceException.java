package com.banter.api.requestexceptions;

public class PlaidGetAccountBalanceException extends Exception {
    public PlaidGetAccountBalanceException(String message) {
        super("There was an exception getting account balances from Plaid: "+message);
    }
}
