package com.banter.api.model.item;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.banter.api.model.item.attribute.InstitutionAttribute;
import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@Component
@DynamoDBDocument
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
