package com.banter.api.model.item;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.banter.api.model.item.attribute.AccountAttribute;
import com.banter.api.model.item.attribute.InstitutionAttribute;
import com.plaid.client.response.Institution;
import lombok.Data;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@DynamoDBTable(tableName = "Accounts")
public class AccountItem {

    @NotEmpty private String userEmail;
    @NotEmpty @Valid private List<InstitutionAttribute> institutions;

    public AccountItem() {
        this.institutions = new ArrayList<>();
    }

    @DynamoDBHashKey
    public String getUserEmail() { return this.userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public void addInstitutionAttribute(InstitutionAttribute institutionAttribute) {
        this.institutions.add(institutionAttribute);
    }
}
