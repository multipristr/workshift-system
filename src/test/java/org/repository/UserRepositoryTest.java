package org.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.model.User;

import java.util.Optional;
import java.util.UUID;

interface UserRepositoryTest {

    UserRepository getRepository();

    @Test
    default void persistAndFind() {
        User user = new User(UUID.randomUUID(), "name");
        UserRepository repository = getRepository();
        repository.persist(user);
        Optional<User> found = repository.find(user.getId());
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals(user, found.get());
    }

    @Test
    default void find_empty() {
        Optional<User> found = getRepository().find(UUID.randomUUID());
        Assertions.assertFalse(found.isPresent());
    }
}