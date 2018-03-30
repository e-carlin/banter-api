package com.banter.api.controller;

import com.banter.api.model.request.DialogflowWebhookRequest;
import com.banter.api.model.response.DialogfloWebhookResponse;
import com.banter.api.requestexceptions.customExceptions.FirestoreException;
import com.banter.api.service.DialogflowWebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class DialogflowWebhookController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    DialogflowWebhookService dialogflowWebhookService;

    @PostMapping("/dialogflow/webhook")
    @ResponseStatus(HttpStatus.OK)
//    public DialogfloWebhookResponse plaidWebhook(@Valid @RequestBody DialogflowWebhookRequest dialogflowWebhookRequest)
    public DialogfloWebhookResponse plaidWebhook(HttpEntity<String> httpEntity)
            throws FirestoreException {

        String json = httpEntity.getBody();
        logger.warn(json.toString());
        return null;

//        logger.debug("*********************** REQUEST: " + dialogflowWebhookRequest);
//        return dialogflowWebhookService.processWebhook(dialogflowWebhookRequest);
    }
}
