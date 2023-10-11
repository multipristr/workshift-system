package org.repository;

import org.model.Shift;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryShiftRepository implements ShiftRepository {
    private final Map<UUID, Shift> table = new HashMap<>();

    @Override
    public void persist(Shift shift) {
        table.put(shift.getId(), shift);
    }

    @Override
    public Optional<Shift> find(UUID shopId) {
        return Optional.ofNullable(table.get(shopId));
    }

}
