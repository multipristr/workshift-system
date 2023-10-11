package org.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.model.Shift;

import java.time.Instant;
import java.util.Collection;
import java.util.UUID;

interface ShiftRepositoryTest {

    ShiftRepository getRepository();

    @Test
    default void persistAndFindAll() {
        Shift shift = new Shift(UUID.randomUUID(), UUID.randomUUID(), Instant.now(), Instant.now());
        shift.addUser(UUID.randomUUID());
        ShiftRepository repository = getRepository();
        repository.persist(shift);
        Collection<Shift> found = repository.findAll();
        Assertions.assertEquals(1, found.size());
        Assertions.assertEquals(shift, found.iterator().next());
    }

    @Test
    default void findAll_empty() {
        Collection<Shift> found = getRepository().findAll();
        Assertions.assertTrue(found.isEmpty());
    }
}