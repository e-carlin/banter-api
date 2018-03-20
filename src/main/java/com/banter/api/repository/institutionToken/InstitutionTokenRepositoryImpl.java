package com.banter.api.repository.institutionToken;

import com.banter.api.model.document.InstitutionTokenDocument;
import com.banter.api.requestexceptions.customExceptions.FirestoreQueryException;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Repository
public class InstitutionTokenRepositoryImpl implements InstitutionTokenRepository {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String INSTITUTION_TOKEN_COLLECTION_REF = "institutionTokens"; //tODO: put in constants file

    @Autowired Firestore db;

    public InstitutionTokenRepositoryImpl() {
    }

    public InstitutionTokenDocument save(InstitutionTokenDocument institutionTokenDocument) throws ExecutionException, InterruptedException { //TODO: Remove these 2 throws and change to FirestoreQueryException
        DocumentReference docRef = db.collection(INSTITUTION_TOKEN_COLLECTION_REF).document(institutionTokenDocument.getItemId());
        ApiFuture<WriteResult> result = docRef.set(institutionTokenDocument);
        WriteResult getResult = result.get();
        logger.debug(String.format("%s document %s updated at %s", INSTITUTION_TOKEN_COLLECTION_REF, institutionTokenDocument.getItemId(), getResult.getUpdateTime()));
        return institutionTokenDocument;
    }

    @Override
    public Optional<InstitutionTokenDocument> findByItemId(String itemId) throws FirestoreQueryException {
        logger.debug("Trying to find institutionToken with itemId: "+itemId);
        try {
            List<QueryDocumentSnapshot> documents =  db.collection(INSTITUTION_TOKEN_COLLECTION_REF).whereEqualTo("itemId", itemId).get().get().getDocuments(); //TODO: Put itemId in a constants file
            if(documents.isEmpty()) {
                logger.debug("Result set was empty");
                return Optional.empty();
            }
            else {
                if(documents.size() > 1) {
                    logger.warn("When finding an institutionToken document by itemId there were multiple documents found. There should only ever be 1...");
                }
                return  Optional.of(documents.get(0).toObject(InstitutionTokenDocument.class));
            }
        } catch (InterruptedException e) {
            logger.error("InterruptedException thrown: "+e.getLocalizedMessage());
            e.printStackTrace();
            throw new FirestoreQueryException("Error querying firestore: "+e.getLocalizedMessage());
        } catch (ExecutionException e) {
            logger.error("ExecutionException thrown: "+e.getLocalizedMessage());
            e.printStackTrace();
            throw new FirestoreQueryException("Error querying firestore: "+e.getLocalizedMessage());
        }
    }
}
