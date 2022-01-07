package com.modsen.hibernate.tutorial.basics;

import com.modsen.hibernate.tutorial.basics.model.Customer;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.Test;

import java.util.UUID;

@Log4j2
public class BasicsCommandsTest {
    private SessionFactory sessionFactory;

    public BasicsCommandsTest() {
        sessionFactory = new Configuration()
                .configure("hibernate-basics.cfg.xml")
                .buildSessionFactory();
    }

    @Test
    void saveAndGet() {
        final Long customerId;

        try (final Session session = sessionFactory.openSession()) {
            final Transaction transaction = session.beginTransaction();
            customerId = (Long) session.save(Customer.builder()
                    .name("Customer-name-" + UUID.randomUUID())
                    .age(25)
                    .build());

            transaction.commit();
        }

        try (final Session session = sessionFactory.openSession()) {
            final Customer customer = session.get(Customer.class, customerId);

            log.info("Loaded customer from db: {}", customer);
        }
    }

    @Test
    void saveAndDelete() {
        final Long customerId;

        try (final Session session = sessionFactory.openSession()) {
            final Transaction transaction = session.beginTransaction();

            customerId = (Long) session.save(Customer.builder()
                    .name("Customer-name-" + UUID.randomUUID())
                    .age(25)
                    .build());

            transaction.commit();
        }

        try (final Session session = sessionFactory.openSession()) {
            final Customer customer = session.get(Customer.class, customerId);

            final Transaction transaction = session.beginTransaction();
            session.delete(customer);
            transaction.commit();
        }
    }

    @Test
    void saveAndUpdate() {
        final Long customerId;

        try (final Session session = sessionFactory.openSession()) {
            final Transaction transaction = session.beginTransaction();

            customerId = (Long) session.save(Customer.builder()
                    .name("Customer-name-" + UUID.randomUUID())
                    .age(25)
                    .build());

            transaction.commit();
        }

        try (final Session session = sessionFactory.openSession()) {
            final Customer customer = session.get(Customer.class, customerId);

            customer.setName("updated-name");

            final Transaction transaction = session.beginTransaction();
            session.update(customer);
            transaction.commit();
        }
    }
}
