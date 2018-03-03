package com.banter.api.repository;

import com.banter.api.model.item.AccountItem;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<AccountItem, String>{
}
