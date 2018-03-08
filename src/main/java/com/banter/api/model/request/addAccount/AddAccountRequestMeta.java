package com.banter.api.model.request.addAccount;

import javax.validation.constraints.NotEmpty;

public class AddAccountRequestMeta {
    @NotEmpty private String name;
    @NotEmpty private String number;
}
