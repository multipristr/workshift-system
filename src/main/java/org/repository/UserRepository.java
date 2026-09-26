package org.repository;

import org.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User persist(User user);

    Optional<User> find(UUID userId);
}
