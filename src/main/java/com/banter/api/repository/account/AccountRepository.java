package com.banter.api.repository.account;

import com.banter.api.model.document.AccountsDocument;
import com.banter.api.model.document.InstitutionTokenDocument;
import com.banter.api.model.request.AddAccountRequest;
import com.banter.api.requestexceptions.customExceptions.FirestoreException;
import com.banter.api.requestexceptions.customExceptions.PlaidGetAccountBalanceException;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

public interface AccountRepository {
    Optional<AccountsDocument.Institution.Account> findAccountByName(String name, String userId) throws FirestoreException;
    boolean userHasInstitution(String userId, String institutionId) throws FirestoreException;
    AccountsDocument add(AddAccountRequest addAccountRequest, InstitutionTokenDocument institutionTokenDocument) throws PlaidGetAccountBalanceException, FirestoreException;
    AccountsDocument add(AccountsDocument accountsDocument) throws ExecutionException, InterruptedException;
}
