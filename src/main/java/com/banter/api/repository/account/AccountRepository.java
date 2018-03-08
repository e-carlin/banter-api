package com.banter.api.repository.account;

import com.banter.api.model.item.AccountItem;
import com.banter.api.requestexceptions.customExceptions.PlaidGetAccountBalanceException;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

public interface AccountRepository {
    Optional<AccountItem> findById(String userId) throws ExecutionException, InterruptedException;

    boolean userHasInstitution(String userId, String institutionId) throws ExecutionException, InterruptedException;

    AccountItem saveAccountItemFromAddAccountRequest(
            String itemId,
            String accessToken,
            String institutionName,
            String institutionId,
            String userId) throws ExecutionException, InterruptedException, PlaidGetAccountBalanceException;

    AccountItem save(AccountItem accountItem) throws ExecutionException, InterruptedException;
}
