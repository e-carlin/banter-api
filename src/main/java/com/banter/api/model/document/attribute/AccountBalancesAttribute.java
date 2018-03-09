package com.banter.api.model.document.attribute;

import lombok.Data;
import lombok.ToString;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Data
@ToString
@Component
public class AccountBalancesAttribute {

    //TODO: Do some more thinking to see if Double is the best option to represent money
    //Plaid returns a double from their sdk but maybe I should cast it to something else (ie currency or BigDecimal)
    @NotNull private Double current;
    private Double available; //Plaid can give us a null value for this
    private Double limit; //Plaid can give us null values for this

    public AccountBalancesAttribute() {}

    public AccountBalancesAttribute(Double current, Double available, Double limit) {
        this.current = current;
        this.available = available;
        this.limit = limit;
    }

}
