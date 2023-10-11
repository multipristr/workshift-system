package org.service;

import org.controller.request.ShopRequests;
import org.model.Shop;
import org.repository.ShopRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ShopService {
    private final ShopRepository repository;

    public ShopService(ShopRepository repository) {
        this.repository = repository;
    }

    public Shop createShop(ShopRequests.Create shopCreate) {
        Shop shop = new Shop(UUID.randomUUID(), shopCreate.getName());
        repository.persist(shop);
        return shop;
    }
}
