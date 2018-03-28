package com.banter.api.controller;

import com.banter.api.model.request.DialogflowWebhookRequest;
import com.banter.api.model.response.DialogfloWebhookResponse;
import com.banter.api.service.DialogflowWebhookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class DialogflowWebhookController {

    @Autowired
    DialogflowWebhookService dialogflowWebhookService;

    @PostMapping("/dialogflow/webhook")
    @ResponseStatus(HttpStatus.OK)
    public DialogfloWebhookResponse plaidWebhook(@Valid @RequestBody DialogflowWebhookRequest dialogflowWebhookRequest) {
        System.out.println("*********************** REQUEST: "+dialogflowWebhookRequest);
        return dialogflowWebhookService.processWebhook(dialogflowWebhookRequest);
    }
}
