package org.model;


import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Shop implements Serializable {
    private static final long serialVersionUID = -79572583456456869L;
    private final Set<UUID> userIds = new HashSet<>(); // junction table
    private final UUID id;
    private String name;

    public Shop(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public Shop addUser(UUID userId) {
        userIds.add(userId);
        return this;
    }

    public Set<UUID> getUserIds() {
        return userIds;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Shop setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shop shop = (Shop) o;
        return Objects.equals(userIds, shop.userIds) && Objects.equals(id, shop.id) && Objects.equals(name, shop.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userIds, id, name);
    }
}
