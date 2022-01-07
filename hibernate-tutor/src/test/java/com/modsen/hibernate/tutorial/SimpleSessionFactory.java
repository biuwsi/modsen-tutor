package com.modsen.hibernate.tutorial;

import com.modsen.hibernate.tutorial.basics.model.Customer;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeEach;

import java.util.Random;
import java.util.UUID;

@Log4j2
public abstract class SimpleSessionFactory {

    protected SessionFactory sessionFactory;
    protected Long existingCustomerId;
    protected String name;

    public SimpleSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @BeforeEach
    void setUp() {
        final Session session = sessionFactory.openSession();
        try (session) {
            final Transaction transaction = session.beginTransaction();

            name = UUID.randomUUID().toString();

            existingCustomerId = (Long) session.save(Customer.builder()
                    .age(new Random().nextInt(100))
                    .name(name)
                    .build());

            transaction.commit();
        }

        log.info("==================\n\n\n");
    }
}
