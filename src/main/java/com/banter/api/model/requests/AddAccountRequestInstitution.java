package com.banter.api.model.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@ToString
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AddAccountRequestInstitution {

    @NotEmpty private String name;
    @NotEmpty private String institutionId;

    public String getInstitutionId() { return this.institutionId; }
    public void setInstitutionId(String institutionId) { this.institutionId = institutionId; }

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }
}
