package org.repository;

import org.exception.MissingEntityException;
import org.model.Shop;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryShopRepository implements ShopRepository {
    private final Map<UUID, Shop> table = new HashMap<>();

    @Override
    public void persist(Shop shop) {
        table.put(shop.getId(), shop);
    }

    @Override
    public Optional<Shop> find(UUID shopId) {
        return Optional.ofNullable(table.get(shopId));
    }

    @Override
    public void addUser(UUID shopId, UUID userId) {
        Shop shop = table.get(shopId);
        if (shop == null) {
            throw new MissingEntityException("No shop with id " + shopId);
        }
        shop.addUser(userId);
    }

    @Override
    public Collection<Shop> findAll() {
        return table.values();
    }
}
