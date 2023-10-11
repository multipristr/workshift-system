package org.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.model.Shift;

import java.time.Instant;
import java.util.List;
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

    @Test
    default void findUserShiftsBetween() {
        Instant timestamp1 = Instant.now().minusSeconds(9999);
        Instant timestamp2 = timestamp1.plusSeconds(50);
        Instant timestamp3 = timestamp2.plusSeconds(50);
        Instant timestamp4 = timestamp3.plusSeconds(50);
        Instant timestamp5 = timestamp4.plusSeconds(50);
        Instant timestamp6 = timestamp5.plusSeconds(50);
        UUID userId = UUID.randomUUID();

        Shift shift1 = new Shift(UUID.randomUUID(), UUID.randomUUID(), timestamp1, timestamp2);
        shift1.addUser(userId);
        Shift shift2 = new Shift(UUID.randomUUID(), UUID.randomUUID(), timestamp2, timestamp3);
        shift2.addUser(userId);
        Shift shift3 = new Shift(UUID.randomUUID(), UUID.randomUUID(), timestamp3, timestamp4);
        shift3.addUser(userId);
        Shift shift4 = new Shift(UUID.randomUUID(), UUID.randomUUID(), timestamp4, timestamp5);
        shift4.addUser(userId);
        Shift shift5 = new Shift(UUID.randomUUID(), UUID.randomUUID(), timestamp5, timestamp6);
        shift5.addUser(userId);
        Shift shift6 = new Shift(UUID.randomUUID(), UUID.randomUUID(), timestamp3, timestamp4);
        shift6.addUser(UUID.randomUUID());

        ShiftRepository repository = getRepository();
        repository.persist(shift1);
        repository.persist(shift2);
        repository.persist(shift3);
        repository.persist(shift4);
        repository.persist(shift5);
        repository.persist(shift6);

        List<Shift> userShiftsBetween = repository.findUserShiftsBetween(userId, timestamp3, timestamp4);
        Assertions.assertEquals(3, userShiftsBetween.size());
        Assertions.assertFalse(userShiftsBetween.contains(shift1));
        Assertions.assertTrue(userShiftsBetween.contains(shift2));
        Assertions.assertTrue(userShiftsBetween.contains(shift3));
        Assertions.assertTrue(userShiftsBetween.contains(shift4));
        Assertions.assertFalse(userShiftsBetween.contains(shift5));
        Assertions.assertFalse(userShiftsBetween.contains(shift6));
    }
}