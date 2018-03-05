package com.banter.api.repository.institutionToken;

import com.banter.api.model.item.InstitutionTokenItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstitutionTokenRepository extends CrudRepository<InstitutionTokenItem, String> {
}
