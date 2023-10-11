package org.controller;

import org.controller.request.ShopRequests;
import org.model.Shop;
import org.service.ShopService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("shops")
public class ShopController {
    private final ShopService service;

    public ShopController(ShopService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Object> createShop(@RequestBody ShopRequests.Create request) {
        Shop shop = service.createShop(request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(shop.getId())
                        .toUri())
                .build();
    }

    @PutMapping("{shopId}/user/{userId}")
    public ResponseEntity<Object> addUserToShop(@PathVariable UUID shopId, @PathVariable UUID userId) {
        service.addUserToShop(shopId, userId);
        return ResponseEntity.noContent().build();
    }
}
