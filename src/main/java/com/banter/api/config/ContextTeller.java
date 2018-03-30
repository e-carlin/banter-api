package com.banter.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * This class just prints out all registered beans on application launch
 * It has no business value it is just so I can see what is going on when debugging
 */
@Component
public class ContextTeller implements CommandLineRunner {

    @Autowired
    ApplicationContext applicationContext;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("-------------> just checking!");

        System.out.println(Arrays.asList(applicationContext.getBeanDefinitionNames()));
    }}
