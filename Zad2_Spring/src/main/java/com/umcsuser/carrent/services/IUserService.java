package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.User;

import java.util.List;

public interface IUserService {
    List<User> findAllUsers();

    User findById(String userId);

    void deleteUser(String targetUserId, String loggedUserId);
    User save(User user);
}
