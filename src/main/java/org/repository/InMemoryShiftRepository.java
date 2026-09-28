package org.repository;

import org.model.Shift;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class InMemoryShiftRepository implements ShiftRepository {
    private final Map<UUID, Shift> table = new HashMap<>();

    @Override
    public Shift persist(Shift shift) {
        table.put(shift.getId(), shift);
        return shift;
    }

    @Override
    public Optional<Shift> find(UUID shiftId) {
        return Optional.ofNullable(table.get(shiftId));
    }

    @Override
    public List<Shift> findUserShiftsBetween(UUID userId, Instant fromInclusive, Instant toInclusive) {
        return table.values().stream()
                .filter(shift -> shift.getUsers().stream().anyMatch(user -> user.getId().equals(userId)))
                .filter(shift -> !shift.getTo().isBefore(fromInclusive))
                .filter(shift -> !shift.getFrom().isAfter(toInclusive))
                .collect(Collectors.toList());
    }
}
