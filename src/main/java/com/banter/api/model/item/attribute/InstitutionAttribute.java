package com.banter.api.model.item.attribute;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.banter.api.model.item.attribute.AccountAttribute;
import lombok.Data;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@Component
@DynamoDBDocument
@ToString
public class InstitutionAttribute {

    @NotEmpty @Setter private String itemId;
    @NotEmpty private String name;
    @NotEmpty private String institutionId;
    @NotNull @Valid private ArrayList<AccountAttribute> accounts;

    public InstitutionAttribute() {
        this.accounts = new ArrayList<>();
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }


    public void addAccountAttribute(AccountAttribute accountAttribute) {
        System.out.println("&&&& HEY: "+accountAttribute.toString());
        this.accounts.add(accountAttribute);
    }
}
