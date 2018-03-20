package com.banter.api.model.document.attribute;

import com.plaid.client.response.TransactionsGetResponse;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class LocationMeta {

    private String address;
    private String city;
    private String state;
    private String zip;
    private Double lat;
    private Double lon;
    private String storeNumber;

    public LocationMeta() {}

    public LocationMeta(TransactionsGetResponse.Transaction) {
        //TODO: Implement
        getAddress()

        getCity()

        getState()

        getZip()

        pgetLat()

        getLon()

        getStoreNumber()
    }
}
