package org.service;

import org.controller.request.UserRequests;
import org.model.User;
import org.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User createUser(UserRequests.Create userCreate) {
        User user = new User(UUID.randomUUID(), userCreate.getName());
        repository.persist(user);
        return user;
    }
}
