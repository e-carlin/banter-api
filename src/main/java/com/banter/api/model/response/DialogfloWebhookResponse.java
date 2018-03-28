package com.banter.api.model.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DialogfloWebhookResponse {

    String fulfillmentText;

    public DialogfloWebhookResponse(String fulfillmentText) {
        this.fulfillmentText = fulfillmentText;
    }
}
