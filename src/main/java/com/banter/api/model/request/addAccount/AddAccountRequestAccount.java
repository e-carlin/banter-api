package com.banter.api.model.request.addAccount;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@ToString
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AddAccountRequestAccount {

    @NotEmpty private String id;
    @NotEmpty private String name;
    @NotEmpty private String type; //TODO: Type should probably be an enum

    public String getId() { return this.id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return this.type; }
    public void setType(String type) { this.type = type; }
}
