package com.banter.api.service;

import com.banter.api.model.document.AccountsDocument;
import com.banter.api.repository.account.AccountRepository;
import com.banter.api.requestexceptions.customExceptions.PlaidGetAccountBalanceException;
import com.plaid.client.response.Account;
import com.plaid.client.response.AccountsBalanceGetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.util.List;

@Service
public class InstitutionService {

    @Autowired private PlaidClientService plaidClientService;
    @Autowired private AccountRepository accountRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    private boolean accountsDocumentContainsInstitutionId(String insId, AccountsDocument accountsDocument) {
        List<AccountsDocument.Institution> institutions = accountsDocument.getInstitutions();
        for(AccountsDocument.Institution institutionAttribute : institutions) {
            if(institutionAttribute.getInstitutionId().equals(insId)) {
                return true;
            }
        }
        return false;
    }

    public AccountsDocument.Institution createInstitution(String itemId, String institutionName, String institutionId, String accessToken) throws PlaidGetAccountBalanceException {
        AccountsDocument.Institution institution = new AccountsDocument.Institution(
                itemId,
                institutionName,
                institutionId);

        Response<AccountsBalanceGetResponse> response = this.plaidClientService.getAccountBalance(accessToken);
        List<Account> accounts = response.body().getAccounts();
        for (Account accountFromPlaid : accounts) {
            AccountsDocument.Institution.Account.Balances accountBalancesAttribute = new AccountsDocument.Institution.Account.Balances(accountFromPlaid.getBalances());

            AccountsDocument.Institution.Account account = new AccountsDocument.Institution.Account(
                    accountFromPlaid,
                    accountBalancesAttribute);

            this.logger.debug("Account attribute: " + account);
            institution.addAccount(account);
        }

        return institution;
    }
}
