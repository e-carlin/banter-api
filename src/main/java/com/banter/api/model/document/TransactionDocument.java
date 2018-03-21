package com.banter.api.model.document;

import com.google.cloud.firestore.annotation.ServerTimestamp;
import com.plaid.client.response.TransactionsGetResponse;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Data
@ToString
public class TransactionDocument {

    @NotNull
    private String accountId;
    @NotNull
    private Double amount; //Todo: better type than double??
    @NotNull
    private List<String> categories; //TODO: Should categories be an enum??
    @NotNull
    private String categoryId;
    @NotNull
    private String transactionDate;
    private LocationMeta locationMeta;
    @NotNull
    private String name;
    private String originalDescription;
    private PaymentMeta paymentMeta;
    @NotNull
    private boolean pending;
    private String pendingTransactionId;
    @NotNull
    private String transactionId;
    @NotNull
    private String transactionType; //TODO: enum?
    private String accountOwner;
    @NotNull
    @ServerTimestamp
    private Date createdAt; //Recommended to be stored as a long https://stackoverflow.com/questions/48473473/save-object-of-localdate-java-time-in-firebase-database


    public TransactionDocument() {
    }

    public TransactionDocument(TransactionsGetResponse.Transaction transaction){

        this.accountId = transaction.getAccountId();
        this.amount = transaction.getAmount();
        this.categories = transaction.getCategory();
        this.categoryId = transaction.getCategoryId();
        this.transactionDate = transaction.getDate();
        this.locationMeta = new LocationMeta(transaction.getLocation());
        this.name = transaction.getName();
        this.originalDescription = transaction.getOriginalDescription();
        this.paymentMeta = new PaymentMeta(transaction.getPaymentMeta());
        this.pending = transaction.getPending();
        this.pendingTransactionId = transaction.getPendingTransactionId();
        this.transactionId = transaction.getTransactionId();
        this.transactionType = transaction.getTransactionType();
        this.accountOwner = transaction.getAccountOwner();
    }

    @Data
    @ToString
    public static final class LocationMeta {
        private String address;
        private String city;
        private String state;
        private String zip;
        private Double lat;
        private Double lon;
        private String storeNumber;

        public LocationMeta() {
        }

        public LocationMeta(TransactionsGetResponse.Transaction.Location locationMeta) {
            this.address = locationMeta.getAddress();
            this.city = locationMeta.getCity();
            this.state = locationMeta.getState();
            this.zip = locationMeta.getZip();
            this.lat = locationMeta.getLat();
            this.lon = locationMeta.getLon();
            this.storeNumber = locationMeta.getStoreNumber();
        }
    }


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

        public PaymentMeta() {
        }

        public PaymentMeta(TransactionsGetResponse.Transaction.PaymentMeta paymentMeta) {
            this.byOrderOf = paymentMeta.getByOrderOf();
            this.payee = paymentMeta.getPayee();
            this.payer = paymentMeta.getPayer();
            this.paymentMethod = paymentMeta.getPaymentMethod();
            this.paymentProcessor = paymentMeta.getPaymentProcessor();
            this.ppdId = paymentMeta.getPpdId();
            this.reason = paymentMeta.getReason();
            this.referenceNumber = paymentMeta.getReferenceNumber();
        }
    }
}
