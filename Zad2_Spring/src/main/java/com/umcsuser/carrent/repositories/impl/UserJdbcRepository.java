package com.umcsuser.carrent.repositories.impl;

import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("jdbc")
public class UserJdbcRepository implements UserRepository {

    private final DataSource dataSource;

    public UserJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        Connection conn = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading users", e);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
        return users;
    }

    @Override
    public Optional<User> findById(String id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        Connection conn = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading user by id", e);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByLogin(String login) {
        String sql = "SELECT * FROM users WHERE login = ?";
        Connection conn = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading user by login", e);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
        return Optional.empty();
    }

    @Override
    public User save(User user) {
        if (user.getId() == null || user.getId().isEmpty()) {
            user.setId(java.util.UUID.randomUUID().toString());
        }
        String sql = "INSERT INTO users (id, login, password) VALUES (?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET login = EXCLUDED.login, password = EXCLUDED.password";

        Connection conn = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getId());
            pstmt.setString(2, user.getLogin());
            pstmt.setString(3, user.getPasswordHash());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while saving user", e);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
        return user;
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM users WHERE id = ?";
        Connection conn = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while deleting user", e);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getString("id"));
        user.setLogin(rs.getString("login"));
        user.setPasswordHash(rs.getString("password"));
        user.setRole(com.umcsuser.carrent.models.Role.USER);
        return user;
    }
}