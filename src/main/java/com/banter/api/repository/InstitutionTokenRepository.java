package com.banter.api.repository;

import com.banter.api.model.item.InstitutionTokenItem;
import org.springframework.data.repository.CrudRepository;

import javax.validation.Valid;

public interface InstitutionTokenRepository extends CrudRepository<InstitutionTokenItem, String> {
}
