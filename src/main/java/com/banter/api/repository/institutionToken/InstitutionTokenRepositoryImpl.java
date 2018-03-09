package com.banter.api.repository.institutionToken;

import com.banter.api.model.document.InstitutionTokenItem;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ExecutionException;

@Repository
public class InstitutionTokenRepositoryImpl implements InstitutionTokenRepository {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String INSTITUTION_TOKEN_COLLECTION_REF = "InstitutionTokens";

    @Autowired Firestore db;

    public InstitutionTokenRepositoryImpl() {}

    public InstitutionTokenItem save(InstitutionTokenItem institutionTokenItem) throws ExecutionException, InterruptedException {
        DocumentReference docRef = db.collection(INSTITUTION_TOKEN_COLLECTION_REF).document(institutionTokenItem.getItemId());
        ApiFuture<WriteResult> result = docRef.set(institutionTokenItem);
        WriteResult getResult = result.get();
        logger.debug(String.format("%s document %s updated at %s", INSTITUTION_TOKEN_COLLECTION_REF, institutionTokenItem.getItemId(), getResult.getUpdateTime()));
        return institutionTokenItem;
    }
}
