package com.banter.api.service;

import com.banter.api.model.item.attribute.AccountAttribute;
import com.banter.api.model.item.attribute.AccountBalancesAttribute;
import com.banter.api.model.item.attribute.InstitutionAttribute;
import com.banter.api.requestexceptions.PlaidGetAccountBalanceException;
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
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public InstitutionAttribute createInstitutionAttribute(String itemId, String institutionName, String institutionId, String accessToken) throws PlaidGetAccountBalanceException {
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

            this.logger.debug("Account attribute: " + accountAttribute);
            institutionAttribute.addAccountAttribute(accountAttribute);
        }

        return institutionAttribute;
    }
}
