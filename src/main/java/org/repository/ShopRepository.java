package org.repository;

import org.model.Shop;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ShopRepository {
    void persist(Shop shop);

    Optional<Shop> find(UUID shopId);

    Collection<Shop> findAll();
}
