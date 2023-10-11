package org.repository;

import org.model.Shop;

import java.util.Optional;
import java.util.UUID;

public interface ShopRepository {
    void persist(Shop shop);

    Optional<Shop> find(UUID shopId);

    void addUser(UUID shopId, UUID userId); // would be handled in junction table in SQL
}
