package com.banter.api.model.document;

import com.google.cloud.firestore.annotation.ServerTimestamp;

import java.util.Date;

import lombok.Data;
import lombok.NonNull;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

/**
 * Created by evan.carlin on 3/23/2018.
 */

@Data
@ToString
public class ChatDocument {
    @NotEmpty
    private String userId;
    @NotEmpty
    private String message;
    @ServerTimestamp
    private Date createdAt;
    @NotEmpty
    private boolean botReplySent;

    public ChatDocument() {}

    public ChatDocument(String message, String userId) {
        this.message = message;
        this.userId = userId;
    }
}
