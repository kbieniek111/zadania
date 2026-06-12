package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.Role;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService implements IAuthService {

    private final UserRepository userRepository;

    public AuthService(@Qualifier("userJdbcRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean register(String login, String password) {
        if (userRepository.findByLogin(login).isPresent()) {
            return false;
        }
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        User newUser = User.builder()
                .login(login)
                .passwordHash(hashedPassword)
                .role(Role.USER)
                .build();

        userRepository.save(newUser);
        return true;
    }

    @Override
    public Optional<User> login(String login, String password) {
        Optional<User> userOpt = userRepository.findByLogin(login);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (BCrypt.checkpw(password, user.getPasswordHash())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
}