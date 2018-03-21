package com.banter.api.service;

import com.banter.api.model.document.InstitutionTokenDocument;
import com.banter.api.model.request.AddAccountRequest;
import com.banter.api.repository.account.AccountRepository;
import com.banter.api.repository.institutionToken.InstitutionTokenRepository;
import com.banter.api.requestexceptions.customExceptions.AddDuplicateInstitutionException;
import com.banter.api.requestexceptions.customExceptions.FirestoreException;
import com.banter.api.requestexceptions.customExceptions.PlaidExchangePublicTokenException;
import com.banter.api.requestexceptions.customExceptions.PlaidGetAccountBalanceException;
import com.plaid.client.response.ItemPublicTokenExchangeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Response;

@Service
public class AddAccountService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    PlaidClientService plaidClientService;
    @Autowired
    InstitutionTokenRepository institutionTokenRepository;

    public AddAccountService() {}

    public void addAccount(AddAccountRequest addAccountRequest, String userId) throws FirestoreException, AddDuplicateInstitutionException, PlaidExchangePublicTokenException, PlaidGetAccountBalanceException {
        logger.debug("In addAccount()");
        //First, check if the user has already added this institution. If so no need to go through process of adding it again
        if (accountRepository.userHasInstitution(userId, addAccountRequest.getInstitutionId())) {
            logger.debug("The user tried to add a duplicate institution. InstitutionName: " + addAccountRequest.getInstitutionName() + " insId:" + addAccountRequest.getInstitutionId());
            throw new AddDuplicateInstitutionException("User has already added institution: " + addAccountRequest.getInstitutionName());
        } else {
            logger.debug("This is a new institution");
            //TODO: Maybe move this to async. Maybe not incase we want to alert the user and have them retry. Although, we could still alert them asynchronously
            Response<ItemPublicTokenExchangeResponse> response = plaidClientService.exchangePublicToken(addAccountRequest.getPublicToken());

            InstitutionTokenDocument institutionTokenDocument = new InstitutionTokenDocument(response.body(), userId);
            institutionTokenRepository.add(institutionTokenDocument);

            accountRepository.add(addAccountRequest, institutionTokenDocument);
        }
    }
}
