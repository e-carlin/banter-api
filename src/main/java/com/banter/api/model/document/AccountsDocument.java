package com.banter.api.model.document;

import com.banter.api.model.document.attribute.InstitutionAttribute;
import com.google.cloud.firestore.FieldValue;
import lombok.Data;
import lombok.ToString;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@ToString
@Component
public class AccountsDocument {

    @NotEmpty private String userId;
    private Date createdAt;
    @NotNull @Valid private List<InstitutionAttribute> institutions;

    public AccountsDocument() {
        this.institutions = new ArrayList<>();
    }


    public String getUserId() { return this.userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public void addInstitutionAttribute(InstitutionAttribute institutionAttribute) {
        this.institutions.add(institutionAttribute);
    }

    public Set<ConstraintViolation<AccountsDocument>> validate() {
        return Validation.buildDefaultValidatorFactory().getValidator().validate(this);
    }
}
