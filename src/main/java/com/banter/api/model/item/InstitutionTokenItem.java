package com.banter.api.model.item;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ToString(exclude = "accessToken")
@DynamoDBTable(tableName = "InstitutionTokens")
public class InstitutionTokenItem {


    @NotEmpty private String itemId;
    @NotEmpty private String accessToken;
    @NotEmpty private String userEmail;

    public InstitutionTokenItem(String itemId, String accessToken, String userEmail) {
        this.itemId = itemId;
        this.accessToken = accessToken;
        this.userEmail = userEmail;
    }

    @DynamoDBHashKey
    public String getItemId() { return this.itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }
}
