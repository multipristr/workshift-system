package org.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.model.Shop;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

interface ShopRepositoryTest {

    ShopRepository getRepository();

    @Test
    default void persistAndFindAll() {
        Shop shop = new Shop(UUID.randomUUID(), "name");
        shop.addUser(UUID.randomUUID());
        ShopRepository repository = getRepository();
        repository.persist(shop);
        Collection<Shop> found = repository.findAll();
        Assertions.assertEquals(1, found.size());
        Assertions.assertEquals(shop, found.iterator().next());
    }

    @Test
    default void persistAndFind() {
        Shop shop = new Shop(UUID.randomUUID(), "name");
        shop.addUser(UUID.randomUUID());
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
    default void findAll_Empty() {
        Collection<Shop> found = getRepository().findAll();
        Assertions.assertTrue(found.isEmpty());
    }
}