package org.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.model.Shift;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

interface ShiftRepositoryTest {

    ShiftRepository getRepository();

    @Test
    default void persistAndFind() {
        Shift shift = new Shift(UUID.randomUUID(), UUID.randomUUID(), Instant.now(), Instant.now());
        shift.addUser(UUID.randomUUID());
        ShiftRepository repository = getRepository();
        repository.persist(shift);
        Optional<Shift> found = repository.find(shift.getId());
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals(shift, found.get());
    }

    @Test
    default void find_empty() {
        Optional<Shift> found = getRepository().find(UUID.randomUUID());
        Assertions.assertFalse(found.isPresent());
    }
}