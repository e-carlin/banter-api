package com.banter.api.repository.account;

import com.banter.api.model.item.AccountItem;
import com.banter.api.model.item.attribute.InstitutionAttribute;
import com.banter.api.model.request.addAccount.AddAccountRequestAccount;
import com.banter.api.requestexceptions.customExceptions.PlaidGetAccountBalanceException;
import com.banter.api.service.InstitutionService;
import com.banter.api.service.PlaidClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
public class AccountRepositoryImpl implements AccountRepositoryCustom {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PlaidClientService plaidClientService;
    @Autowired
    private InstitutionService institutionService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public AccountItem saveAccountItemFromAddAccountRequest(
            List<AddAccountRequestAccount> requestAccounts,
            String itemId, String accessToken,
            String institutionName,
            String institutionId,
            String userSub)
            throws ConstraintViolationException, PlaidGetAccountBalanceException {
        this.logger.debug("Saving account item from add account request");
        //TODO: First, check if they already have an existing AccountItem. If so get it,
        // and add this InstitutionAttribute to it, While adding the institutionToken attribute check if the institution_id / account_id is already found
        // IF so just update the balance
        AccountItem accountItem;
        Optional<AccountItem> existingAccountItem = accountRepository.findById(userSub);

        //If there is already an account item for this user in the DB then use it
        if (existingAccountItem.isPresent()) {
            logger.debug("This user already has an account item");
            accountItem = existingAccountItem.get();

            List<String> existingInstitutionIds = accountItem.getInstitutions().stream().map(existingInstitutionId -> existingInstitutionId.getInstitutionId()).collect(Collectors.toList());
            //If this institution is already in the account item. Then no need to add it again. Just update the balances
            if (existingInstitutionIds.contains(institutionId)) {
                logger.debug("This user has added this account before. Just going to update their balances. TODO: This needs to be implemented");
                //TODO: Update balances
                return accountItem;
            }
        } else { //There is no existing account item so create one
            logger.debug("This is the user's first account. Creating new AccountItem");
            accountItem = new AccountItem();
            accountItem.setUserSub(userSub);
        }

        //Add the institution attribute to the account item (either newly created or existing account item)
        logger.debug("This is a new institution for this user. Creating a new InstitutionAttribute");
        InstitutionAttribute institutionAttribute = institutionService.createInstitutionAttribute(itemId, institutionName, institutionId, accessToken);
        accountItem.addInstitutionAttribute(institutionAttribute);

        Set<ConstraintViolation<AccountItem>> errors = accountItem.validate();
        if (!errors.isEmpty()) {
            logger.error("Validation errors encountered when saving accountItem: " + Arrays.toString(errors.toArray()));
            throw new ConstraintViolationException(errors);
        }

        this.logger.debug("Institution attribute to add is: "+institutionAttribute);
        this.logger.debug("Success building account item: " + accountItem);
        this.accountRepository.save(accountItem);
        return accountItem;
    }

    public boolean userHasInstitution(String userSub, String insId) {
        Optional<AccountItem> accountItemOptional = accountRepository.findById(userSub);
        if(accountItemOptional.isPresent()) {
            AccountItem accountItem = accountItemOptional.get();
            List<InstitutionAttribute> institutionAttributes = accountItem.getInstitutions();
            for(InstitutionAttribute institution : institutionAttributes) {
                if(institution.getInstitutionId().equals(insId)) {
                    return true;
                }
            }
        }
        return false;
    }
}
