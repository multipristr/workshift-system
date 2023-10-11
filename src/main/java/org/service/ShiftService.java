package org.service;

import org.controller.request.ShiftRequests;
import org.exception.LogicalValidationException;
import org.model.Shift;
import org.repository.ShiftRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ShiftService {
    private final ShiftRepository repository;

    public ShiftService(ShiftRepository repository) {
        this.repository = repository;
    }

    public Shift createShift(ShiftRequests.Create shiftCreate) {
        if (!shiftCreate.getTo().isAfter(shiftCreate.getFrom())) {
            throw new LogicalValidationException("Shift at shop " + shiftCreate.getShopId() + " ends before it has started - it's from " + shiftCreate.getFrom() + " to " + shiftCreate.getTo());
        }

        Shift shift = new Shift(UUID.randomUUID(), shiftCreate.getShopId(), shiftCreate.getFrom(), shiftCreate.getTo());
        repository.persist(shift);
        return shift;
    }

    public void addUserToShift(UUID shiftId, UUID userId) {
        /*
        No user is allowed to work in the same shop for more than 8 hours, within a 24 hour window.
    No user can work more than 5 days in a row in the same shop.
    A user can not work in multiple shops at the same time.
        repository.
        if (!shiftCreate.getTo().isAfter(shiftCreate.getFrom())) {
            throw new LogicalValidationException("Shift at shop " + shiftCreate.getShopId() + " ends before it has started - it's from " + shiftCreate.getFrom() + " to " + shiftCreate.getTo());
        }

        Shift shift = new Shift(UUID.randomUUID(), shiftCreate.getShopId(), shiftCreate.getFrom(), shiftCreate.getTo());
        repository.persist(shift);
        return shift;

         */
    }
}
