package org.repository;

import org.model.Shop;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryShopRepository implements ShopRepository {
    private final Map<UUID, Shop> table = new HashMap<>();

    @Override
    public Shop persist(Shop shop) {
        table.put(shop.getId(), shop);
        return shop;
    }

    @Override
    public Optional<Shop> find(UUID shopId) {
        return Optional.ofNullable(table.get(shopId));
    }

}
