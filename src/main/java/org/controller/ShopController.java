package org.controller;

import org.controller.request.ShopRequests;
import org.model.Shop;
import org.service.ShopService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
}
