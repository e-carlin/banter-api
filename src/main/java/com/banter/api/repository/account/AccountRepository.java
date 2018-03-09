package com.banter.api.repository.account;

import com.banter.api.model.document.AccountsDocument;
import com.banter.api.requestexceptions.customExceptions.PlaidGetAccountBalanceException;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

public interface AccountRepository {
    Optional<AccountsDocument> findById(String userId) throws ExecutionException, InterruptedException;

    boolean userHasInstitution(String userId, String institutionId) throws ExecutionException, InterruptedException;

    AccountsDocument saveAccountItemFromAddAccountRequest(
            String itemId,
            String accessToken,
            String institutionName,
            String institutionId,
            String userId) throws ExecutionException, InterruptedException, PlaidGetAccountBalanceException;

    AccountsDocument add(AccountsDocument accountsDocument) throws ExecutionException, InterruptedException;
}
