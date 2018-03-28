package com.banter.api.model.request;

import lombok.Data;
import lombok.ToString;

import java.util.Map;

@Data
@ToString
public class DialogflowWebhookRequest {
    String responseId;
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
