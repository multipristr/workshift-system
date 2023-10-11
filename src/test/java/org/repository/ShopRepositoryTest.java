package org.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.model.Shop;

import java.util.Collection;
import java.util.UUID;

interface ShopRepositoryTest {

    ShopRepository getRepository();

    @Test
    default void persistAndFind() {
        Shop shop = new Shop(UUID.randomUUID(), "name");
        shop.addUser(UUID.randomUUID());
        ShopRepository repository = getRepository();
        repository.persist(shop);
        Collection<Shop> found = repository.findAll();
        Assertions.assertEquals(1, found.size());
        Assertions.assertEquals(shop, found.iterator().next());
    }

    @Test
    default void findEmpty() {
        Collection<Shop> found = getRepository().findAll();
        Assertions.assertTrue(found.isEmpty());
    }
}