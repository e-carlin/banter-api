package com.banter.api.service;

import com.banter.api.model.document.AccountsDocument;
import com.banter.api.model.request.DialogflowWebhookRequest;
import com.banter.api.model.response.DialogfloWebhookResponse;
import com.banter.api.repository.account.AccountRepository;
import com.banter.api.requestexceptions.customExceptions.FirestoreException;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class DialogflowWebhookService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String GET_ACCOUNT_BALANCE_INTENT = "getAccountBalance";
    private static final String ACCOUNT_TYPE_ENTITY = "accountType";
    private static final String ACCOUNT_NAME_ENTITY = "accountName";

    @Autowired
    AccountRepository accountRepository;

    public DialogflowWebhookService() {
    }

    public DialogfloWebhookResponse processWebhook(DialogflowWebhookRequest request) throws FirestoreException {
        switch (request.getQueryResult().getIntent().getDisplayName()) {
            case (GET_ACCOUNT_BALANCE_INTENT):
                return getAccountBalance(
                        request.getQueryInput().getPayload().getUserId(),
                        request.getQueryResult().getParameters().get(ACCOUNT_NAME_ENTITY),
                        request.getQueryResult().getParameters().get(ACCOUNT_TYPE_ENTITY)
                        );
            default:
                return new DialogfloWebhookResponse("I'm not sure how to handle that intent");

        }
    }

    private DialogfloWebhookResponse getAccountBalance(String userId, String accountName, String accountType) throws FirestoreException {
        Optional<AccountsDocument.Institution.Account> account = accountRepository.findAccountByName(accountName+accountType, userId); //tODO: remove hard code
        if(account.isPresent()) {
            return new DialogfloWebhookResponse(String.format("The balance of your %s %s account is %s", account.get().getName(), account.get().getType(), account.get().getBalances().getCurrent()));
        }
        else {
            return new DialogfloWebhookResponse("We were unable to find that account");
        }
    }

}
