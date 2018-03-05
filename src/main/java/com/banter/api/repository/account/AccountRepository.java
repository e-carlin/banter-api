package com.banter.api.repository.account;

import com.banter.api.model.item.AccountItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<AccountItem, String>, AccountRepositoryCustom {
}
