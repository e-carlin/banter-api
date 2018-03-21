package com.banter.api.model.document;

import com.google.cloud.firestore.annotation.ServerTimestamp;
import lombok.Data;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@ToString
@Component
public class AccountsDocument {

    @NotEmpty
    private String userId;
    @NotNull
    @ServerTimestamp
    private Date createdAt;
    @NotNull
    @Valid
    private List<Institution> institutions;

    public AccountsDocument() {
        this.institutions = new ArrayList<>();
    }


    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void addInstitutionAttribute(Institution institution) {
        this.institutions.add(institution);
    }

    public Set<ConstraintViolation<AccountsDocument>> validate() {
        return Validation.buildDefaultValidatorFactory().getValidator().validate(this);
    }


    @Data
    @Component
    @ToString
    public static class Institution {

        @NotEmpty @Setter
        private String itemId;
        @NotEmpty private String name;
        @NotEmpty private String institutionId;
        @NotNull @Valid private ArrayList<Account> accounts;

        public Institution() {

            this.accounts = new ArrayList<>();
        }

        public Institution(String itemId, String name, String institutionId) {
            this.itemId = itemId;
            this.name = name;
            this.institutionId = institutionId;
            this.accounts = new ArrayList<>();
        }

        public void addAccount(Account account) {
            this.accounts.add(account);
        }

        @Data
        @ToString
        @Component
        public static class Account {

            @NotEmpty private String id;
            @NotEmpty private String name;
            @NotEmpty private String type; //TODO: Maybe make this an enum
            @NotEmpty private String subtype; //TODO: Maybe make this an enum too
            @NotNull @Valid private AccountsDocument.Institution.Account.Balances balances;

            public Account() {}

            public Account(com.plaid.client.response.Account account, Balances balances) {
                this.id = account.getAccountId();
                this.name = account.getName();
                this.type = account.getType();
                this.subtype = account.getType();
                this.balances = balances;
            }

            @Data
            @ToString
            @Component
            public static class Balances {

                //TODO: Do some more thinking to see if Double is the best option to represent money
                //Plaid returns a double from their sdk but maybe I should cast it to something else (ie currency or BigDecimal)
                @NotNull private Double current;
                private Double available; //Plaid can give us a null value for this
                private Double limit; //Plaid can give us null values for this

                public Balances() {}

                public Balances(com.plaid.client.response.Account.Balances balances) {
                    this.current = balances.getCurrent();
                    this.available = balances.getAvailable();
                    this.limit = balances.getLimit();
                }

            }
        }
    }
}
