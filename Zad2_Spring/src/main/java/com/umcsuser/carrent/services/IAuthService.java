package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.User;

import java.util.Optional;

public interface IAuthService {
    boolean register(String login, String password);

    Optional<User> login(String login, String password);
}
