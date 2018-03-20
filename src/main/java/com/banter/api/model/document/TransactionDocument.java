package com.banter.api.model.document;

import com.banter.api.model.document.attribute.LocationMeta;
import com.banter.api.model.document.attribute.PaymentMeta;
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

    @NotNull private String accountId;
    @NotNull private Double amount; //Todo: better type than double??
    @NotNull private List<String> categories; //TODO: Should categories be an enum??
    @NotNull private String categoryId;
    @NotNull private Date date;
    private LocationMeta locationMeta;
    @NotNull private String name;
    private String originalDescription;
    private PaymentMeta paymentMeta;
    @NotNull private boolean pending;
    private String  pendingTransactionId;
    @NotNull private String transactionId;
    @NotNull private String transactionType; //enum?
    private String  accountOwner;


    public TransactionDocument() {}

    public TransactionDocument(TransactionsGetResponse.Transaction transaction) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        accountId = transaction.getAccountId();
        amount = transaction.getAmount();
        categories = transaction.getCategory();
        categoryId = transaction.getCategoryId();
        date = simpleDateFormat.parse(transaction.getDate());
        locationMeta = new LocationMeta(transaction.getLocation());

    }
}
