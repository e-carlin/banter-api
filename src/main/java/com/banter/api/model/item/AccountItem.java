package com.banter.api.model.item;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.banter.api.model.item.attribute.InstitutionAttribute;
import lombok.Data;
import lombok.ToString;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@ToString
@Component
@DynamoDBDocument
@DynamoDBTable(tableName = "Accounts")
public class AccountItem {

    @NotEmpty private String userSub;
    @NotNull @Valid private List<InstitutionAttribute> institutions;

    public AccountItem() {
        this.institutions = new ArrayList<>();
    }


    @DynamoDBHashKey
    public String getUserSub() { return this.userSub; }
    public void setUserSub(String userSub) { this.userSub = userSub; }

    public void addInstitutionAttribute(InstitutionAttribute institutionAttribute) {
        this.institutions.add(institutionAttribute);
    }

    public Set<ConstraintViolation<AccountItem>> validate() {
        return Validation.buildDefaultValidatorFactory().getValidator().validate(this);
    }
}
