package com.banter.api.repository.institutionToken;

import com.banter.api.model.document.InstitutionTokenItem;

import java.util.concurrent.ExecutionException;

public interface InstitutionTokenRepository {

    InstitutionTokenItem save(InstitutionTokenItem institutionTokenItem) throws ExecutionException, InterruptedException;
}
