package com.modsen.hibernate.tutorial;

import com.modsen.hibernate.tutorial.basics.model.Customer;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestData {
    public static final Customer TEST_CUSTOMER = Customer.builder()
            .age(24)
            .name("Some-customer")
            .build();
}
