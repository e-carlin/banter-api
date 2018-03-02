package com.banter.api.controller;

import com.banter.api.model.item.InstitutionTokenItem;
import com.banter.api.model.request.addAccount.AddAccountRequest;
import com.banter.api.repository.InstitutionTokenRepository;
import com.plaid.client.PlaidClient;
import com.plaid.client.request.ItemPublicTokenExchangeRequest;
import com.plaid.client.response.ItemPublicTokenExchangeResponse;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import retrofit2.Response;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.websocket.OnError;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

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
    InstitutionTokenRepository insitutionTokenRepository;
//
//    @Autowired
//    AccountRepository accountRepository;

    @PostMapping("/account/add")
    @ResponseStatus(HttpStatus.CREATED)
    public void addAccount(@Valid @RequestBody AddAccountRequest addAccountRequest) throws ConstraintViolationException{
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
            //TODO: Remove hard coded email
            saveInstitutionTokenItem(response.body().getItemId(), response.body().getAccessToken(), "evforward123+hardcodedfromaddaccount@gmail.com");

            //TODO: save accounts
        }
        else {
            try {
                System.out.println("Response was unsuccessful: "+response.errorBody().string());
                //TODO: Return error to client
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Couldn't parse response error body");
            }
        }
        //TODO: return nice message
    }
    private void saveInstitutionTokenItem(String itemId, String accessToken, String userEmail) throws ConstraintViolationException {
        //TODO: This doesn't seem like good DI
        InstitutionTokenItem institutionTokenItem = new InstitutionTokenItem();
        institutionTokenItem.setItemId(itemId);
        institutionTokenItem.setAccessToken(accessToken);
        institutionTokenItem.setUserEmail(userEmail);

        Set<ConstraintViolation<InstitutionTokenItem>> errors = Validation.buildDefaultValidatorFactory().getValidator().validate(institutionTokenItem);
        if(!errors.isEmpty()) {
            System.out.println("Error validating institutionTokenItem");
            throw new ConstraintViolationException(errors);
        }
        else{
            System.out.println("InstitutionTokenItem is: "+institutionTokenItem.toString());
            insitutionTokenRepository.save(institutionTokenItem);
        }
    }
}
