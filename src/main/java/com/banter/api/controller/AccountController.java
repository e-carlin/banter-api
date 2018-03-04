package com.banter.api.controller;

import com.banter.api.model.item.AccountItem;
import com.banter.api.model.item.InstitutionTokenItem;
import com.banter.api.model.item.attribute.AccountAttribute;
import com.banter.api.model.item.attribute.AccountBalancesAttribute;
import com.banter.api.model.item.attribute.InstitutionAttribute;
import com.banter.api.model.request.addAccount.AddAccountRequest;
import com.banter.api.model.request.addAccount.AddAccountRequestAccount;
import com.banter.api.repository.AccountRepository;
import com.banter.api.repository.InstitutionTokenRepository;
import com.plaid.client.PlaidClient;
import com.plaid.client.request.AccountsBalanceGetRequest;
import com.plaid.client.request.ItemPublicTokenExchangeRequest;
import com.plaid.client.response.Account;
import com.plaid.client.response.AccountsBalanceGetResponse;
import com.plaid.client.response.ItemPublicTokenExchangeResponse;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import retrofit2.Response;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validation;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@ToString
@RestController
public class AccountController {


    @Value("${plaid.clientId}")
    private String plaidClientId;
    @Value("${plaid.secretKey}")
    private String plaidSecretKey;
    @Value("${plaid.publicKey}")
    private String plaidPublicKey;

    @Autowired InstitutionTokenRepository insitutionTokenRepository;

    @Autowired
    AccountRepository accountRepository;

    @PostMapping("/account/add")
    @ResponseStatus(HttpStatus.CREATED)
    public void addAccount(@Valid @RequestBody AddAccountRequest addAccountRequest) throws ConstraintViolationException {
        System.out.println("Add account called");
        System.out.println("Request is: " + addAccountRequest);

        //TODO: This should be done somewehre else
        PlaidClient plaidClient = PlaidClient.newBuilder()
                .clientIdAndSecret(plaidClientId, plaidSecretKey)
                .publicKey(plaidPublicKey) // optional. only needed to call endpoints that require a public key
                .sandboxBaseUrl() // or equivalent, depending on which environment you're calling into
                .build();

        //TODO: We should wrap this in our own custom object
        //TODO: Maybe move this to async. Maybe not incase we want to alert the user and have them retry. Although, we could still alert them asynchronously
        Response<ItemPublicTokenExchangeResponse> response;
        try {
            response = plaidClient.service()
                    .itemPublicTokenExchange(new ItemPublicTokenExchangeRequest(addAccountRequest.getPublicToken())).execute();
            System.out.println("*** Got a response from Plaid ***");
        } catch (IOException e) {
            e.printStackTrace();
            //TODO: return error to client
            System.out.println("**** BIG TIME ERROR EXCHANGING TOKEN ****");
            return;
        }

        if (response.isSuccessful()) {
            String accessToken = response.body().getAccessToken();
            String itemId = response.body().getItemId();
            System.out.println("ItemId: " + itemId);
            System.out.println("accessToken: " + accessToken);
            //TODO: Remove hard coded email
            InstitutionTokenItem institutionTokenItem = new InstitutionTokenItem();
            institutionTokenItem.setItemId(response.body().getItemId());
            institutionTokenItem.setAccessToken(response.body().getAccessToken());
            institutionTokenItem.setUserEmail("evforward123+hardcodedfromaddaccount@gmail.com");
            insitutionTokenRepository.save(institutionTokenItem);
            
            //TODO: Remove hard coded email
            saveAccountItem(addAccountRequest.getAccounts(), response.body().getItemId(), response.body().getAccessToken(), addAccountRequest.getInstitution().getName(), addAccountRequest.getInstitution().getInstitutionId(), "evforward123+hardcodedemailtoremovefromaddaccount@carlin.com");
        } else {
            try {
                System.out.println("Response from Plaid was unsuccessful: " + response.errorBody().string());
                //TODO: Return error to client
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Couldn't parse response error body");
            }
        }
        //TODO: return nice message
    }
    private void saveAccountItem(List<AddAccountRequestAccount> requestAccounts, String itemId, String accessToken, String institutionName, String institutionId, String userEmail) {
        //TODO: First, check if they already have an existing AccountItem. If so get it,
        // and add this InstitutionAttribute to it

        AccountItem accountItemToSave = new AccountItem();
        accountItemToSave.setUserEmail(userEmail);

        InstitutionAttribute institutionAttribute = new InstitutionAttribute();
        institutionAttribute.setItemId(itemId);
        institutionAttribute.setName(institutionName);
        institutionAttribute.setInstitutionId(institutionId);

            //TODO: This should be done somewehre else
            PlaidClient plaidClient = PlaidClient.newBuilder()
                    .clientIdAndSecret(plaidClientId, plaidSecretKey)
                    .publicKey(plaidPublicKey) // optional. only needed to call endpoints that require a public key
                    .sandboxBaseUrl() // or equivalent, depending on which environment you're calling into
                    .build();
            //TODO: we should wrap this in our own object
            Response<AccountsBalanceGetResponse> response;
            try {
                response = plaidClient.service().accountsBalanceGet(
                        new AccountsBalanceGetRequest(accessToken))
                        .execute();
                List<Account> accounts = response.body().getAccounts();
                for(Account account : accounts) {
                    AccountBalancesAttribute accountBalancesAttribute = new AccountBalancesAttribute();
                    accountBalancesAttribute.setAvailable(account.getBalances().getAvailable());
                    accountBalancesAttribute.setCurrent(account.getBalances().getCurrent());
                    accountBalancesAttribute.setLimit(account.getBalances().getLimit());

                    AccountAttribute accountAttribute = new AccountAttribute();
                    accountAttribute.setId(account.getAccountId());
                    accountAttribute.setName(account.getName());
                    accountAttribute.setType(account.getType());
                    accountAttribute.setSubtype(account.getSubtype());
                    accountAttribute.setBalances(accountBalancesAttribute);
                    System.out.println("Account attribute is: "+accountAttribute.toString());
                    institutionAttribute.addAccountAttribute(accountAttribute);
                }
            } catch (IOException e) {
                //TODO: Better error handling
                System.out.println("************* FATAL ERROR Getting balances in /add/account");
                e.printStackTrace();
            }
        accountItemToSave.addInstitutionAttribute(institutionAttribute);

        Set<ConstraintViolation<AccountItem>> errors = accountItemToSave.validate();
        if(!errors.isEmpty()) {
            System.out.println("Validation errors found when trying to save new accountItem");
            System.out.println(Arrays.toString(errors.toArray()));
            throw new ConstraintViolationException(errors);
        }
        else {
            accountRepository.save(accountItemToSave);
        }
    }
}
