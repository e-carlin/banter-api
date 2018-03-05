package com.banter.api.controller;

import com.banter.api.model.item.InstitutionTokenItem;
import com.banter.api.model.request.addAccount.AddAccountRequest;
import com.banter.api.repository.AccountRepository;
import com.banter.api.repository.InstitutionTokenRepository;
import com.banter.api.requestexceptions.PlaidExchangePublicTokenException;
import com.banter.api.requestexceptions.PlaidGetAccountBalanceException;
import com.banter.api.service.plaid.PlaidClientService;
import com.plaid.client.response.ItemPublicTokenExchangeResponse;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import retrofit2.Response;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

@ToString
@RestController
public class AccountController {

    @Autowired
    InstitutionTokenRepository insitutionTokenRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    PlaidClientService plaidClientService;

    private final String HARD_CODED_EMAIL = "evforward123+hardcodedfromaddaccount@gmail.com";

    @PostMapping("/account/add")
    @ResponseStatus(HttpStatus.CREATED)
    public void addAccount(@Valid @RequestBody AddAccountRequest addAccountRequest) throws ConstraintViolationException, PlaidExchangePublicTokenException, PlaidGetAccountBalanceException {


        //TODO: We should wrap this in our own custom object
        //TODO: Maybe move this to async. Maybe not incase we want to alert the user and have them retry. Although, we could still alert them asynchronously
        Response<ItemPublicTokenExchangeResponse> response = plaidClientService.exchangPublicToken(addAccountRequest.getPublicToken());

        //TODO: Remove hard coded email
        InstitutionTokenItem institutionTokenItem = new InstitutionTokenItem(response.body().getItemId(),
                response.body().getAccessToken(),
                HARD_CODED_EMAIL);
        insitutionTokenRepository.save(institutionTokenItem);

        //TODO: Remove hard coded email
        accountRepository.saveAccountItemFromAddAccountRequest(addAccountRequest.getAccounts(),
                response.body().getItemId(),
                response.body().getAccessToken(),
                addAccountRequest.getInstitution().getName(),
                addAccountRequest.getInstitution().getInstitutionId(),
                HARD_CODED_EMAIL);
        //TODO: return nice message
    }
}
