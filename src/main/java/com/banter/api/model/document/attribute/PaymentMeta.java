package com.banter.api.model.document.attribute;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PaymentMeta {

    private String byOrderOf;
    private String payee;
    private String payer;
    private String paymentMethod;
    private String paymentProcessor;
    private String ppdId;
    private String reason;
    private String referenceNumber;

    public PaymentMeta() {}
}
