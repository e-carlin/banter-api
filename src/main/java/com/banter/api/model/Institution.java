package com.banter.api.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import javax.validation.constraints.NotNull;

/**
 * Overcoming snake case json
 * https://stackoverflow.com/questions/10519265/jackson-overcoming-underscores-in-favor-of-camel-case
 * search "spring" 3rd or 4th answer...
 */

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@DynamoDBTable(tableName = "Institution")
public class Institution {


    @NotNull private String itemId;
    @NotNull private String userEmail;
    @NotNull private String accessToken;
    @NotNull private String name;
    @NotNull private String institutionId;

    public Institution() {}

    @DynamoDBHashKey
    public String getItemId() { return this.itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getUserEmail() { return this.userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getAccessToken() { return this.accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getName() { return this.name; }
    public void setName() { this.name = name; }

    public String getInstitutionId() { return this.institutionId; }
    public void setInstitutionId(String institutionId) { this.institutionId = institutionId;}

    public String toString() {
        return "{itemId: "+this.itemId +
                ", userEmail: "+this.userEmail +
                ", name: "+ this.name +
                ", institutionId: "+ this.institutionId + " }";
    }

}
