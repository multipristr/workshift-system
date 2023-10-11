package org.repository;

import org.model.Shift;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InMemoryShiftRepository implements ShiftRepository {
    private final Map<UUID, Shift> table = new HashMap<>();

    @Override
    public void persist(Shift shift) {
        table.put(shift.getId(), shift);
    }

    @Override
    public Collection<Shift> findAll() {
        return table.values();
    }
}
