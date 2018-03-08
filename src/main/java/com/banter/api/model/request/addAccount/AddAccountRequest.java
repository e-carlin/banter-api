package com.banter.api.model.request.addAccount;

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

    @NotNull @Valid private AddAccountRequestInstitution institution;
    @NotEmpty @Valid private List<AddAccountRequestAccount> accounts;
    @NotEmpty private String linkSessionId;
    @NotEmpty private String publicToken;

    public AddAccountRequest() {}

    public AddAccountRequestInstitution getInstitution() { return this.institution; }
    public void setInstitution (AddAccountRequestInstitution institution) { this.institution = institution; }

    public List<AddAccountRequestAccount> getAccounts() { return this.accounts; }
    public void setAccounts(List<AddAccountRequestAccount> accounts) { this.accounts = accounts; }

    public String getLinkSessionId() { return this.linkSessionId; }
    public void setLinkSessionId(String linkSessionId) { this.linkSessionId = linkSessionId; }

    public String getPublicToken() { return this.publicToken; }
    public void setPublicToken(String publicToken) { this.publicToken = publicToken;}
}


