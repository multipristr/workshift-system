package org.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.model.Shop;
import org.model.User;

import java.util.Optional;
import java.util.UUID;

interface ShopRepositoryTest {

    ShopRepository getRepository();

    @Test
    default void persistAndFind() {
        Shop shop = new Shop(UUID.randomUUID(), "name");
        shop.addUser(new User(UUID.randomUUID(), "testUser"));
        ShopRepository repository = getRepository();
        repository.persist(shop);

        Optional<Shop> found = repository.find(shop.getId());
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals(shop, found.get());
    }

    @Test
    default void find_Empty() {
        Optional<Shop> found = getRepository().find(UUID.randomUUID());
        Assertions.assertFalse(found.isPresent());
    }
}