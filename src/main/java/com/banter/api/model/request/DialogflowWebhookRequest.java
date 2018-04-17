package com.banter.api.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@ToString
public class DialogflowWebhookRequest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    String responseId;
    @NonNull
    @JsonProperty("session") //Our Firestore function sets the session to the userId; not sure if this is a hack or not
            //TODO: Need to do a regex on set to get just the userId (ish following the final /)
    String userId;
    QueryResult queryResult;

    public DialogflowWebhookRequest() {
    }

    public void setUserId(String sessionId) {
        String[] parts = sessionId.split("/"); //The userId is the string after the final slash in the sessionId
        //TODO: this logic will break if there is a slash in the userId. Can there be slashes?
        String userId = parts[parts.length-1];
        this.userId = userId;
    }

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
