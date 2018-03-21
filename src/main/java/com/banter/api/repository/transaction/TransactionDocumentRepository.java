package com.banter.api.repository.transaction;

import com.banter.api.model.document.TransactionDocument;

import java.util.List;

public interface TransactionDocumentRepository {
    void add(TransactionDocument transactionDocument);
    void addAll(List<TransactionDocument> transactionDocuments);
}
