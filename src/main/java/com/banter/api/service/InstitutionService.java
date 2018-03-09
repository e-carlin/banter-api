package com.banter.api.service;

import com.banter.api.model.document.AccountsDocument;
import com.banter.api.model.document.attribute.AccountAttribute;
import com.banter.api.model.document.attribute.AccountBalancesAttribute;
import com.banter.api.model.document.attribute.InstitutionAttribute;
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

//    public boolean userHasInstitution(String userSub, String insId) {
////        Optional<AccountsDocument> accountItemOptional = accountRepository.findById(userSub);
////        if(accountItemOptional.isPresent()) {
////            AccountsDocument accountItem = accountItemOptional.get();
////            return accountItemContainsInstitutionId(insId, accountItem);
////        }
////        return false;
////    }

    private boolean accountItemContainsInstitutionId(String insId, AccountsDocument accountsDocument) {
        List<InstitutionAttribute> institutions = accountsDocument.getInstitutions();
        for(InstitutionAttribute institutionAttribute : institutions) {
            if(institutionAttribute.getInstitutionId().equals(insId)) {
                return true;
            }
        }
        return false;
    }

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
