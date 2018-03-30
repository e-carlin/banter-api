package com.banter.api.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@Data
@ToString
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PlaidWebhookRequest {

    @NotEmpty private String webhookType;
    @NotEmpty private String webhookCode;
    @NotEmpty private String itemId;
    private String error;
    @JsonProperty("new_transactions")
    private int numberOfNewTransactions;

    public PlaidWebhookRequest() {}
}
