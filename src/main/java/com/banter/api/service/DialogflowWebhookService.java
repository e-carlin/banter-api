package com.banter.api.service;

import com.banter.api.model.request.DialogflowWebhookRequest;
import com.banter.api.model.response.DialogfloWebhookResponse;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class DialogflowWebhookService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String GET_ACCOUNT_BALANCE_INTENT = "getAccountBalance";
    private static final String ACCOUNT_TYPE_ENTITY = "accountType";
    private static final String ACCOUNT_NAME_ENTITY = "accountName";

    public DialogflowWebhookService() {
    }

    public DialogfloWebhookResponse processWebhook(DialogflowWebhookRequest request) {

        switch (request.getQueryResult().getIntent().getDisplayName()) {
            case (GET_ACCOUNT_BALANCE_INTENT):
                return getAccountBalance(
                        request.getQueryResult().getParameters().get(ACCOUNT_NAME_ENTITY),
                        request.getQueryResult().getParameters().get(ACCOUNT_TYPE_ENTITY)
                        );
            default:
                return new DialogfloWebhookResponse("I'm not sure how to handle that intent");

        }
    }

    private DialogfloWebhookResponse getAccountBalance(String accountName, String accountType) {
        logger.warn("Getting balance");
        logger.warn("AccountName: "+accountName);
        logger.warn("AccountType: "+accountType );
        return new DialogfloWebhookResponse("You want to get balance");
    }

}
