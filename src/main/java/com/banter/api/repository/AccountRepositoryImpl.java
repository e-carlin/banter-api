package com.banter.api.repository;

import com.banter.api.model.item.AccountItem;
import com.banter.api.model.item.attribute.AccountAttribute;
import com.banter.api.model.item.attribute.AccountBalancesAttribute;
import com.banter.api.model.item.attribute.InstitutionAttribute;
import com.banter.api.model.request.addAccount.AddAccountRequestAccount;
import com.banter.api.requestexceptions.PlaidGetAccountBalanceException;
import com.banter.api.service.plaid.PlaidClientService;
import com.plaid.client.response.Account;
import com.plaid.client.response.AccountsBalanceGetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import retrofit2.Response;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
public class AccountRepositoryImpl implements AccountRepositoryCustom {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PlaidClientService plaidClientService;

    @Override
    public AccountItem saveAccountItemFromAddAccountRequest(
            List<AddAccountRequestAccount> requestAccounts,
            String itemId, String accessToken,
            String institutionName,
            String institutionId,
            String userEmail)
            throws ConstraintViolationException, PlaidGetAccountBalanceException {
        //TODO: First, check if they already have an existing AccountItem. If so get it,
        // and add this InstitutionAttribute to it, While adding the institution attribute check if the institution_id / account_id is already found
        // IF so just update the balance

        AccountItem accountItemToSave = new AccountItem();
        accountItemToSave.setUserEmail(userEmail);

        InstitutionAttribute institutionAttribute = new InstitutionAttribute(
                itemId,
                institutionName,
                institutionId);


        //TODO: we should wrap this in our own object
        Response<AccountsBalanceGetResponse> response = this.plaidClientService.getAccountBalance(accessToken);
        List<Account> accounts = response.body().getAccounts();
        for (Account account : accounts) {
            AccountBalancesAttribute accountBalancesAttribute = new AccountBalancesAttribute(
                    account.getBalances().getCurrent(),
                    account.getBalances().getAvailable(),
                    account.getBalances().getLimit());


            AccountAttribute accountAttribute = new AccountAttribute(
                    account.getAccountId(),
                    account.getName(),
                    account.getType(),
                    account.getSubtype(),
                    accountBalancesAttribute);

            institutionAttribute.addAccountAttribute(accountAttribute);
        }

            accountItemToSave.addInstitutionAttribute(institutionAttribute);

            Set<ConstraintViolation<AccountItem>> errors = accountItemToSave.validate();
            if (!errors.isEmpty()) {
                //TODO: Add error logging
                System.out.println("Validation errors found when trying to save new accountItem");
                System.out.println(Arrays.toString(errors.toArray()));
                throw new ConstraintViolationException(errors);
            } else {
                this.accountRepository.save(accountItemToSave);
                return accountItemToSave;
            }
        }
    }
