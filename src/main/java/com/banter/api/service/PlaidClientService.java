package com.banter.api.service;

import com.banter.api.requestexceptions.PlaidExchangePublicTokenException;
import com.banter.api.requestexceptions.PlaidGetAccountBalanceException;
import com.plaid.client.PlaidClient;
import com.plaid.client.request.AccountsBalanceGetRequest;
import com.plaid.client.request.ItemPublicTokenExchangeRequest;
import com.plaid.client.response.AccountsBalanceGetResponse;
import com.plaid.client.response.ItemPublicTokenExchangeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.IOException;

/**
 * A wrapper around the Plaid service. All interactions with Plaid should flow through methods in this class.
 */
@Service
public class PlaidClientService {

    private PlaidClient plaidClient;
    private String plaidClientId;
    private String plaidSecretKey;
    private String plaidPublicKey;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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

    /**
     * Exchange a public token for an item_id and access_token
     * @param publicToken The public token for the institutiojn
     * @return a response containing the resutling item_id and access_token
     * @throws PlaidExchangePublicTokenException
     */
    public Response<ItemPublicTokenExchangeResponse> exchangePublicToken(String publicToken) throws PlaidExchangePublicTokenException {
        this.logger.debug("Exchange public token called with token: "+publicToken);
        try {
            Response<ItemPublicTokenExchangeResponse> response = plaidClient.service().itemPublicTokenExchange(new ItemPublicTokenExchangeRequest(publicToken)).execute();
            if (response.isSuccessful()) {
                logger.debug("Success exchanging public token. ItemId: "+response.body().getItemId());
                return response;
            } else {
                logger.error("Exchange public token response returned errors: "+response.errorBody().string());
                throw new PlaidExchangePublicTokenException(response.errorBody().string());
            }
        } catch (IOException e) {
            logger.error("IOException when trying to exchange public token: "+e.getMessage());
            throw new PlaidExchangePublicTokenException(e.getMessage());
        }
    }

    /**
     * Get balances for all accounts attached to a particular accessToken
     * @param accessToken the accessToken for the accounts
     * @return a response object containing the balances
     * @throws PlaidGetAccountBalanceException
     */
    public Response<AccountsBalanceGetResponse> getAccountBalance(String accessToken) throws PlaidGetAccountBalanceException {
        this.logger.debug("Plaid getAccountBalance called with access token: "+accessToken);
        try {
            return this.plaidClient.service().accountsBalanceGet(
                    new AccountsBalanceGetRequest(accessToken))
                    .execute();
        } catch (IOException e) {
            this.logger.error("Error getting balances from Plaid: "+e.getMessage());
            throw new PlaidGetAccountBalanceException(e.getMessage());
        }
    }
}
