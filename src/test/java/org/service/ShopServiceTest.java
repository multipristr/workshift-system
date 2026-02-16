package org.service;

import org.controller.request.ShopRequests;
import org.exception.MissingEntityException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.model.Shop;
import org.repository.InMemoryShopRepository;

import java.util.UUID;

class ShopServiceTest {
    private ShopService service;

    @BeforeEach
    void setUp() {
        service = new ShopService(new InMemoryShopRepository());
    }

    @Test
    void createShop() {
        ShopRequests.Create request = new ShopRequests.Create().setName("name");
        Shop model = service.createShop(request);
        Assertions.assertEquals(request.getName(), model.getName());
        Assertions.assertNotNull(model.getId());
        Assertions.assertNotNull(model.getUserIds());
        Assertions.assertTrue(model.getUserIds().isEmpty());
    }

    @Test
    void addUserToShop() {
        ShopRequests.Create request = new ShopRequests.Create().setName("name");
        Shop model = service.createShop(request);
        Assertions.assertDoesNotThrow(() -> service.addUserToShop(model.getId(), UUID.randomUUID()));
    }

    @Test
    void addUserToShop_missingShop() {
        Assertions.assertThrows(MissingEntityException.class, () -> service.addUserToShop(UUID.randomUUID(), UUID.randomUUID()));
    }
}