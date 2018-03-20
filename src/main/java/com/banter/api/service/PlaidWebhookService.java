package com.banter.api.service;

import com.banter.api.model.request.PlaidWebhookRequest;
import com.banter.api.requestexceptions.customExceptions.PlaidGetTransactionsException;
import com.banter.api.requestexceptions.customExceptions.UnsupportedWebhookTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@Service
public class PlaidWebhookService {

    @Autowired PlaidClientService plaidClientService;

    public PlaidWebhookService() {}

    public void processWebhook(PlaidWebhookRequest request)  throws UnsupportedWebhookTypeException {
        switch (request.getWebhookType()) {
            case("TRANSACTIONS"): //TODO: Remove hardcode
                this.transactionsWebhook(request);
                break;
            default:
                throw new UnsupportedWebhookTypeException(String.format("The webhook type %s is unsupported", request.getWebhookType()));
        }
    }

    private void transactionsWebhook(PlaidWebhookRequest request) {
        String itemId = request.getItemId();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            plaidClientService.getTransactions(itemId, simpleDateFormat.parse("2017-01-01"), simpleDateFormat.parse("2017-02-01"));
            //tODO: Finish implementing
        } catch (PlaidGetTransactionsException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
