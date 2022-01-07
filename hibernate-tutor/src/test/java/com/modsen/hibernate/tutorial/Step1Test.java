package com.modsen.hibernate.tutorial;

import com.modsen.hibernate.tutorial.basics.model.Customer;
import lombok.extern.log4j.Log4j2;
import org.hibernate.LazyInitializationException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Serializable;

@Log4j2
public class Step1Test {

    private final SessionFactory SESSION_FACTORY = new Configuration()
            .configure()
            .buildSessionFactory();

    private Long existingCustomerId;

    @BeforeEach
    void setUp() {
        final Session session = SESSION_FACTORY.openSession();
        try (session) {
            final Transaction transaction = session.beginTransaction();

            existingCustomerId = (Long) session.save(TestData.TEST_CUSTOMER);

            transaction.commit();
        }

        log.info("==================\n\n\n");
    }

    @Test
    void successfullySavedTest() {
        final Session session = SESSION_FACTORY.openSession();
        final Transaction transaction = session.getTransaction();

        try (session) {
            transaction.begin();
            final Customer customer = Customer.builder()
                    .name("Alex")
                    .age(22)
                    .build();
            final Serializable id = session.save(customer);

            log.info("Successfully saved entity '{}' with id {}", Customer.class.getCanonicalName(), id);

            transaction.commit();
        } catch (Exception e) {
            log.error("Something gone wrong", e);
            transaction.rollback();
        }
    }

    @Test
    void lazyLoadingSessionClosedTest() {
        Customer customer;

        try (Session session = SESSION_FACTORY.openSession()) {
            customer = session.load(Customer.class, existingCustomerId);
            log.info("Entity does not loaded! Got generated proxy from hibernate: {}", customer.getClass().getName());
        }

        log.info("Expecting exception after getting parameter");
        Assertions.assertThrows(LazyInitializationException.class, customer::getAge);
    }

    @Test
    void lazyLoading() {
        try (Session session = SESSION_FACTORY.openSession()) {
            Customer customer = session.load(Customer.class, existingCustomerId);
            log.info("Entity does not loaded yet! Check out proxy from the hibernate: {}", customer.getClass().getName());

            log.info("After this action entity will be loaded:");
            final Integer age = customer.getAge();
            log.info("Got property from loaded entity: {}", age);
        }
    }
}
