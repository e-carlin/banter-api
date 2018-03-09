package com.banter.api.repository.account;

import com.banter.api.model.document.AccountsDocument;
import com.banter.api.model.document.attribute.InstitutionAttribute;
import com.banter.api.requestexceptions.customExceptions.PlaidGetAccountBalanceException;
import com.banter.api.service.InstitutionService;
import com.banter.api.service.PlaidClientService;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
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
    private static final String ACCOUNT_COLLECTION_REF = "Accounts";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PlaidClientService plaidClientService;
    @Autowired
    private InstitutionService institutionService;
    @Autowired
    Firestore db;

    public AccountsDocument add(AccountsDocument accountsDocument) {
        db.collection(ACCOUNT_COLLECTION_REF).add(accountsDocument);
        logger.debug(String.format("Added new AccountDocument: "+ accountsDocument));
        return accountsDocument;
    }


    public Optional<AccountsDocument> findById(String userId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = db.collection(ACCOUNT_COLLECTION_REF).document(userId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        if (document.exists()) {
            return Optional.of(document.toObject(AccountsDocument.class));
        } else {
            logger.debug("No document found");
            return Optional.empty();
        }
    }

    public AccountsDocument saveAccountItemFromAddAccountRequest(String itemId,
                                                                 String accessToken,
                                                                 String institutionName,
                                                                 String institutionId,
                                                                 String userId) throws ExecutionException, InterruptedException, PlaidGetAccountBalanceException {
        this.logger.debug("Saving account document from add account request");

        AccountsDocument accountsDocument = new AccountsDocument();
        accountsDocument.setUserId(userId);
        //TODO: add createdAt timestamp
        InstitutionAttribute institutionAttribute = institutionService.createInstitutionAttribute(itemId, institutionName, institutionId, accessToken);
        accountsDocument.addInstitutionAttribute(institutionAttribute);

        Set<ConstraintViolation<AccountsDocument>> errors = accountsDocument.validate();
        if (!errors.isEmpty()) {
            logger.error("Validation errors encountered when saving accountsDocument: " + Arrays.toString(errors.toArray()));
            throw new ConstraintViolationException(errors);
        }
        this.logger.debug("Institution attribute to add is: " + institutionAttribute);
        this.logger.debug("Success building account document: " + accountsDocument);
        this.accountRepository.add(accountsDocument);
        return accountsDocument;
    }

    public boolean userHasInstitution(String userId, String institutionId) throws ExecutionException, InterruptedException {
        CollectionReference accounts = db.collection(ACCOUNT_COLLECTION_REF);
        Query query = accounts.whereEqualTo("userId", userId).orderBy("createdAt").limit(1);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documentSnapshots = querySnapshot.get().getDocuments();
        if (documentSnapshots.size() < 1) {
            logger.debug("User does not have any AccountDocuments");
            return false;
        } else {
            DocumentSnapshot documentSnapshot = documentSnapshots.get(0);
            AccountsDocument accountsDocument = documentSnapshot.toObject(AccountsDocument.class);
            List<InstitutionAttribute> institutions = accountsDocument.getInstitutions();
            for (InstitutionAttribute institution : institutions) {
                if (institution.getInstitutionId().equals(institutionId)) {
                    return true;
                }
            }
            return false;
        }
    }
}
