package com.banter.api.model.item.attribute;

import lombok.Data;
import lombok.Setter;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString
public class InstitutionAttribute {

    @NotEmpty @Setter
    private String itemId;
    @NotEmpty private String name;
    @NotEmpty private String institutionId;
    @NotEmpty @Valid private List<AccountAttribute> accounts;

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public InstitutionAttribute() {
        this.accounts = new ArrayList<>();
    }

    public void addAccountAttribute(AccountAttribute accountAttribute) {
        System.out.println("&&&& HEY: "+accountAttribute.toString());
        this.accounts.add(accountAttribute);
    }
}
