package com.banter.api.service;

import com.banter.api.model.document.InstitutionTokenDocument;
import com.banter.api.model.document.TransactionDocument;
import com.banter.api.model.request.PlaidWebhookRequest;
import com.banter.api.repository.institutionToken.InstitutionTokenRepository;
import com.banter.api.repository.transaction.TransactionDocumentRepository;
import com.banter.api.requestexceptions.customExceptions.FirestoreException;
import com.banter.api.requestexceptions.customExceptions.PlaidGetTransactionsException;
import com.banter.api.requestexceptions.customExceptions.UnsupportedWebhookTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
public class PlaidWebhookService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String INITIAL_UPDATE_WEBHOOK_CODE = "INITIAL_UPDATE";
    private final String HISTORICAL_UPDATE_WEBHOOK_CODE = "HISTORICAL_UPDATE";
    private final String DEFAULT_UPDATE_WEBHOOK_CODE = "DEFAULT_UPDATE";

    @Autowired
    PlaidClientService plaidClientService;
    @Autowired
    TransactionDocumentRepository transactionDocumentRepository;
    @Autowired
    InstitutionTokenRepository institutionTokenRepository;

    public PlaidWebhookService() {
    }

    public void processWebhook(PlaidWebhookRequest request) throws UnsupportedWebhookTypeException, FirestoreException {
        logger.debug("Processing PlaidWebhookRequest");
        switch (request.getWebhookType()) {
            case ("TRANSACTIONS"): //TODO: Remove hardcode
                logger.debug("Webhook is of type TRANSACTIONS");
                this.processTransactionsWebhook(request);
                break;
            default:
                throw new UnsupportedWebhookTypeException(String.format("The webhook type %s is unsupported", request.getWebhookType()));
        }
    }

    private void processTransactionsWebhook(PlaidWebhookRequest request) throws FirestoreException {
        String itemId = request.getItemId();
        List<TransactionDocument> transactionDocuments;
        LocalDate startDate;
        LocalDate endDate;
        try {
            switch (request.getWebhookCode()) {
                case (INITIAL_UPDATE_WEBHOOK_CODE):
                    logger.debug("Webhook code is " + INITIAL_UPDATE_WEBHOOK_CODE);
                    //Initial update is 30 days. So get transaction from now+2 days to now-45 days. That will cover all with a buffer
                    endDate = LocalDate.now().plus(Period.ofDays(2));
                    startDate = endDate.minus(Period.ofDays(45));
                    logger.debug(String.format("Getting transactions from %s to %s ", startDate, endDate));
                    transactionDocuments = plaidClientService.getTransactions(itemId, startDate, endDate);
                    transactionDocumentRepository.addAll(transactionDocuments);
                    logger.debug(String.format("Success processing %s transaction webhook. Plaid said there were %s new transactions, we processed %s", INITIAL_UPDATE_WEBHOOK_CODE, request.getNumberOfNewTransactions(), transactionDocuments.size()));
                    break;
                case (HISTORICAL_UPDATE_WEBHOOK_CODE):
                    logger.debug(String.format("Webhook code is %s", HISTORICAL_UPDATE_WEBHOOK_CODE));
                    Optional<InstitutionTokenDocument> institutionTokenDocument = institutionTokenRepository.findByItemId(itemId);
                    if (!institutionTokenDocument.isPresent()) {
                        logger.error("ERROR!! While processing the plaid historical update webhook we were processing an itemId and could not find the corresponding institutionTokenDocument");
                        break;
                    } else {
                        endDate = institutionTokenDocument.get().getCreatedAt().toInstant().atZone(ZoneOffset.UTC).toLocalDate().plus(Period.ofDays(2));
                        startDate = endDate.minus(Period.ofMonths(25));
                        logger.debug(String.format("Getting transactions from %s to %s", startDate, endDate));
                        transactionDocuments = plaidClientService.getTransactions(itemId, startDate, endDate);
                        transactionDocumentRepository.addAll(transactionDocuments);
                        logger.debug(String.format("Success processing %s transaction webhook. Plaid said there were %s new transactions, we processed %s", HISTORICAL_UPDATE_WEBHOOK_CODE, request.getNumberOfNewTransactions(), transactionDocuments.size()));
                        break;
                    }
                case (DEFAULT_UPDATE_WEBHOOK_CODE):
                    logger.debug(String.format("Webhook code is %s", DEFAULT_UPDATE_WEBHOOK_CODE));
                    endDate = LocalDate.now().plus(Period.ofDays(2));
                    startDate = endDate.minus(Period.ofDays(5)); //TODO: Tune this
                    logger.debug(String.format("Getting transactions from %s to %s", startDate, endDate));
                    transactionDocuments = plaidClientService.getTransactions(itemId, startDate, endDate);
                    transactionDocumentRepository.addAll(transactionDocuments);
                    logger.debug(String.format("Success processing %s transaction webhook. Plaid said there were %s new transactions, we processed %s", HISTORICAL_UPDATE_WEBHOOK_CODE, request.getNumberOfNewTransactions(), transactionDocuments.size()));
                    break;

                default:
                    logger.error(String.format("Unrecognized transaction webhook code", request.getWebhookCode()));
                    break;
            }
        } catch (PlaidGetTransactionsException e) {
            logger.error("There was an error getting transactions from Plaid: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
