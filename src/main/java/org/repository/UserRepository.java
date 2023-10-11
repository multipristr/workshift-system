package org.repository;

import org.model.User;

import java.util.Collection;

public interface UserRepository {
    void persist(User user);

    Collection<User> findAll();
}
