package com.modsen.hibernate.tutorial.cache;

import com.modsen.hibernate.tutorial.BaseHibernateTest;
import com.modsen.hibernate.tutorial.cache.model.CachedCustomer;
import com.modsen.hibernate.tutorial.cache.model.Customer;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.UUID;

@Log4j2
public class CacheTest extends BaseHibernateTest {
    private Long existingCustomerId;
    private String name;

    public CacheTest() {
        super("hibernate-second-lvl-cache.cfg.xml");
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

    @Test
    void firstLvlCacheTest() {
        try (Session session = sessionFactory.openSession()) {
            log.info("Loading customer from database");
            final Customer customer = session.get(Customer.class, existingCustomerId);

            log.info("Loading customer from database one more time");
            final Customer customer2 = session.get(Customer.class, existingCustomerId);

            log.info("Additional query does not appear: entity got from 1-sq lvl cache. Even links the same");
            Assertions.assertSame(customer, customer2);
        }
    }

    @Test
    void firstLvlCacheSessionClosedTest() {
        Customer customer1;
        Customer customer2;

        try (Session session = sessionFactory.openSession()) {
            log.info("Loading customer from database");
            customer1 = session.get(Customer.class, existingCustomerId);
        }

        try (Session session = sessionFactory.openSession()) {
            log.info("Loading the same customer from database but with different session");
            customer2 = session.get(Customer.class, existingCustomerId);
        }

        Assertions.assertNotSame(customer1, customer2);
    }

    @Test
    void secondLvlCacheTest() {
        try (Session session = sessionFactory.openSession()) {
            log.info("Loading customer from database");
            CachedCustomer cachedCustomer = session.get(CachedCustomer.class, existingCustomerId);
        }

        try (Session session = sessionFactory.openSession()) {
            log.info("Loading the same customer from database but with different session. Query doesn't executed");
            CachedCustomer cachedCustomer = session.get(CachedCustomer.class, existingCustomerId);
        }
    }

    @Test
    void queryCacheTest() {
        try (Session session = sessionFactory.openSession()) {
            log.info("Loading customer from database");
            final CachedCustomer cachedCustomer1 = session.createQuery("select c from CachedCustomer c where c.name = :name", CachedCustomer.class)
                    .setParameter("name", name)
                    .setCacheable(true)
                    .uniqueResult();
        }

        try (Session session = sessionFactory.openSession()) {
            log.info("Loading the same customer from database");
            final CachedCustomer cachedCustomer2 = session.createQuery("select c from CachedCustomer c where c.name = :name", CachedCustomer.class)
                    .setParameter("name", name)
                    .setCacheable(true)
                    .uniqueResult();
        }
    }
}
