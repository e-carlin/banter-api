package com.banter.api.model.item.attribute;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AccountBalancesAttribute {

    //TODO: Do some more thinking to see if Double is the best option to represent money
    //Plaid returns a double from their sdk but maybe I should cast it to something else
    @NotNull private Double available;
    @NotNull private Double current;
    private Double limit; //Plaid can give us null values for this :(
}
