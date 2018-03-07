package com.banter.api.model.item;

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
public class AccountItem {

    @NotEmpty private String userId;
    @NotNull @Valid private List<InstitutionAttribute> institutions;

    public AccountItem() {
        this.institutions = new ArrayList<>();
    }


    public String getUserId() { return this.userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public void addInstitutionAttribute(InstitutionAttribute institutionAttribute) {
        this.institutions.add(institutionAttribute);
    }

    public Set<ConstraintViolation<AccountItem>> validate() {
        return Validation.buildDefaultValidatorFactory().getValidator().validate(this);
    }
}
