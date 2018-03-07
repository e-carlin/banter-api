package com.banter.api.controller;

import com.banter.api.model.item.AccountItem;
import com.banter.api.model.item.InstitutionTokenItem;
import com.banter.api.model.request.addAccount.AddAccountRequest;
import com.banter.api.repository.account.AccountRepository;
import com.banter.api.repository.institutionToken.InstitutionTokenRepository;
import com.banter.api.requestexceptions.customExceptions.AddDuplicateInstitutionException;
import com.banter.api.requestexceptions.customExceptions.NoAccountItemException;
import com.banter.api.requestexceptions.customExceptions.PlaidExchangePublicTokenException;
import com.banter.api.requestexceptions.customExceptions.PlaidGetAccountBalanceException;
import com.banter.api.service.PlaidClientService;
import com.plaid.client.response.ItemPublicTokenExchangeResponse;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import retrofit2.Response;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.Optional;

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
    /**
     * Handles POST to /account/add for adding new accounts to a user's profile
     *
     * @param addAccountRequest A request containing the account details we want to add (ie public_token, account_id, ...)
     * @throws ConstraintViolationException
     * @throws PlaidExchangePublicTokenException
     * @throws PlaidGetAccountBalanceException
     */
    @PostMapping("/accounts/add")
    @ResponseStatus(HttpStatus.CREATED)
    public void addAccount(@Valid @RequestBody AddAccountRequest addAccountRequest)
            throws ConstraintViolationException,
            PlaidExchangePublicTokenException,
            PlaidGetAccountBalanceException,
            AddDuplicateInstitutionException {
        this.logger.info("POST /account/add called");
        String userSub = SecurityContextHolder.getContext().getAuthentication().getName();

        //First, check if the user has already added this institution. If so no need to go through process of adding it again
        //TODO: Remove hard coded email
        if (accountRepository.userHasInstitution(userSub, addAccountRequest.getInstitution().getInstitutionId())) {
            logger.debug("The user tried to add a duplicate institution. InstitutionName: "+addAccountRequest.getInstitution().getName()+" insId:"+addAccountRequest.getInstitution().getInstitutionId());
            throw new AddDuplicateInstitutionException("User has already added institution: " + addAccountRequest.getInstitution().getName());
        } else {
            //TODO: Maybe move this to async. Maybe not incase we want to alert the user and have them retry. Although, we could still alert them asynchronously
            Response<ItemPublicTokenExchangeResponse> response = plaidClientService.exchangePublicToken(addAccountRequest.getPublicToken());

            //If the itemId (hash key) is already found this just updates the existing item. It will create a new item
            // if the itemId isn't already in the table
            //TODO: Remove hard coded email
            InstitutionTokenItem institutionTokenItem = new InstitutionTokenItem(response.body().getItemId(),
                    response.body().getAccessToken(),
                    userSub);
            institutionTokenRepository.save(institutionTokenItem);

            //TODO: Remove hard coded email
            accountRepository.saveAccountItemFromAddAccountRequest(addAccountRequest.getAccounts(),
                    response.body().getItemId(),
                    response.body().getAccessToken(),
                    addAccountRequest.getInstitution().getName(),
                    addAccountRequest.getInstitution().getInstitutionId(),
                    userSub);
            //TODO: return nice message
        }
    }

    @GetMapping("/accounts")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody AccountItem getAccounts() throws NoAccountItemException, AddDuplicateInstitutionException {
        logger.info("GET /accounts called");
        String userSub = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.debug(String.format("Looking up accounts for userSub: %s", userSub));

        Optional<AccountItem> accountItemOptional =  accountRepository.findById(userSub);
        if(accountItemOptional.isPresent()) {
            return accountItemOptional.get();
        }
        else { //This user doesn't have an account item
            logger.warn(String.format("No accounts found for userSub %s.", userSub));
            throw new NoAccountItemException("No accounts found");
        }
    }
}
