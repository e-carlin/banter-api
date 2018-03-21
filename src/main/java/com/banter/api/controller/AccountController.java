package com.banter.api.controller;

import com.banter.api.model.request.AddAccountRequest;
import com.banter.api.repository.account.AccountRepository;
import com.banter.api.repository.institutionToken.InstitutionTokenRepository;
import com.banter.api.requestexceptions.customExceptions.*;
import com.banter.api.service.AddAccountService;
import com.banter.api.service.PlaidClientService;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
    @Autowired
    AddAccountService addAccountService;

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
    @ResponseBody
    public String addAccount(@Valid @RequestBody AddAccountRequest addAccountRequest)
            throws ConstraintViolationException,
            PlaidExchangePublicTokenException,
            PlaidGetAccountBalanceException,
            AddDuplicateInstitutionException,
            FirestoreException {
        this.logger.info("POST /accounts/add called");
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.debug(String.format("Adding accounts for user %s", userId));
        addAccountService.addAccount(addAccountRequest, userId);
        return "{'status' : 'success'}"; //TODO: something better
    }
}
