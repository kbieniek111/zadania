package com.umcsuser.carrent.web;

import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.services.IUserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> list() {
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public User get(@PathVariable String id) {
        return userService.findById(id);
    }
}