package com.banter.api.model.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * POJO for mapping a request from /add/account to an object
 *
 * {
 "institution": {
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

@ToString
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AddAccountRequest {

    @NotNull @Valid private AddInstitutionRequest institution;
    @NotEmpty private List<Map<String, String>> accounts;
    @NotEmpty private String linkSessionId;
    @NotEmpty private String publicToken;

    public AddAccountRequest() {}

    public AddInstitutionRequest getInstitution() { return this.institution; }
    public void setInstitution (AddInstitutionRequest institution) { this.institution = institution; }

    public List<Map<String, String>> getAccounts() { return this.accounts; }
    public void setAccounts(List<Map<String, String>> accounts) { this.accounts = accounts; }

    public String getLinkSessionId() { return this.linkSessionId; }
    public void setLinkSessionId(String linkSessionId) { this.linkSessionId = linkSessionId; }

    public String getPublicToken() { return this.publicToken; }
    public void setPublicToken(String publicToken) { this.publicToken = publicToken;}

}
