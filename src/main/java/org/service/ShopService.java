package org.service;

import org.controller.request.ShopRequests;
import org.exception.MissingEntityException;
import org.model.Shop;
import org.model.User;
import org.repository.ShopRepository;
import org.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ShopService {
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;

    public ShopService(ShopRepository shopRepository, UserRepository userRepository) {
        this.shopRepository = shopRepository;
        this.userRepository = userRepository;
    }

    public Shop createShop(ShopRequests.Create shopCreate) {
        Shop shop = new Shop(UUID.randomUUID(), shopCreate.getName());
        return shopRepository.persist(shop);
    }

    public void addUserToShop(UUID shopId, UUID userId) {
        Shop shop = shopRepository.find(shopId).orElseThrow(() -> new MissingEntityException("No shop with id " + shopId));
        User user = userRepository.find(userId).orElseThrow(() -> new MissingEntityException("No user with id " + userId));
        shop.addUser(user);
    }
}
