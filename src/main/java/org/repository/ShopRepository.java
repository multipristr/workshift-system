package org.repository;

import org.model.Shop;

import java.util.Collection;

public interface ShopRepository {
    void persist(Shop shop);

    Collection<Shop> findAll();
}
