package org.repository;

import org.model.Shift;

import java.util.Collection;

public interface ShiftRepository {
    void persist(Shift shift);

    Collection<Shift> findAll();
}
