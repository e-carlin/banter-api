package com.banter.api.repository.account;

import com.banter.api.model.item.AccountItem;
import com.banter.api.model.request.addAccount.AddAccountRequestAccount;
import com.banter.api.requestexceptions.customExceptions.PlaidGetAccountBalanceException;

import javax.validation.ConstraintViolationException;
import java.util.List;

public interface AccountRepositoryCustom {
    AccountItem saveAccountItemFromAddAccountRequest(List<AddAccountRequestAccount> requestAccounts, String itemId, String accessToken, String institutionName, String institutionId, String userEmail) throws ConstraintViolationException, PlaidGetAccountBalanceException;
    boolean userHasInstitution(String userEmail, String insId);

}
