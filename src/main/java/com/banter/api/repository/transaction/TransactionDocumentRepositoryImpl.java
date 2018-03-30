package com.banter.api.repository.transaction;

import com.banter.api.model.document.TransactionDocument;
import com.banter.api.requestexceptions.customExceptions.FirestoreException;
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
public class TransactionDocumentRepositoryImpl implements TransactionDocumentRepository {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String TRANSACTION_COLLECTION_REF = "transactions"; //tODO: put in constants file

    @Autowired
    Firestore db;

    public TransactionDocumentRepositoryImpl() {
    }

    /**
     * Adds only documents where the existing transactionId is not already in the DB
     * @param transactionDocument
     */
    @Override
    public void add(TransactionDocument transactionDocument) throws FirestoreException {
        if(!this.findByTransactionId(transactionDocument.getTransactionId()).isPresent()) {
            ApiFuture<DocumentReference> docRef = db.collection(TRANSACTION_COLLECTION_REF).add(transactionDocument);
            //TODO: Error handling of adding document
            logger.debug(String.format("Added new TransactionDocument: " + transactionDocument));
        }
        else {
            logger.debug(String.format("Skipping transaction with id %s. It is already in the db", transactionDocument.getTransactionId()));
        }
    }

    @Override
    public void addAll(List<TransactionDocument> transactionDocuments) throws FirestoreException {
        for (TransactionDocument transaction : transactionDocuments) {
            this.add(transaction);
        }
    }

    @Override
    public Optional<TransactionDocument> findByTransactionId(String transactionId) throws FirestoreException {
        try {
            CollectionReference accounts = db.collection(TRANSACTION_COLLECTION_REF);
            Query query = accounts.whereEqualTo("transactionId", transactionId).limit(1);
            ApiFuture<QuerySnapshot> querySnapshot = query.get();
            List<QueryDocumentSnapshot> documentSnapshots = querySnapshot.get().getDocuments();
            if (documentSnapshots.size() < 1) {
                logger.debug(String.format("Transaction with id %s not found", transactionId));
                return Optional.empty();
            } else {
                logger.debug(String.format("Transaction with id %s found", transactionId));
                return Optional.of(documentSnapshots.get(0).toObject(TransactionDocument.class));
            }
        } catch (InterruptedException e) {
            logger.error("Firestore interrupted exception caught: " + e.getLocalizedMessage());
            e.printStackTrace();
            throw new FirestoreException("There was an exception thrown while querying the database");

        } catch (ExecutionException e) {
            logger.error("Firestore execution exception caught: " + e.getLocalizedMessage());
            e.printStackTrace();
            throw new FirestoreException("There was an exception thrown while querying the database");
        }
    }
}
