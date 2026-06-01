//package com.umcsuser.carrent.repositories.impl;
//
//import com.umcsuser.carrent.config.HibernateUtil;
//import com.umcsuser.carrent.models.User;
//import com.umcsuser.carrent.repositories.UserRepository;
//import org.hibernate.Session;
//import org.hibernate.Transaction;
//
//import java.util.List;
//import java.util.Optional; // Dodajemy import dla Optional
//
//public class UserHibernateRepository implements UserRepository {
//
//    @Override
//    public List<User> findAll() {
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            return session.createQuery("FROM User", User.class).list();
//        }
//    }
//
//    @Override
//    public Optional<User> findById(String id) {
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            User user = session.get(User.class, id);
//            return Optional.ofNullable(user);
//        }
//    }
//
//    @Override
//    public Optional<User> findByLogin(String login) {
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            User user = session.createQuery("FROM User WHERE login = :login", User.class)
//                    .setParameter("login", login)
//                    .uniqueResult();
//            return Optional.ofNullable(user);
//        }
//    }
//
//    @Override
//    public User save(User user) {
//        Transaction transaction = null;
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            transaction = session.beginTransaction();
//
//            if (user.getId() == null || user.getId().isEmpty()) {
//                user.setId(java.util.UUID.randomUUID().toString());
//            }
//            user = session.merge(user);
//            transaction.commit();
//            return user;
//        } catch (Exception e) {
//            if (transaction != null) {
//                transaction.rollback();
//            }
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    @Override
//    public void deleteById(String id) {
//        Transaction transaction = null;
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            transaction = session.beginTransaction();
//            User userToDelete = session.get(User.class, id);
//            if (userToDelete != null) {
//                session.remove(userToDelete);
//            }
//            transaction.commit();
//        } catch (Exception e) {
//            if (transaction != null) {
//                transaction.rollback();
//            }
//            e.printStackTrace();
//        }
//    }
//}