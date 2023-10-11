package org.service;

import org.controller.request.ShiftRequests;
import org.exception.LogicValidationException;
import org.model.Shift;
import org.repository.ShiftRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ShiftService {
    private final ShiftRepository shiftRepository;

    public ShiftService(ShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
    }

    public Shift createShift(ShiftRequests.Create shiftCreate) {
        if (!shiftCreate.getTo().isAfter(shiftCreate.getFrom())) {
            throw new LogicValidationException("Shift at shop " + shiftCreate.getShopId() + " ends before it has started - it's from " + shiftCreate.getFrom() + " to " + shiftCreate.getTo());
        }

        Shift shift = new Shift(UUID.randomUUID(), shiftCreate.getShopId(), shiftCreate.getFrom(), shiftCreate.getTo());
        shiftRepository.persist(shift);
        return shift;
    }
}
