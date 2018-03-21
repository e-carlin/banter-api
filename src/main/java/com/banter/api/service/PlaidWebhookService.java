package com.banter.api.service;

import com.banter.api.model.document.TransactionDocument;
import com.banter.api.model.request.PlaidWebhookRequest;
import com.banter.api.repository.transaction.TransactionDocumentRepository;
import com.banter.api.requestexceptions.customExceptions.PlaidGetTransactionsException;
import com.banter.api.requestexceptions.customExceptions.UnsupportedWebhookTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
public class PlaidWebhookService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String INITIAL_UPDATE_WEBHOOK_CODE = "INITIAL_UPDATE";

    @Autowired PlaidClientService plaidClientService;
    @Autowired TransactionDocumentRepository transactionDocumentRepository;

    public PlaidWebhookService() {}

    public void processWebhook(PlaidWebhookRequest request)  throws UnsupportedWebhookTypeException {
        logger.debug("Processing webhook request");
        switch (request.getWebhookType()) {
            case("TRANSACTIONS"): //TODO: Remove hardcode
                logger.debug("Webhook is of type TRANSACTIONS");
                this.processTransactionsWebhook(request);
                break;
            default:
                throw new UnsupportedWebhookTypeException(String.format("The webhook type %s is unsupported", request.getWebhookType()));
        }
    }

    private void processTransactionsWebhook(PlaidWebhookRequest request) {
        String itemId = request.getItemId();
        try {
            switch (request.getWebhookCode()) {
                case(INITIAL_UPDATE_WEBHOOK_CODE):
                    logger.debug("Webhook code is "+INITIAL_UPDATE_WEBHOOK_CODE);
                    //Initial update is 30 days. So get transaction from now+2 days to now-45 days. That will cover all with a buffer
                    LocalDate endDate = LocalDate.now().plus(Period.ofDays(2));
                    LocalDate  startDate = endDate.minus(Period.ofDays(45));
                    logger.debug(String.format("Getting transactions from %s to %s ", startDate, endDate));
                    List<TransactionDocument> transactionDocuments = plaidClientService.getTransactions(itemId, startDate, endDate);
                    transactionDocumentRepository.addAll(transactionDocuments);
                    logger.debug("Success processing "+INITIAL_UPDATE_WEBHOOK_CODE+" transaction webhook");
                    break;

                    //TODO: Implement the other codes

                default:
                    logger.error(String.format("Unrecognized transaction webhook code",request.getWebhookCode()));
                    //TODO: should we do something?
            }
        } catch (PlaidGetTransactionsException e) {
            logger.error("There was an error getting transactions from Plaid: "+e.getMessage());
            e.printStackTrace();
        }
    }
}
