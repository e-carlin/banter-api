package com.banter.api.controller;

import com.banter.api.model.Institution;
import com.banter.api.repository.InstitutionRepository;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class AccountController {

    @Autowired
    InstitutionRepository insitutionRepository;
//
//    @Autowired
//    AccountRepository accountRepository;

//    @PostMapping("/account/add")
//    public void addAccount(@RequestBody AddAccountRequest addAccountRequest) {
//
//    }

    @PostMapping("/account/add")
    @ResponseStatus(HttpStatus.CREATED)
    public void addAccount(@Valid @RequestBody Institution institution) {
        System.out.println("Add account called");
        System.out.println("Institution is: "+institution);

        //TODO: return nice message
    }
}
