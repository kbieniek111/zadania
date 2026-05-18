package com.umcsuser.carrent.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.models.Vehicle;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();

                String dbUrl = System.getenv("DB_URL");
                if (dbUrl == null || dbUrl.isEmpty()) {
                    throw new RuntimeException("Błąd: Nie ustawiono zmiennej środowiskowej DB_URL w IntelliJ!");
                }

                configuration.setProperty("hibernate.connection.url", dbUrl);
                configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");

                configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

                configuration.setProperty("hibernate.show_sql", "true");
                configuration.setProperty("hibernate.format_sql", "true");
                configuration.setProperty("hibernate.hbm2ddl.auto", "update");

                configuration.addAnnotatedClass(User.class);
                configuration.addAnnotatedClass(Vehicle.class);
                configuration.addAnnotatedClass(Rental.class);

                sessionFactory = configuration.buildSessionFactory();
                System.out.println("Hibernate uruchomiony pomyślnie!");

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Wystąpił błąd podczas konfiguracji Hibernate!");
            }
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}