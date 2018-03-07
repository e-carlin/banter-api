package com.banter.api.model.item;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Data;
import lombok.ToString;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;

@Data
@ToString(exclude = "accessToken")
@Component
@DynamoDBTable(tableName = "InstitutionTokens")
public class InstitutionTokenItem {


    @NotEmpty private String itemId;
    @NotEmpty private String accessToken;
    @NotEmpty private String userSub;

    public InstitutionTokenItem(String itemId, String accessToken, String userSub) {
        this.itemId = itemId;
        this.accessToken = accessToken;
        this.userSub = userSub;
    }

    public InstitutionTokenItem() {}

    @DynamoDBHashKey
    public String getItemId() { return this.itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }
}
