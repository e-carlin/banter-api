package com.banter.api.service.plaid;

import com.banter.api.requestexceptions.PlaidExchangePublicTokenException;
import com.banter.api.requestexceptions.PlaidGetAccountBalanceException;
import com.plaid.client.PlaidApiService;
import com.plaid.client.PlaidClient;
import com.plaid.client.request.AccountsBalanceGetRequest;
import com.plaid.client.request.ItemPublicTokenExchangeRequest;
import com.plaid.client.response.AccountsBalanceGetResponse;
import com.plaid.client.response.ItemPublicTokenExchangeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.IOException;

@Service
public class PlaidClientService {

    private PlaidClient plaidClient;
    private String plaidClientId;
    private String plaidSecretKey;
    private String plaidPublicKey;

    public PlaidClientService(@Value("${plaid.clientId}") String plaidClientId, @Value("${plaid.secretKey}") String plaidSecretKey, @Value("${plaid.publicKey}") String plaidPublicKey) {
        this.plaidClientId = plaidClientId;
        this.plaidSecretKey = plaidSecretKey;
        this.plaidPublicKey = plaidPublicKey;

        this.plaidClient = PlaidClient.newBuilder()
                .clientIdAndSecret(this.plaidClientId, this.plaidSecretKey)
                .publicKey(this.plaidPublicKey) // optional. only needed to call endpoints that require a public key
                .sandboxBaseUrl() // or equivalent, depending on which environment you're calling into
                .build();
    }

    public Response<ItemPublicTokenExchangeResponse> exchangPublicToken(String publicToken) throws PlaidExchangePublicTokenException {
        try {
            Response<ItemPublicTokenExchangeResponse> response = plaidClient.service().itemPublicTokenExchange(new ItemPublicTokenExchangeRequest(publicToken)).execute();
            if (response.isSuccessful()) {
                return response;
            } else {
                throw new PlaidExchangePublicTokenException(response.errorBody().string());
            }
        } catch (IOException e) {
            throw new PlaidExchangePublicTokenException(e.getMessage());
        }
    }

    public Response<AccountsBalanceGetResponse> getAccountBalance(String accessToken) throws PlaidGetAccountBalanceException {
        try {
            return this.plaidClient.service().accountsBalanceGet(
                    new AccountsBalanceGetRequest(accessToken))
                    .execute();
        } catch (IOException e) {
            throw new PlaidGetAccountBalanceException(e.getMessage());
        }
    }
}
