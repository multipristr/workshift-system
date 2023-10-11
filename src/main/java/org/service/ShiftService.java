package org.service;

import org.controller.request.ShiftRequests;
import org.exception.LogicValidationException;
import org.exception.MissingEntityException;
import org.model.Shift;
import org.model.Shop;
import org.repository.ShiftRepository;
import org.repository.ShopRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ShiftService {
    private final ShiftRepository shiftRepository;
    private final ShopRepository shopRepository;

    public ShiftService(ShiftRepository shiftRepository, ShopRepository shopRepository) {
        this.shiftRepository = shiftRepository;
        this.shopRepository = shopRepository;
    }

    public Shift createShift(ShiftRequests.Create shiftCreate) {
        if (!shiftCreate.getTo().isAfter(shiftCreate.getFrom())) {
            throw new LogicValidationException("Shift at shop " + shiftCreate.getShopId() + " ends before it has started - it's from " + shiftCreate.getFrom() + " to " + shiftCreate.getTo());
        }
        Optional<Shop> shop = shopRepository.find(shiftCreate.getShopId());
        if (!shop.isPresent()) {
            throw new MissingEntityException("No shop with id " + shiftCreate.getShopId());
        }

        Shift shift = new Shift(UUID.randomUUID(), shiftCreate.getShopId(), shiftCreate.getFrom(), shiftCreate.getTo());
        shiftRepository.persist(shift);
        return shift;
    }
}
