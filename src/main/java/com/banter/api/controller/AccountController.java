package com.banter.api.controller;

import com.banter.api.model.requests.AddAccountRequest;
import com.banter.api.model.Institution;
import com.banter.api.model.requests.AddInstitutionRequest;
import com.banter.api.repository.InstitutionRepository;
import com.plaid.client.PlaidClient;
import com.plaid.client.request.ItemPublicTokenExchangeRequest;
import com.plaid.client.response.ItemPublicTokenExchangeResponse;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import retrofit2.Response;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@ToString
@RestController
public class AccountController {


    @Value("${plaid.clientId}")
    private String plaidClientId;
    @Value("${plaid.secretKey}")
    private String plaidSecretKey;
    @Value("${plaid.publicKey}")
    private String plaidPublicKey;

    @Autowired
    InstitutionRepository insitutionRepository;
//
//    @Autowired
//    AccountRepository accountRepository;

//    @PostMapping("/account/add")
//    public void addAccount(@RequestBody AddAccountRequest addAccountRequest) {
//
//    }

    @PostMapping("/account/add")
    @ResponseStatus(HttpStatus.CREATED)
    public void addAccount(@Valid @RequestBody AddAccountRequest addAccountRequest) {
        System.out.println("Add account called");
        System.out.println("Request is: "+ addAccountRequest);

        PlaidClient plaidClient = PlaidClient.newBuilder()
                .clientIdAndSecret(plaidClientId, plaidSecretKey)
                .publicKey(plaidPublicKey) // optional. only needed to call endpoints that require a public key
                .sandboxBaseUrl() // or equivalent, depending on which environment you're calling into
                .build();

        //TODO: Maybe move this to async. Maybe not incase we want to alert the user and have them retry. Although, we could still alert them asynchronously
        Response<ItemPublicTokenExchangeResponse> response;
        try {
            response = plaidClient.service()
                    .itemPublicTokenExchange(new ItemPublicTokenExchangeRequest(addAccountRequest.getPublicToken())).execute();
            System.out.println("*** Got a response from Plaid ***");
        } catch (IOException e) {
            e.printStackTrace();
            //TODO: return error to client
            System.out.println("**** BIG TIME ERROR EXCHANGING TOKEN ****");
            return;
        }

        if (response.isSuccessful()) {
            String accessToken = response.body().getAccessToken();
            String itemId = response.body().getItemId();
            System.out.println("ItemId: "+itemId);
            System.out.println("accessToken: "+accessToken);

            @Valid Institution institution = new Institution();
            AddInstitutionRequest ins = addAccountRequest.getInstitution();
            institution.setAccessToken(accessToken);
            institution.setItemId(itemId);
            institution.setName(addAccountRequest.getInstitution().getName());
            institution.setInstitutionId(addAccountRequest.getInstitution().getInstitutionId());
            institution.setUserEmail("evan+fakefromaddaccount@carlin.com"); // TODO: Get the userEmail from the authorization token header
            System.out.println("Institution to be saved is: "+institution);
            insitutionRepository.save(institution);
        }
        else {
            try {
                System.out.println("Response was unsuccessful: "+response.errorBody().string());
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Couldn't parse response error body");
            }
        }
        //TODO: return nice message
    }
}
