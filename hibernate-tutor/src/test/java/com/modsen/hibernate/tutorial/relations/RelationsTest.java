package com.modsen.hibernate.tutorial.relations;

import com.modsen.hibernate.tutorial.dirty.model.Customer;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class RelationsTest {
    private final SessionFactory sessionFactory;
    private Long existingCustomerId;
    private Customer savedCustomer;

    public RelationsTest() {
        this.sessionFactory = new Configuration()
                .configure("hibernate-relations.cfg.xml")
                .buildSessionFactory();
    }
}
