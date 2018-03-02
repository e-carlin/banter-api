package com.banter.api.model.item;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@ToString(exclude = "accessToken")
@DynamoDBTable(tableName = "InstitutionTokens")
public class InstitutionTokenItem {


    @NotNull private String itemId;
    @NotNull private String userEmail;
    @NotNull private String accessToken;

    public InstitutionTokenItem() {}

    @DynamoDBHashKey
    public String getItemId() { return this.itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getUserEmail() { return this.userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getAccessToken() { return this.accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

}
