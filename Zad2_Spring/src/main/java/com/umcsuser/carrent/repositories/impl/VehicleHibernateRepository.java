package com.umcsuser.carrent.repositories.impl;

import com.umcsuser.carrent.config.HibernateUtil;
import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.VehicleRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class VehicleHibernateRepository implements VehicleRepository {

    @Override
    public List<Vehicle> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Vehicle", Vehicle.class).list();
        }
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Vehicle vehicle = session.get(Vehicle.class, id);
            return Optional.ofNullable(vehicle);
        }
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            if (vehicle.getId() == null || vehicle.getId().isEmpty()) {
                vehicle.setId(java.util.UUID.randomUUID().toString());
            }

            vehicle = session.merge(vehicle);

            transaction.commit();
            return vehicle;
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
            Vehicle vehicleToDelete = session.get(Vehicle.class, id);
            if (vehicleToDelete != null) {
                session.remove(vehicleToDelete);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
}