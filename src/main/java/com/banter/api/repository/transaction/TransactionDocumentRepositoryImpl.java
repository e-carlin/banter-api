package com.banter.api.repository.transaction;

import com.banter.api.model.document.TransactionDocument;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TransactionDocumentRepositoryImpl implements TransactionDocumentRepository {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String TRANSACTION_COLLECTION_REF = "transactions"; //tODO: put in constants file

    @Autowired
    Firestore db;

    public TransactionDocumentRepositoryImpl() {
    }

    @Override
    public void add(TransactionDocument transactionDocument) {
        ApiFuture<DocumentReference> docRef = db.collection(TRANSACTION_COLLECTION_REF).add(transactionDocument);
        //TODO: Error handling of adding document
        logger.debug(String.format("Added new TransactionDocument: " + transactionDocument));
    }

    @Override
    public void addAll(List<TransactionDocument> transactionDocuments) {
        for (TransactionDocument transaction : transactionDocuments) {
            this.add(transaction);
        }
    }
}
