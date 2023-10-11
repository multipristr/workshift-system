package org.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.model.User;

import java.util.Collection;
import java.util.UUID;

interface UserRepositoryTest {

    UserRepository getRepository();

    @Test
    default void persistAndFind() {
        User user = new User(UUID.randomUUID(), "name");
        UserRepository repository = getRepository();
        repository.persist(user);
        Collection<User> found = repository.findAll();
        Assertions.assertEquals(1, found.size());
        Assertions.assertEquals(user, found.iterator().next());
    }

    @Test
    default void findEmpty() {
        Collection<User> found = getRepository().findAll();
        Assertions.assertTrue(found.isEmpty());
    }
}