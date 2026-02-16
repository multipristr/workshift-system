package org.service;

import org.controller.request.UserRequests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.model.User;
import org.repository.InMemoryUserRepository;

class UserServiceTest {
    private UserService service;

    @BeforeEach
    void setUp() {
        service = new UserService(new InMemoryUserRepository());
    }

    @Test
    void createUser() {
        UserRequests.Create request = new UserRequests.Create().setName("name");
        User model = service.createUser(request);
        Assertions.assertEquals(request.getName(), model.getName());
        Assertions.assertNotNull(model.getId());
    }
}