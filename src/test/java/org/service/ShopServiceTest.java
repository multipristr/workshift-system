package org.service;

import org.controller.request.ShopRequests;
import org.exception.MissingEntityException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.model.Shop;
import org.model.User;
import org.repository.InMemoryShopRepository;
import org.repository.InMemoryUserRepository;
import org.repository.UserRepository;

import java.util.UUID;

class ShopServiceTest {
    private ShopService service;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
        service = new ShopService(new InMemoryShopRepository(), userRepository);
    }

    @Test
    void createShop() {
        ShopRequests.Create request = new ShopRequests.Create().setName("name");
        Shop model = service.createShop(request);
        Assertions.assertEquals(request.getName(), model.getName());
        Assertions.assertNotNull(model.getId());
        Assertions.assertNotNull(model.getUsers());
        Assertions.assertTrue(model.getUsers().isEmpty());
    }

    @Test
    void addUserToShop() {
        Shop shop = service.createShop(new ShopRequests.Create().setName("name"));
        User user = userRepository.persist(new User(UUID.randomUUID(), "testUser"));
        Assertions.assertDoesNotThrow(() -> service.addUserToShop(shop.getId(), user.getId()));
    }

    @Test
    void addUserToShop_missingShop() {
        User user = userRepository.persist(new User(UUID.randomUUID(), "testUser"));
        Assertions.assertThrows(MissingEntityException.class, () -> service.addUserToShop(UUID.randomUUID(), user.getId()));
    }

    @Test
    void addUserToShop_missingUser() {
        Shop shop = service.createShop(new ShopRequests.Create().setName("name"));
        Assertions.assertThrows(MissingEntityException.class, () -> service.addUserToShop(shop.getId(), UUID.randomUUID()));
    }
}