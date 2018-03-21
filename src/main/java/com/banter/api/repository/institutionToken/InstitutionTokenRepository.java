package com.banter.api.repository.institutionToken;

import com.banter.api.model.document.InstitutionTokenDocument;
import com.banter.api.requestexceptions.customExceptions.FirestoreException;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

public interface InstitutionTokenRepository {

    InstitutionTokenDocument add(InstitutionTokenDocument institutionTokenDocument);

    Optional<InstitutionTokenDocument> findByItemId(String itemId) throws FirestoreException;
}
