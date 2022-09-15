package com.modsen.hibernate.tutorial.dirty;

import com.modsen.hibernate.tutorial.BaseHibernateTest;
import com.modsen.hibernate.tutorial.dirty.model.Customer;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.UUID;

@Log4j2
public class DirtyCheckingTest extends BaseHibernateTest {

    private Long existingCustomerId;
    private Customer savedCustomer;

    public DirtyCheckingTest() {
        super("hibernate-dirty.cfg.xml");
    }

    @BeforeEach
    void setUp() {
        final Session session = sessionFactory.openSession();
        try (session) {
            final Transaction transaction = session.beginTransaction();

            this.savedCustomer = Customer.builder()
                    .age(new Random().nextInt(100))
                    .name(UUID.randomUUID().toString())
                    .build();

            existingCustomerId = (Long) session.save(savedCustomer);
            transaction.commit();
        }

        log.info("==================\n\n\n");
    }

    @Test
    void dirtyChecking() {
        try (Session session = sessionFactory.openSession()) {
            Customer customer = session.get(Customer.class, existingCustomerId);
            Assertions.assertFalse(session.isDirty());

            customer.setName("value-changed");
            Assertions.assertTrue(session.isDirty());
        }

        try (Session session = sessionFactory.openSession()) {
            Customer customer = session.load(Customer.class, existingCustomerId);
            Assertions.assertEquals(savedCustomer.getName(), customer.getName());
        }
    }

    @Test
    void dirtyCheckingWithTheSameValue() {
        try (Session session = sessionFactory.openSession()) {
            Customer customer = session.load(Customer.class, existingCustomerId);
            Assertions.assertFalse(session.isDirty());

            customer.setName(savedCustomer.getName());
            Assertions.assertFalse(session.isDirty());
        }

        try (Session session = sessionFactory.openSession()) {
            Customer customer = session.load(Customer.class, existingCustomerId);
            Assertions.assertEquals(savedCustomer.getAge(), customer.getAge());
        }
    }


    @Test
    void dirtyCheckingTricky() {
        String newName = "some-new-name";

        try (Session session = sessionFactory.openSession()) {
            Customer customer = session.get(Customer.class, existingCustomerId);
            Assertions.assertFalse(session.isDirty());

            customer.setName(newName);
            customer.setName(savedCustomer.getName());
            Assertions.assertFalse(session.isDirty());
        }

        try (Session session = sessionFactory.openSession()) {
            Customer customer = session.get(Customer.class, existingCustomerId);
            Assertions.assertEquals(savedCustomer.getName(), customer.getName());
        }
    }

    @Test
    void dirtyCheckingWithTransaction() {
        final Session session = sessionFactory.openSession();
        final Transaction transaction = session.getTransaction();
        String newName = "some-new-name";

        try (session) {
            transaction.begin();
            Customer customer = session.get(Customer.class, existingCustomerId);
            Assertions.assertFalse(session.isDirty());

            customer.setName(newName);
            customer.setName(savedCustomer.getName());
            Assertions.assertFalse(session.isDirty());

            transaction.commit();
        } catch (Exception e) {
            log.error(e);
            transaction.rollback();
        }

        try (Session session2 = sessionFactory.openSession()) {
            Customer customer = session2.load(Customer.class, existingCustomerId);
            Assertions.assertEquals(savedCustomer.getName(), customer.getName());
        }
    }
}
