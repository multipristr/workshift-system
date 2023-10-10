package org.service;

import org.repository.ShopRepository;
import org.springframework.stereotype.Service;

@Service
public class ShopService {
    private final ShopRepository repository;

    public ShopService(ShopRepository repository) {
        this.repository = repository;
    }
}
