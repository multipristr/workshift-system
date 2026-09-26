package org.repository;

import org.model.Shop;

import java.util.Optional;
import java.util.UUID;

public interface ShopRepository {
    Shop persist(Shop shop);

    Optional<Shop> find(UUID shopId);
}
