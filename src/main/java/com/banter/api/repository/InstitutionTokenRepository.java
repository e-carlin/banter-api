package com.banter.api.repository;

import com.banter.api.model.item.InstitutionTokenItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.validation.Valid;

@Repository
public interface InstitutionTokenRepository extends CrudRepository<InstitutionTokenItem, String> {
}
