package com.banter.api.controller;

import com.banter.api.model.request.PlaidWebhookRequest;
import com.banter.api.requestexceptions.customExceptions.FirestoreException;
import com.banter.api.requestexceptions.customExceptions.UnsupportedWebhookTypeException;
import com.banter.api.service.PlaidWebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;

@RestController
public class PlaidWebhookController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PlaidWebhookService plaidWebhookService;

    @PostMapping("/plaid/webhook")
    @ResponseStatus(HttpStatus.OK)
    public void plaidWebhook(@Valid @RequestBody PlaidWebhookRequest plaidWebhookRequest) throws
            UnsupportedWebhookTypeException,
            FirestoreException {
        plaidWebhookService.processWebhook(plaidWebhookRequest);
    }
}
