package com.banter.api.repository.institutionToken;

import com.banter.api.model.document.InstitutionTokenDocument;
import com.banter.api.requestexceptions.customExceptions.FirestoreQueryException;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

public interface InstitutionTokenRepository {

    InstitutionTokenDocument save(InstitutionTokenDocument institutionTokenDocument) throws ExecutionException, InterruptedException;

    Optional<InstitutionTokenDocument> findByItemId(String itemId) throws FirestoreQueryException;
}
