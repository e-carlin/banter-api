package com.banter.api.model.document;

import com.google.cloud.firestore.annotation.ServerTimestamp;
import com.plaid.client.response.ItemPublicTokenExchangeResponse;
import lombok.Data;
import lombok.ToString;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@ToString(exclude = "accessToken")
@Component
public class InstitutionTokenDocument {


    @NotEmpty
    private String itemId;
    @NotEmpty
    private String accessToken;
    @NotEmpty
    private String userId;
    @NotNull
    @ServerTimestamp
    private Date createdAt;

    public InstitutionTokenDocument(ItemPublicTokenExchangeResponse response, String userId) {
        this.itemId = response.getItemId();
        this.accessToken = response.getAccessToken();
        this.userId = userId;
    }

    public InstitutionTokenDocument() {
    }
}
