package com.banter.api.model.item;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@ToString
@DynamoDBTable(tableName = "Accounts")
public class AccountItem {

    @NotNull private String userEmail;
    @NotNull private String itemId;
    @NotNull private String institutionName;

}
