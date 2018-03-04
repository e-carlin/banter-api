package com.banter.api.service.plaid;

import com.plaid.client.PlaidApiService;
import com.plaid.client.PlaidClient;
import com.plaid.client.request.ItemPublicTokenExchangeRequest;
import com.plaid.client.response.ItemPublicTokenExchangeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
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

    public Response<ItemPublicTokenExchangeResponse> exchangPublicToken(String publicToken) throws PlaidExecuteExchangePublicTokenException {
        try {
            return plaidClient.service().itemPublicTokenExchange(new ItemPublicTokenExchangeRequest(publicToken)).execute();
        } catch (IOException e) {
            throw new PlaidExecuteExchangePublicTokenException(e.getMessage());
        }
    }

    //TODO: Delete, this should be wrapped up in my own methods
    public PlaidApiService getService() {
        return plaidClient.service();
    }
}
