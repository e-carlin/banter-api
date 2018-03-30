package com.banter.api.repository.transaction;

import com.banter.api.model.document.TransactionDocument;
import com.banter.api.requestexceptions.customExceptions.FirestoreException;

import java.util.List;
import java.util.Optional;

public interface TransactionDocumentRepository {
    void add(TransactionDocument transactionDocument) throws FirestoreException;
    void addAll(List<TransactionDocument> transactionDocuments) throws FirestoreException;
    Optional<TransactionDocument> findByTransactionId(String transactionId) throws FirestoreException;
}
