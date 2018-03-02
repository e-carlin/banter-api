package com.banter.api.repository;

import com.banter.api.model.Institution;
import org.springframework.data.repository.CrudRepository;

public interface InstitutionRepository extends CrudRepository<Institution, String> {
}
