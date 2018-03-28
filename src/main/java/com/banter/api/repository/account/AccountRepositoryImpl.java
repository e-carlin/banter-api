package com.banter.api.repository.account;

import com.banter.api.model.document.AccountsDocument;
import com.banter.api.model.document.InstitutionTokenDocument;
import com.banter.api.model.request.AddAccountRequest;
import com.banter.api.requestexceptions.customExceptions.FirestoreException;
import com.banter.api.requestexceptions.customExceptions.PlaidGetAccountBalanceException;
import com.banter.api.service.InstitutionService;
import com.banter.api.service.PlaidClientService;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;

//TODO: All of the logic in here is likely broken, please fix

@Repository
public class AccountRepositoryImpl implements AccountRepository {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String ACCOUNT_COLLECTION_REF = "accounts";
    private static final double NAME_MATCH_MIN_PERCENT = 0.5; //TODO: Need to tune this number

    @Autowired
    private PlaidClientService plaidClientService;
    @Autowired
    private InstitutionService institutionService;
    @Autowired
    Firestore db;

    public AccountRepositoryImpl() {}

    @Override
    public AccountsDocument add(AccountsDocument accountsDocument) {
        db.collection(ACCOUNT_COLLECTION_REF).add(accountsDocument);
        logger.debug(String.format("Added new AccountDocument: "+ accountsDocument));
        return accountsDocument;
    }

    @Override
    public AccountsDocument add(AddAccountRequest request, InstitutionTokenDocument institutionTokenDocument) throws PlaidGetAccountBalanceException, FirestoreException {
        this.logger.debug("Saving account document from add account request");

        //First, check if the user has an existing AccountsDocument
        Optional<AccountsDocument> accountsDocumentOptional = getMostRecentAccountsDocument(institutionTokenDocument.getUserId());
        AccountsDocument accountsDocument;
        if(!accountsDocumentOptional.isPresent()) {
            //They don't have an existing accountsDocument so create a new one
            logger.debug("This is the users first institution. Creating new AccountsDocument");
            accountsDocument = new AccountsDocument();
            accountsDocument.setUserId(institutionTokenDocument.getUserId());
        }
        else {
            //They have an existing accountsDocument so use it
            logger.debug("The user has an existing accounts document using it");
            accountsDocument = accountsDocumentOptional.get();
            accountsDocument.setCreatedAt(null); //Set to null so the db server adds a new updated createdAt timestamp. If we didn't set it to null this new document would be save with the createdAt timestamp of the old document
        }
        //Add the new institution to the accountsDocument
        AccountsDocument.Institution institution = institutionService.createInstitution(institutionTokenDocument.getItemId(), request.getInstitutionName(), request.getInstitutionId(), institutionTokenDocument.getAccessToken());
        accountsDocument.addInstitutionAttribute(institution);

        //Validate everything looks good
        Set<ConstraintViolation<AccountsDocument>> errors = accountsDocument.validate();
        if (!errors.isEmpty()) {
            logger.error("Validation errors encountered when saving accountsDocument: " + Arrays.toString(errors.toArray()));
            throw new ConstraintViolationException(errors);
        }
        this.logger.debug("Institution attribute to add is: " + institution);
        this.logger.debug("Success building account document: " + accountsDocument);
        this.add(accountsDocument);
        return accountsDocument;
    }

    @Override
    public boolean userHasInstitution(String userId, String institutionId) throws FirestoreException {
            Optional<AccountsDocument> accountsDocument =  getMostRecentAccountsDocument(userId);
            if (!accountsDocument.isPresent()) {
                logger.debug("User does not have any AccountDocuments");
                return false;
            } else {
                List<AccountsDocument.Institution> institutions = accountsDocument.get().getInstitutions();
                for (AccountsDocument.Institution institution : institutions) {
                    if (institution.getInstitutionId().equals(institutionId)) {
                        return true;
                    }
                }
            }
        return false;
    }

    private Optional<AccountsDocument> getMostRecentAccountsDocument(String userId) throws FirestoreException {
        try {
            CollectionReference accounts = db.collection(ACCOUNT_COLLECTION_REF);
            Query query = accounts.whereEqualTo("userId", userId).orderBy("createdAt", Query.Direction.DESCENDING).limit(1);
            ApiFuture<QuerySnapshot> querySnapshot = query.get();
            List<QueryDocumentSnapshot> documentSnapshots = querySnapshot.get().getDocuments();
            if (documentSnapshots.size() < 1) {
                logger.debug("User does not have any AccountDocuments");
                return Optional.empty();
            } else {
                return Optional.of(documentSnapshots.get(0).toObject(AccountsDocument.class));
            }
        }catch (InterruptedException e) {
            logger.error("Firestore interrupted exception caught: "+e.getLocalizedMessage());
            e.printStackTrace();
            throw new FirestoreException("There was an exception thrown while querying the database");

        } catch (ExecutionException e) {
            logger.error("Firestore execution exception caught: "+e.getLocalizedMessage());
            e.printStackTrace();
            throw new FirestoreException("There was an exception thrown while querying the database");
        }

    }

    //TODO: Test
    @Override
    public Optional<AccountsDocument.Institution.Account> findAccountByName(String nameOfAccountToFind, String userId) throws FirestoreException {
        Optional<AccountsDocument> accountsDocument = getMostRecentAccountsDocument(userId);
        if(!accountsDocument.isPresent()) {
            return Optional.empty();
        }
        else {
            AccountsDocument.Institution.Account closestMatchingAccount = null;
            Double closestMatchingPercent = 0.0;
            for(AccountsDocument.Institution institution : accountsDocument.get().getInstitutions()) {
                for(AccountsDocument.Institution.Account account : institution.getAccounts()) {
                    double matchPercent = getLevenshteinDistancePercent(nameOfAccountToFind, account.getName());
                    if(matchPercent > closestMatchingPercent) {
                        closestMatchingPercent = matchPercent;
                        closestMatchingAccount = account;
                    }
                }
            }
            if(closestMatchingPercent > NAME_MATCH_MIN_PERCENT) {
                return Optional.of(closestMatchingAccount);
            }
            else {
                return Optional.empty();
            }
        }
    }

    //TODO: Test
    private double getLevenshteinDistancePercent(String left, String right) {
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
        int distance = levenshteinDistance.apply(left, right);
        String larger = ( left.length() > right.length()) ? left : right;
        return (larger.length() - distance) / (double)larger.length(); //cast to double to do float instead of integer division
    }

}
