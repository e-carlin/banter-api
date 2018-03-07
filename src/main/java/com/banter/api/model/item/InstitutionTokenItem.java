package com.banter.api.model.item;

import lombok.Data;
import lombok.ToString;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;

@Data
@ToString(exclude = "accessToken")
@Component
public class InstitutionTokenItem {


    @NotEmpty private String itemId;
    @NotEmpty private String accessToken;
    @NotEmpty private String userId;

    public InstitutionTokenItem(String itemId, String accessToken, String userId) {
        this.itemId = itemId;
        this.accessToken = accessToken;
        this.userId = userId;
    }

    public InstitutionTokenItem() {}

    public String getItemId() { return this.itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }
}
