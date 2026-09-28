package org.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.model.Shift;
import org.model.Shop;
import org.model.User;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface ShiftRepositoryTest {

    ShiftRepository getRepository();

    @Test
    default void persistAndFind() {
        Shift shift = new Shift(UUID.randomUUID(), new Shop(UUID.randomUUID(), "testShop"), Instant.now(), Instant.now());
        shift.addUser(new User(UUID.randomUUID(), "testUser"));
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
        User user = new User(UUID.randomUUID(), "testUser1");

        Shift shift1 = new Shift(UUID.randomUUID(), new Shop(UUID.randomUUID(), "testShop1"), timestamp1, timestamp2).addUser(user);
        Shift shift2 = new Shift(UUID.randomUUID(), new Shop(UUID.randomUUID(), "testShop2"), timestamp2, timestamp3).addUser(user);
        Shift shift3 = new Shift(UUID.randomUUID(), new Shop(UUID.randomUUID(), "testShop3"), timestamp3, timestamp4).addUser(user);
        Shift shift4 = new Shift(UUID.randomUUID(), new Shop(UUID.randomUUID(), "testShop4"), timestamp4, timestamp5).addUser(user);
        Shift shift5 = new Shift(UUID.randomUUID(), new Shop(UUID.randomUUID(), "testShop5"), timestamp5, timestamp6).addUser(user);
        Shift shift6 = new Shift(UUID.randomUUID(), new Shop(UUID.randomUUID(), "testShop6"), timestamp3, timestamp4).addUser(new User(UUID.randomUUID(), "testUser2"));

        ShiftRepository repository = getRepository();
        repository.persist(shift1);
        repository.persist(shift2);
        repository.persist(shift3);
        repository.persist(shift4);
        repository.persist(shift5);
        repository.persist(shift6);

        List<Shift> userShiftsBetween = repository.findUserShiftsBetween(user.getId(), timestamp3, timestamp4);
        Assertions.assertEquals(3, userShiftsBetween.size());
        Assertions.assertFalse(userShiftsBetween.contains(shift1));
        Assertions.assertTrue(userShiftsBetween.contains(shift2));
        Assertions.assertTrue(userShiftsBetween.contains(shift3));
        Assertions.assertTrue(userShiftsBetween.contains(shift4));
        Assertions.assertFalse(userShiftsBetween.contains(shift5));
        Assertions.assertFalse(userShiftsBetween.contains(shift6));
    }
}