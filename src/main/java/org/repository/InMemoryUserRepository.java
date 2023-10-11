package org.repository;

import org.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InMemoryUserRepository implements UserRepository {
    private final Map<UUID, User> table = new HashMap<>();

    @Override
    public void persist(User user) {
        table.put(user.getId(), user);
    }

    @Override
    public Collection<User> findAll() {
        return table.values();
    }
}
