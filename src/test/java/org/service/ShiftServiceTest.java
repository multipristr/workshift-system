package org.service;

import org.exception.InvalidStateException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.model.Shift;
import org.repository.InMemoryShiftRepository;
import org.repository.ShiftRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

class ShiftServiceTest {
    private ShiftRepository repository;
    private ShiftService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryShiftRepository();
        service = new ShiftService(repository);
    }

    @Test
    void addUserToShift_moreThan5DaysInRow_2dayShifts() {
        UUID userId = UUID.randomUUID();
        UUID shopId = UUID.randomUUID();

        Instant day1 = Instant.now().truncatedTo(ChronoUnit.DAYS);
        Instant day2 = day1.plus(2, ChronoUnit.DAYS);
        Instant day3 = day2.plus(2, ChronoUnit.DAYS);

        Shift shift1 = new Shift(UUID.randomUUID(), shopId, day1.minusSeconds(1), day1);
        repository.persist(shift1);
        Shift shift2 = new Shift(UUID.randomUUID(), shopId, day2.minusSeconds(1), day2);
        repository.persist(shift2);
        Shift shift3 = new Shift(UUID.randomUUID(), shopId, day3.minusSeconds(1), day3);
        repository.persist(shift3);

        service.addUserToShift(shift1.getId(), userId);
        service.addUserToShift(shift2.getId(), userId);
        Assertions.assertThrows(InvalidStateException.class, () -> service.addUserToShift(shift3.getId(), userId));
    }

    @Test
    void addUserToShift_moreThan5DaysInRow_1dayShifts() {
        UUID userId = UUID.randomUUID();
        UUID shopId = UUID.randomUUID();

        Instant day1 = Instant.now().truncatedTo(ChronoUnit.DAYS);
        Instant day2 = day1.plus(2, ChronoUnit.DAYS);
        Instant day3 = day2.plus(1, ChronoUnit.DAYS);
        Instant day4 = day3.plus(1, ChronoUnit.DAYS);
        Instant day5 = day4.plus(1, ChronoUnit.DAYS);
        Instant day6 = day5.plus(1, ChronoUnit.DAYS);
        Instant day7 = day6.plus(1, ChronoUnit.DAYS);

        Shift shift1 = new Shift(UUID.randomUUID(), shopId, day1, day1.plusSeconds(1));
        repository.persist(shift1);
        Shift shift2 = new Shift(UUID.randomUUID(), shopId, day2, day2.plusSeconds(1));
        repository.persist(shift2);
        Shift shift3 = new Shift(UUID.randomUUID(), shopId, day3, day3.plusSeconds(1));
        repository.persist(shift3);
        Shift shift4 = new Shift(UUID.randomUUID(), shopId, day4, day4.plusSeconds(1));
        repository.persist(shift4);
        Shift shift5 = new Shift(UUID.randomUUID(), shopId, day5, day5.plusSeconds(1));
        repository.persist(shift5);
        Shift shift6 = new Shift(UUID.randomUUID(), shopId, day6, day6.plusSeconds(1));
        repository.persist(shift6);
        Shift shift7 = new Shift(UUID.randomUUID(), shopId, day7, day7.plusSeconds(1));
        repository.persist(shift7);

        service.addUserToShift(shift1.getId(), userId);
        service.addUserToShift(shift2.getId(), userId);
        service.addUserToShift(shift3.getId(), userId);
        service.addUserToShift(shift4.getId(), userId);
        service.addUserToShift(shift5.getId(), userId);
        service.addUserToShift(shift6.getId(), userId);
        Assertions.assertThrows(InvalidStateException.class, () -> service.addUserToShift(shift7.getId(), userId));
    }
}