package com.banter.api.repository.account;

import com.banter.api.model.item.AccountItem;
import com.banter.api.model.item.attribute.InstitutionAttribute;
import com.banter.api.requestexceptions.customExceptions.PlaidGetAccountBalanceException;
import com.banter.api.service.InstitutionService;
import com.banter.api.service.PlaidClientService;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
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
import java.util.stream.Collectors;

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

    public AccountItem save(AccountItem accountItem) throws ExecutionException, InterruptedException {
        DocumentReference docRef = db.collection(ACCOUNT_COLLECTION_REF).document(accountItem.getUserId());
        ApiFuture<WriteResult> result = docRef.set(accountItem);
        WriteResult getResult = result.get();
        logger.debug(String.format("%s document %s updated at %s", ACCOUNT_COLLECTION_REF, accountItem.getUserId(), getResult.getUpdateTime()));
        return accountItem;
    }


    public Optional<AccountItem> findById(String userId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = db.collection(ACCOUNT_COLLECTION_REF).document(userId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
            if(document.exists()) {
                return Optional.of(document.toObject(AccountItem.class));
            }
            else {
                logger.debug("No document found");
                return Optional.empty();
            }
    }

    public AccountItem saveAccountItemFromAddAccountRequest(String itemId,
                                                            String accessToken,
                                                            String institutionName,
                                                            String institutionId,
                                                            String userId) throws ExecutionException, InterruptedException, PlaidGetAccountBalanceException {
        this.logger.debug("Saving account item from add account request");

        //TODO: First, check if they already have an existing AccountItem. If so get it,
        // and add this InstitutionAttribute to it. If the institutionAtrribute is also a duplicate just update the balances
        AccountItem accountItem;
        Optional<AccountItem> existingAccountItem = accountRepository.findById(userId);

        //If there is already an account item for this user in the DB then use it
        if (existingAccountItem.isPresent()) {
            logger.debug("This user already has an account item");
            accountItem = existingAccountItem.get();

            List<String> existingInstitutionIds = accountItem.getInstitutions().stream().map(existingInstitutionId -> existingInstitutionId.getInstitutionId()).collect(Collectors.toList());
            //If this institution is already in the account item. Then no need to add it again. Just update the balances
            if (existingInstitutionIds.contains(institutionId)) {
                logger.debug("This user has added this account before. Just going to update their balances. TODO: This needs to be implemented");
                //TODO: Update balances, this is half implemented in institutionService.createInstitutionAttribute()
                return accountItem;
            }
        } else { //There is no existing account item so create one
            logger.debug("This is the user's first account. Creating new AccountItem");
            accountItem = new AccountItem();
            accountItem.setUserId(userId);
        }

        //Add the institution attribute to the account item (either newly created or existing account item)
        logger.debug("This is a new institution for this user. Creating a new InstitutionAttribute");
        InstitutionAttribute institutionAttribute = institutionService.createInstitutionAttribute(itemId, institutionName, institutionId, accessToken);
        accountItem.addInstitutionAttribute(institutionAttribute);

        Set<ConstraintViolation<AccountItem>> errors = accountItem.validate();
        if (!errors.isEmpty()) {
            logger.error("Validation errors encountered when saving accountItem: " + Arrays.toString(errors.toArray()));
            throw new ConstraintViolationException(errors);
        }
        this.logger.debug("Institution attribute to add is: "+institutionAttribute);
        this.logger.debug("Success building account item: " + accountItem);
        this.accountRepository.save(accountItem);
        return accountItem;
    }

    public boolean userHasInstitution(String userId, String institutionId) throws ExecutionException, InterruptedException {
        Optional<AccountItem> accountItemOptional = accountRepository.findById(userId);
        if(accountItemOptional.isPresent()) {
            AccountItem accountItem = accountItemOptional.get();
            List<InstitutionAttribute> institutionAttributes = accountItem.getInstitutions();
            for(InstitutionAttribute institution : institutionAttributes) {
                if(institution.getInstitutionId().equals(institutionId)) {
                    return true;
                }
            }
        }
        return false;
    }
}
