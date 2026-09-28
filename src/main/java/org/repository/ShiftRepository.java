package org.repository;

import org.model.Shift;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShiftRepository {
    Shift persist(Shift shift);

    Optional<Shift> find(UUID shiftId);

    List<Shift> findUserShiftsBetween(UUID userId, Instant fromInclusive, Instant toInclusive);
}
