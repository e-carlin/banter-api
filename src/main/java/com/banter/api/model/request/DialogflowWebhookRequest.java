package com.banter.api.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString;

import java.util.Map;

@Data
@ToString
public class DialogflowWebhookRequest {
    String responseId;
    @NonNull
    @JsonProperty("session") //Our Firestore function sets the session to the userId; not sure if this is a hack or not
            //TODO: Need to do a regex on set to get just the userId (ish following the final /)
    String userId;
    QueryResult queryResult;

    @Data
    @ToString
    public static class QueryResult {
        String queryText;
        Intent intent;
        Map<String, String> parameters;

        @Data
        @ToString
        public static class Intent {
            String name;
            String displayName;
        }
    }
}
