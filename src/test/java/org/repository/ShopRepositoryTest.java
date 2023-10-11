package org.repository;

import org.exception.MissingEntityException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.model.Shop;

import java.util.Optional;
import java.util.UUID;

interface ShopRepositoryTest {

    ShopRepository getRepository();

    @Test
    default void persistAndFind() {
        Shop shop = new Shop(UUID.randomUUID(), "name").addUser(UUID.randomUUID());
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

    @Test
    default void addUser() {
        Shop shop = new Shop(UUID.randomUUID(), "name");
        UUID userId = UUID.randomUUID();
        ShopRepository repository = getRepository();
        repository.persist(shop);

        repository.addUser(shop.getId(), userId);
        Optional<Shop> found = repository.find(shop.getId());
        Assertions.assertTrue(found.isPresent());
        Assertions.assertTrue(found.get().getUserIds().contains(userId));
    }

    @Test
    default void addUser_empty() {
        ShopRepository repository = getRepository();
        Assertions.assertThrows(MissingEntityException.class, () -> repository.addUser(UUID.randomUUID(), UUID.randomUUID()));
    }
}