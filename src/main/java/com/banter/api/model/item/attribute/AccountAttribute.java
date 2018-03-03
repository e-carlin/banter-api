package com.banter.api.model.item.attribute;

import lombok.Data;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@ToString
public class AccountAttribute {

    @NotEmpty private String id;
    @NotEmpty private String name;
    @NotEmpty private String type; //TODO: Maybe make this an enum
    @NotEmpty private String subtype; //TODO: Maybe make this an enum too
    @NotEmpty @Valid private AccountBalancesAttribute balances;

    public AccountAttribute(String id, String name, String type, String subtype, AccountBalancesAttribute balances) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.subtype = subtype;
        this.balances = balances;
    }

    public AccountAttribute() {}

}
