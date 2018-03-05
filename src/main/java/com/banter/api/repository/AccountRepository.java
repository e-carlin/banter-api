package com.banter.api.repository;

import com.banter.api.model.item.AccountItem;
import com.banter.api.model.request.addAccount.AddAccountRequestAccount;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends CrudRepository<AccountItem, String>, AccountRepositoryCustom {
}
