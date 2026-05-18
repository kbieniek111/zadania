package com.umcsuser.carrent.repositories.impl;

import com.umcsuser.carrent.config.HibernateUtil;
import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.repositories.RentalRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class RentalHibernateRepository implements RentalRepository {

    @Override
    public List<Rental> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Rental", Rental.class).list();
        }
    }

    @Override
    public Optional<Rental> findById(String id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Rental rental = session.get(Rental.class, id);
            return Optional.ofNullable(rental);
        }
    }

    @Override
    public Rental save(Rental rental) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            if (rental.getId() == null || rental.getId().isEmpty()) {
                rental.setId(java.util.UUID.randomUUID().toString());
            }
            rental = session.merge(rental);

            transaction.commit();
            return rental;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deleteById(String id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Rental rentalToDelete = session.get(Rental.class, id);
            if (rentalToDelete != null) {
                session.remove(rentalToDelete);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Rental rental = session.createQuery(
                            "FROM Rental r WHERE r.vehicle.id = :vId AND (r.returnDate IS NULL OR r.returnDate = '')", Rental.class)
                    .setParameter("vId", vehicleId)
                    .uniqueResult();

            return Optional.ofNullable(rental);
        }
    }
}