package org.service;

import org.controller.request.ShiftRequests;
import org.exception.InvalidStateException;
import org.exception.LogicalValidationException;
import org.exception.MissingEntityException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.model.Shift;
import org.repository.InMemoryShiftRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

class ShiftServiceTest {
    private ShiftService service;

    @BeforeEach
    void setUp() {
        service = new ShiftService(new InMemoryShiftRepository());
    }

    @Test
    void createShift() {
        ShiftRequests.Create request = new ShiftRequests.Create()
                .setFrom(Instant.now().minusSeconds(9))
                .setTo(Instant.now().plusSeconds(9))
                .setShopId(UUID.randomUUID());
        Shift model = service.createShift(request);
        Assertions.assertEquals(request.getFrom(), model.getFrom());
        Assertions.assertEquals(request.getTo(), model.getTo());
        Assertions.assertEquals(request.getShopId(), model.getShopId());
        Assertions.assertNotNull(model.getId());
        Assertions.assertNotNull(model.getUserIds());
        Assertions.assertTrue(model.getUserIds().isEmpty());
    }

    @Test
    void createShift_wrongTime() {
        ShiftRequests.Create request = new ShiftRequests.Create()
                .setTo(Instant.now().minusSeconds(9))
                .setFrom(Instant.now().plusSeconds(9))
                .setShopId(UUID.randomUUID());
        Assertions.assertThrows(LogicalValidationException.class, () -> service.createShift(request));
    }

    @Test
    void addUserToShift_missingShift() {
        Assertions.assertThrows(MissingEntityException.class, () -> service.addUserToShift(UUID.randomUUID(), UUID.randomUUID()));
    }

    @Test
    void addUserToShift_moreThan5DaysInRow_2dayShifts_before() {
        UUID userId = UUID.randomUUID();
        UUID shopId = UUID.randomUUID();

        Instant day1 = Instant.now().truncatedTo(ChronoUnit.DAYS);
        Instant day2 = day1.plus(2, ChronoUnit.DAYS);
        Instant day3 = day2.plus(2, ChronoUnit.DAYS);
        Instant day4 = day3.plus(3, ChronoUnit.DAYS);

        Shift shift1 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day1.minusSeconds(1)).setTo(day1));
        Shift shift2 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day2.minusSeconds(1)).setTo(day2));
        Shift shift3 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day3.minusSeconds(1)).setTo(day3));
        Shift shift4 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day4.minusSeconds(1)).setTo(day4));

        service.addUserToShift(shift2.getId(), userId);
        service.addUserToShift(shift3.getId(), userId);
        service.addUserToShift(shift4.getId(), userId);
        Assertions.assertThrows(InvalidStateException.class, () -> service.addUserToShift(shift1.getId(), userId));
    }

    @Test
    void addUserToShift_moreThan5DaysInRow_2dayShifts_between() {
        UUID userId = UUID.randomUUID();
        UUID shopId = UUID.randomUUID();

        Instant day1 = Instant.now().truncatedTo(ChronoUnit.DAYS);
        Instant day2 = day1.plus(2, ChronoUnit.DAYS);
        Instant day3 = day2.plus(2, ChronoUnit.DAYS);

        Shift shift1 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day1.minusSeconds(1)).setTo(day1));
        Shift shift2 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day2.minusSeconds(1)).setTo(day2));
        Shift shift3 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day3.minusSeconds(1)).setTo(day3));

        service.addUserToShift(shift1.getId(), userId);
        service.addUserToShift(shift3.getId(), userId);
        Assertions.assertThrows(InvalidStateException.class, () -> service.addUserToShift(shift2.getId(), userId));
    }

    @Test
    void addUserToShift_moreThan5DaysInRow_2dayShifts_after() {
        UUID userId = UUID.randomUUID();
        UUID shopId = UUID.randomUUID();

        Instant day1 = Instant.now().truncatedTo(ChronoUnit.DAYS);
        Instant day0 = day1.minus(3, ChronoUnit.DAYS);
        Instant day2 = day1.plus(2, ChronoUnit.DAYS);
        Instant day3 = day2.plus(2, ChronoUnit.DAYS);

        Shift shift0 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day0.minusSeconds(1)).setTo(day0));
        Shift shift1 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day1.minusSeconds(1)).setTo(day1));
        Shift shift2 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day2.minusSeconds(1)).setTo(day2));
        Shift shift3 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day3.minusSeconds(1)).setTo(day3));

        service.addUserToShift(shift0.getId(), userId);
        service.addUserToShift(shift1.getId(), userId);
        service.addUserToShift(shift2.getId(), userId);
        Assertions.assertThrows(InvalidStateException.class, () -> service.addUserToShift(shift3.getId(), userId));
    }

    @Test
    void addUserToShift_moreThan5DaysInRow_1dayShifts_before() {
        UUID userId = UUID.randomUUID();
        UUID shopId = UUID.randomUUID();

        Instant day1 = Instant.now().truncatedTo(ChronoUnit.DAYS);
        Instant day2 = day1.plus(1, ChronoUnit.DAYS);
        Instant day3 = day2.plus(1, ChronoUnit.DAYS);
        Instant day4 = day3.plus(1, ChronoUnit.DAYS);
        Instant day5 = day4.plus(1, ChronoUnit.DAYS);
        Instant day6 = day5.plus(1, ChronoUnit.DAYS);
        Instant day7 = day6.plus(2, ChronoUnit.DAYS);

        Shift shift1 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day1).setTo(day1.plusSeconds(1)));
        Shift shift2 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day2).setTo(day2.plusSeconds(1)));
        Shift shift3 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day3).setTo(day3.plusSeconds(1)));
        Shift shift4 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day4).setTo(day4.plusSeconds(1)));
        Shift shift5 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day5).setTo(day5.plusSeconds(1)));
        Shift shift6 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day6).setTo(day6.plusSeconds(1)));
        Shift shift7 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day7).setTo(day7.plusSeconds(1)));

        service.addUserToShift(shift2.getId(), userId);
        service.addUserToShift(shift3.getId(), userId);
        service.addUserToShift(shift4.getId(), userId);
        service.addUserToShift(shift5.getId(), userId);
        service.addUserToShift(shift6.getId(), userId);
        service.addUserToShift(shift7.getId(), userId);
        Assertions.assertThrows(InvalidStateException.class, () -> service.addUserToShift(shift1.getId(), userId));
    }

    @Test
    void addUserToShift_moreThan5DaysInRow_1dayShifts_between() {
        UUID userId = UUID.randomUUID();
        UUID shopId = UUID.randomUUID();

        Instant day1 = Instant.now().truncatedTo(ChronoUnit.DAYS);
        Instant day2 = day1.plus(1, ChronoUnit.DAYS);
        Instant day3 = day2.plus(1, ChronoUnit.DAYS);
        Instant day4 = day3.plus(1, ChronoUnit.DAYS);
        Instant day5 = day4.plus(1, ChronoUnit.DAYS);
        Instant day6 = day5.plus(1, ChronoUnit.DAYS);

        Shift shift1 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day1).setTo(day1.plusSeconds(1)));
        Shift shift2 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day2).setTo(day2.plusSeconds(1)));
        Shift shift3 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day3).setTo(day3.plusSeconds(1)));
        Shift shift4 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day4).setTo(day4.plusSeconds(1)));
        Shift shift5 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day5).setTo(day5.plusSeconds(1)));
        Shift shift6 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day6).setTo(day6.plusSeconds(1)));

        service.addUserToShift(shift1.getId(), userId);
        service.addUserToShift(shift2.getId(), userId);
        service.addUserToShift(shift3.getId(), userId);
        service.addUserToShift(shift5.getId(), userId);
        service.addUserToShift(shift6.getId(), userId);
        Assertions.assertThrows(InvalidStateException.class, () -> service.addUserToShift(shift4.getId(), userId));
    }

    @Test
    void addUserToShift_moreThan5DaysInRow_1dayShifts_after() {
        UUID userId = UUID.randomUUID();
        UUID shopId = UUID.randomUUID();

        Instant day1 = Instant.now().truncatedTo(ChronoUnit.DAYS);
        Instant day2 = day1.plus(2, ChronoUnit.DAYS);
        Instant day3 = day2.plus(1, ChronoUnit.DAYS);
        Instant day4 = day3.plus(1, ChronoUnit.DAYS);
        Instant day5 = day4.plus(1, ChronoUnit.DAYS);
        Instant day6 = day5.plus(1, ChronoUnit.DAYS);
        Instant day7 = day6.plus(1, ChronoUnit.DAYS);

        Shift shift1 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day1).setTo(day1.plusSeconds(1)));
        Shift shift2 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day2).setTo(day2.plusSeconds(1)));
        Shift shift3 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day3).setTo(day3.plusSeconds(1)));
        Shift shift4 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day4).setTo(day4.plusSeconds(1)));
        Shift shift5 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day5).setTo(day5.plusSeconds(1)));
        Shift shift6 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day6).setTo(day6.plusSeconds(1)));
        Shift shift7 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day7).setTo(day7.plusSeconds(1)));

        service.addUserToShift(shift1.getId(), userId);
        service.addUserToShift(shift2.getId(), userId);
        service.addUserToShift(shift3.getId(), userId);
        service.addUserToShift(shift4.getId(), userId);
        service.addUserToShift(shift5.getId(), userId);
        service.addUserToShift(shift6.getId(), userId);
        Assertions.assertThrows(InvalidStateException.class, () -> service.addUserToShift(shift7.getId(), userId));
    }

    @Test
    void addUserToShift_moreThan8HoursWithin24Hours_before() {
        UUID userId = UUID.randomUUID();
        UUID shopId = UUID.randomUUID();

        Instant day1Start = Instant.now().truncatedTo(ChronoUnit.DAYS).plus(23, ChronoUnit.HOURS);
        Instant day1End = day1Start.plus(4, ChronoUnit.HOURS);
        Instant day2Start = day1End.plus(4, ChronoUnit.HOURS);
        Instant day2End = day2Start.plus(4, ChronoUnit.HOURS);

        Shift shift1 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day1Start).setTo(day1End));
        Shift shift2 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day2Start).setTo(day2End));
        Shift shift3 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day1Start.minusMillis(2)).setTo(day1Start.minusMillis(1)));

        service.addUserToShift(shift1.getId(), userId);
        service.addUserToShift(shift2.getId(), userId);
        Assertions.assertThrows(InvalidStateException.class, () -> service.addUserToShift(shift3.getId(), userId));
    }

    @Test
    void addUserToShift_moreThan8HoursWithin24Hours_during() {
        UUID userId = UUID.randomUUID();
        UUID shopId = UUID.randomUUID();

        Instant day1Start = Instant.now().truncatedTo(ChronoUnit.DAYS).plus(23, ChronoUnit.HOURS);
        Instant day1End = day1Start.plus(4, ChronoUnit.HOURS);
        Instant day2Start = day1End.plus(4, ChronoUnit.HOURS);
        Instant day2End = day2Start.plus(4, ChronoUnit.HOURS);

        Shift shift1 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day1Start).setTo(day1End));
        Shift shift2 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day2Start).setTo(day2End));
        Shift shift3 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day1End.plusMillis(1)).setTo(day1End.plusMillis(2)));

        service.addUserToShift(shift1.getId(), userId);
        service.addUserToShift(shift2.getId(), userId);
        Assertions.assertThrows(InvalidStateException.class, () -> service.addUserToShift(shift3.getId(), userId));
    }

    @Test
    void addUserToShift_moreThan8HoursWithin24Hours_after() {
        UUID userId = UUID.randomUUID();
        UUID shopId = UUID.randomUUID();

        Instant day1Start = Instant.now().truncatedTo(ChronoUnit.DAYS).plus(23, ChronoUnit.HOURS);
        Instant day1End = day1Start.plus(4, ChronoUnit.HOURS);
        Instant day2Start = day1End.plus(4, ChronoUnit.HOURS);
        Instant day2End = day2Start.plus(4, ChronoUnit.HOURS);

        Shift shift1 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day1Start).setTo(day1End));
        Shift shift2 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day2Start).setTo(day2End));
        Shift shift3 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day2End.plusMillis(1)).setTo(day2End.plusMillis(2)));

        service.addUserToShift(shift1.getId(), userId);
        service.addUserToShift(shift2.getId(), userId);
        Assertions.assertThrows(InvalidStateException.class, () -> service.addUserToShift(shift3.getId(), userId));
    }

    @Test
    void addUserToShift_moreThan8HoursWithin24Hours_overlappingShifts() {
        UUID userId = UUID.randomUUID();
        UUID shopId = UUID.randomUUID();

        Instant day1Start = Instant.now().truncatedTo(ChronoUnit.DAYS);
        Instant day1End = day1Start.plus(8, ChronoUnit.HOURS);
        Instant day2Start = day1Start.plus(1, ChronoUnit.DAYS).plus(4, ChronoUnit.HOURS);
        Instant day2End = day2Start.plus(2, ChronoUnit.HOURS);
        Instant day2StartShift2 = day2End.minus(1, ChronoUnit.HOURS);
        Instant day2EndShift2 = day2End.plus(1, ChronoUnit.HOURS);

        Shift shift1 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day1Start).setTo(day1End));
        Shift shift2 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day2StartShift2).setTo(day2EndShift2));
        Shift shift3 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day2Start).setTo(day2End));
        Shift shift4 = service.createShift(new ShiftRequests.Create()
                .setShopId(shopId).setFrom(day1End.plusMillis(1)).setTo(day1End.plusMillis(2)));

        service.addUserToShift(shift1.getId(), userId);
        service.addUserToShift(shift2.getId(), userId);
        service.addUserToShift(shift3.getId(), userId);
        Assertions.assertThrows(InvalidStateException.class, () -> service.addUserToShift(shift4.getId(), userId));
    }

    @Test
    void addUserToShift_multipleShiftsAtDifferentShopsAtSameTime_beforeAndDuring() {
        UUID userId = UUID.randomUUID();

        Instant timestamp1 = Instant.now();
        Instant timestamp2 = timestamp1.plusSeconds(10);
        Instant timestamp3 = timestamp2.plusSeconds(10);
        Instant timestamp4 = timestamp3.plusSeconds(10);

        Shift shift1 = service.createShift(new ShiftRequests.Create()
                .setShopId(UUID.randomUUID()).setFrom(timestamp1).setTo(timestamp2));
        Shift shift2 = service.createShift(new ShiftRequests.Create()
                .setShopId(UUID.randomUUID()).setFrom(timestamp2).setTo(timestamp3));
        Shift shift3 = service.createShift(new ShiftRequests.Create()
                .setShopId(UUID.randomUUID()).setFrom(timestamp3).setTo(timestamp4));

        service.addUserToShift(shift3.getId(), userId);
        Assertions.assertThrows(InvalidStateException.class, () -> service.addUserToShift(shift2.getId(), userId));
        service.addUserToShift(shift1.getId(), userId);
    }

    @Test
    void addUserToShift_multipleShiftsAtDifferentShopsAtSameTime_within() {
        UUID userId = UUID.randomUUID();

        Instant timestamp1 = Instant.now();
        Instant timestamp2 = timestamp1.plusSeconds(10);
        Instant timestamp3 = timestamp1.plusSeconds(1);
        Instant timestamp4 = timestamp2.minusSeconds(1);

        Shift shift1 = service.createShift(new ShiftRequests.Create()
                .setShopId(UUID.randomUUID()).setFrom(timestamp1).setTo(timestamp2));
        Shift shift2 = service.createShift(new ShiftRequests.Create()
                .setShopId(UUID.randomUUID()).setFrom(timestamp3).setTo(timestamp4));

        service.addUserToShift(shift1.getId(), userId);
        Assertions.assertThrows(InvalidStateException.class, () -> service.addUserToShift(shift2.getId(), userId));
    }

    @Test
    void addUserToShift_multipleShiftsAtDifferentShopsAtSameTime_duringAndAfter() {
        UUID userId = UUID.randomUUID();

        Instant timestamp1 = Instant.now();
        Instant timestamp2 = timestamp1.plusSeconds(10);
        Instant timestamp3 = timestamp2.plusSeconds(10);
        Instant timestamp4 = timestamp3.plusSeconds(10);

        Shift shift1 = service.createShift(new ShiftRequests.Create()
                .setShopId(UUID.randomUUID()).setFrom(timestamp1).setTo(timestamp2));
        Shift shift2 = service.createShift(new ShiftRequests.Create()
                .setShopId(UUID.randomUUID()).setFrom(timestamp2).setTo(timestamp3));
        Shift shift3 = service.createShift(new ShiftRequests.Create()
                .setShopId(UUID.randomUUID()).setFrom(timestamp3).setTo(timestamp4));

        service.addUserToShift(shift1.getId(), userId);
        Assertions.assertThrows(InvalidStateException.class, () -> service.addUserToShift(shift2.getId(), userId));
        service.addUserToShift(shift3.getId(), userId);
    }
}