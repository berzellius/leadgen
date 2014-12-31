package com.leadgen.billing;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by berz on 30.12.14.
 */
@Service
@Transactional
public class BillingMainContractImpl implements BillingMainContract {
    @Override
    public String sayHello() {
        return "hello from Billing!";
    }
}
