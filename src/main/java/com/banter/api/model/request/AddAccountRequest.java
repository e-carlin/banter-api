package com.banter.api.model.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * POJO for mapping a request from /add/account to an object
 *
 * {
 "institutionToken": {
 "name": "Chase",
 "institution_id": "ins_3"
 },
 "account": {
 "id": "6p5Wb6PGlgt5NmeywVgziPwZWPZWN5fMd8eGW",
 "name": "Plaid Checking",
 "type": "depository"
 },
 "account_id": "6p5Wb6PGlgt5NmeywVgziPwZWPZWN5fMd8eGW",
 "accounts": [{
 "id": "6p5Wb6PGlgt5NmeywVgziPwZWPZWN5fMd8eGW",
 "name": "Plaid Checking",
 "type": "depository"
 }, {
 "id": "6p5Wb6PGlgt5NmeywVgziPwZWPZWB1TbyZMP4",
 "name": "Plaid Saving",
 "type": "depository"
 }],
 "link_session_id": "40c71787-91ad-4c60-a183-19596fe58ad5",
 "public_token": "public-sandbox-91ac2949-8a57-400f-b530-38fd829aa089"
 }
 */

@Data
@ToString
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AddAccountRequest {

    @NotEmpty private String linkSessionId;
    @NotEmpty private String publicToken;
    @NotEmpty private String institutionName;
    @NotEmpty private String institutionId;

    public AddAccountRequest() {}


    public String getLinkSessionId() { return this.linkSessionId; }
    public void setLinkSessionId(String linkSessionId) { this.linkSessionId = linkSessionId; }

    public String getPublicToken() { return this.publicToken; }
    public void setPublicToken(String publicToken) { this.publicToken = publicToken;}
}



