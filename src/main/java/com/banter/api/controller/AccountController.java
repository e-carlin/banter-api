package com.banter.api.controller;

import com.banter.api.model.item.InstitutionTokenItem;
import com.banter.api.model.request.addAccount.AddAccountRequest;
import com.banter.api.repository.account.AccountRepository;
import com.banter.api.repository.institutionToken.InstitutionTokenRepository;
import com.banter.api.requestexceptions.AddDuplicateInstitutionException;
import com.banter.api.requestexceptions.PlaidExchangePublicTokenException;
import com.banter.api.requestexceptions.PlaidGetAccountBalanceException;
import com.banter.api.service.PlaidClientService;
import com.plaid.client.response.ItemPublicTokenExchangeResponse;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import retrofit2.Response;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

/**
 * Controller for /account/* routes
 */
@ToString
@RestController
public class AccountController {

    @Autowired
    InstitutionTokenRepository institutionTokenRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    PlaidClientService plaidClientService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String HARD_CODED_EMAIL = "evforward123+hardcodedfromaddaccount@gmail.com";

    /**
     * Handles POST to /account/add for adding new accounts to a user's profile
     *
     * @param addAccountRequest A request containing the account details we want to add (ie public_token, account_id, ...)
     * @throws ConstraintViolationException
     * @throws PlaidExchangePublicTokenException
     * @throws PlaidGetAccountBalanceException
     */
    @PostMapping("/account/add")
    @ResponseStatus(HttpStatus.CREATED)
    public void addAccount(@Valid @RequestBody AddAccountRequest addAccountRequest)
            throws ConstraintViolationException,
            PlaidExchangePublicTokenException,
            PlaidGetAccountBalanceException,
            AddDuplicateInstitutionException {
        this.logger.info("/account/add called");

        //First, check if the user has already added this institution. If so no need to go through process of adding it again
        //TODO: Remove hard coded email
        if (accountRepository.userHasInstitution(HARD_CODED_EMAIL, addAccountRequest.getInstitution().getInstitutionId())) {
            logger.debug("The user tried to add a duplicate institution. InstitutionName: "+addAccountRequest.getInstitution().getName()+" insId:"+addAccountRequest.getInstitution().getInstitutionId());
            throw new AddDuplicateInstitutionException("User has already added account: " + addAccountRequest.getInstitution().getName());
        } else {
            //TODO: Maybe move this to async. Maybe not incase we want to alert the user and have them retry. Although, we could still alert them asynchronously
            Response<ItemPublicTokenExchangeResponse> response = plaidClientService.exchangePublicToken(addAccountRequest.getPublicToken());

            //If the itemId (hash key) is already found this just updates the existing item. It will create a new item
            // if the itemId isn't already in the table
            //TODO: Remove hard coded email
            InstitutionTokenItem institutionTokenItem = new InstitutionTokenItem(response.body().getItemId(),
                    response.body().getAccessToken(),
                    HARD_CODED_EMAIL);
            institutionTokenRepository.save(institutionTokenItem);

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
}
