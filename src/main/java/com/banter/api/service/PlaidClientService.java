package com.banter.api.service;

import com.banter.api.model.document.InstitutionTokenDocument;
import com.banter.api.model.document.TransactionDocument;
import com.banter.api.repository.institutionToken.InstitutionTokenRepository;
import com.banter.api.requestexceptions.customExceptions.FirestoreQueryException;
import com.banter.api.requestexceptions.customExceptions.PlaidExchangePublicTokenException;
import com.banter.api.requestexceptions.customExceptions.PlaidGetAccountBalanceException;
import com.banter.api.requestexceptions.customExceptions.PlaidGetTransactionsException;
import com.plaid.client.PlaidClient;
import com.plaid.client.request.AccountsBalanceGetRequest;
import com.plaid.client.request.ItemPublicTokenExchangeRequest;
import com.plaid.client.request.TransactionsGetRequest;
import com.plaid.client.response.AccountsBalanceGetResponse;
import com.plaid.client.response.ItemPublicTokenExchangeResponse;
import com.plaid.client.response.TransactionsGetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * A wrapper around the Plaid service. All interactions with Plaid should flow through methods in this class.
 */
@Service
public class PlaidClientService {

    @Autowired
    InstitutionTokenRepository institutionTokenRepository;

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
     *
     * @param publicToken The public token for the institutiojn
     * @return a response containing the resutling item_id and access_token
     * @throws PlaidExchangePublicTokenException
     */
    public Response<ItemPublicTokenExchangeResponse> exchangePublicToken(String publicToken) throws PlaidExchangePublicTokenException {
        this.logger.debug("Exchange public token called with token: " + publicToken);
        try {
            Response<ItemPublicTokenExchangeResponse> response = plaidClient.service().itemPublicTokenExchange(new ItemPublicTokenExchangeRequest(publicToken)).execute();
            if (response.isSuccessful()) {
                logger.debug("Success exchanging public token. ItemId: " + response.body().getItemId());
                return response;
            } else {
                logger.error("Exchange public token response returned errors: " + response.errorBody().string());
                throw new PlaidExchangePublicTokenException(response.errorBody().string());
            }
        } catch (IOException e) {
            logger.error("IOException when trying to exchange public token: " + e.getMessage());
            throw new PlaidExchangePublicTokenException(e.getMessage());
        }
    }

    /**
     * Get balances for all accounts attached to a particular accessToken
     *
     * @param accessToken the accessToken for the accounts
     * @return a response object containing the balances
     * @throws PlaidGetAccountBalanceException
     */
    public Response<AccountsBalanceGetResponse> getAccountBalance(String accessToken) throws PlaidGetAccountBalanceException {
        this.logger.debug("Plaid getAccountBalance called with access token: " + accessToken);
        try {
            return this.plaidClient.service().accountsBalanceGet(
                    new AccountsBalanceGetRequest(accessToken))
                    .execute();
            //tODO: Check if errorbody is null
        } catch (IOException e) {
            this.logger.error("Error getting balances from Plaid: " + e.getMessage());
            throw new PlaidGetAccountBalanceException(e.getMessage());
        }
    }

    public List<TransactionDocument> getTransactions(String itemId, LocalDate startDate, LocalDate endDate) throws PlaidGetTransactionsException {
        logger.debug("Getting transactions");
        //Ok, we want to get some transactions
        try {
            //First, go get the institutionTokenDocument so we can get the acessToken and userId associated with this itemId
            Optional<InstitutionTokenDocument> institutionTokenDocument = institutionTokenRepository.findByItemId(itemId);
            if (!institutionTokenDocument.isPresent()) { //The result set was empty
                logger.error("There was no institutionTokenDocument for itemId: "+itemId);
                throw new PlaidGetTransactionsException(String.format("There was no institutionTokenDocument with itemId: %s", itemId));
            } else {
                logger.debug("Found the institutionTokenDocument associated with itemId: "+itemId+" Document: "+institutionTokenDocument);
                //Ok, we got a valid institutionTokenDocument. Get the accessToken and userId from it
                String accessToken = institutionTokenDocument.get().getAccessToken();
                String userId = institutionTokenDocument.get().getUserId();

                //With the accessToken we can go grab the list of transactions from Plaid
                logger.debug("Calling Plaid API to get Transactions");
                Response<TransactionsGetResponse> response = plaidClient.service().transactionsGet(
                        new TransactionsGetRequest(
                                accessToken,
                                java.sql.Date.valueOf(startDate),
                                java.sql.Date.valueOf(endDate)))
                        .execute();

                if (response.errorBody() != null) { //Plaid errored out
                    logger.error("There was an error returned from Plaid: "+response.errorBody().string());
                    throw new PlaidGetTransactionsException("Error response from Plaid: " + response.errorBody().string());
                } else {
                    //Success! We got some transactions back from plaid
                    logger.debug("Success getting transactions from Plaid API");
                    List<TransactionsGetResponse.Transaction> transactionsResponse = response.body().getTransactions();
                    logger.debug("We received "+transactionsResponse.size()+" transactions");
                    List<TransactionDocument> transactionDocuments = new ArrayList<>();
                    for (TransactionsGetResponse.Transaction transaction : transactionsResponse) {
                        transactionDocuments.add(new TransactionDocument(transaction, userId));
                    }
                    logger.debug("We turned those into "+transactionDocuments.size()+" transactionDocuments");
                    return transactionDocuments;
                }
            }
        } catch (FirestoreQueryException e) {
            logger.error("There was an error retrieving the institutionTokenDocument from Firestore: " + e.getLocalizedMessage());
            e.printStackTrace();
            throw new PlaidGetTransactionsException("There was an error getting transactions. Please try again.");
        } catch (IOException e) {
            logger.error(String.format("There was an error retrieving the transactions from Plaid: %s", e.getLocalizedMessage()));
            e.printStackTrace();
            throw new PlaidGetTransactionsException("There was an error getting transactions. Please try again.");
        }
    }
}
