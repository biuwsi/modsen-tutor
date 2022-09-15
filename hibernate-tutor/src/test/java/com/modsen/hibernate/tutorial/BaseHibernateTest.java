package com.modsen.hibernate.tutorial;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class BaseHibernateTest {
    protected SessionFactory sessionFactory;

    public BaseHibernateTest(String configurationFileName) {
        PostgreSQLContainer container = new PostgreSQLContainer<>("postgres:14.5")
                .withInitScript("relations/ddl2.sql");
        container.start();
        sessionFactory = new Configuration()
                .configure(configurationFileName)
                .setProperty("hibernate.connection.url", container.getJdbcUrl())
                .setProperty("hibernate.connection.username", container.getUsername())
                .setProperty("hibernate.connection.password", container.getPassword())
                .buildSessionFactory();
    }
}
