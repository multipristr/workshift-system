package org.repository;

import org.model.Shift;

import java.util.Optional;
import java.util.UUID;

public interface ShiftRepository {
    void persist(Shift shift);

    Optional<Shift> find(UUID shopId);
}
