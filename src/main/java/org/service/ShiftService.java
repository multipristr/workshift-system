package org.service;

import org.controller.request.ShiftRequests;
import org.exception.InvalidStateException;
import org.exception.LogicalValidationException;
import org.exception.MissingEntityException;
import org.model.Shift;
import org.repository.ShiftRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ShiftService {
    private static final int MAX_WORK_MILLISECONDS_IN_PAST_24_HOURS = 8 * 3600000; // TODO FIXME maybe move to properties
    private static final int MAX_DAYS_IN_SAME_SHOP_IN_PAST_5_DAYS = 5; // TODO FIXME maybe move to properties
    private final ShiftRepository repository;

    public ShiftService(ShiftRepository repository) {
        this.repository = repository;
    }

    public Shift createShift(ShiftRequests.Create shiftCreate) {
        if (!shiftCreate.getTo().isAfter(shiftCreate.getFrom())) {
            throw new LogicalValidationException("Shift at shop '" + shiftCreate.getShopId()
                    + "' ends before it has started - it's from " + shiftCreate.getFrom() + " to " + shiftCreate.getTo());
        }

        Shift shift = new Shift(UUID.randomUUID(), shiftCreate.getShopId(), shiftCreate.getFrom(), shiftCreate.getTo());
        repository.persist(shift);
        return shift;
    }

    public void addUserToShift(UUID shiftId, UUID userId) {
        Optional<Shift> found = repository.find(shiftId);
        if (!found.isPresent()) {
            throw new MissingEntityException("No shift with id " + shiftId);
        }
        Shift shift = found.get();

        Instant shiftFirstMoment5DaysAgo = shift.getTo().minus(5, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
        Instant shiftLastMoment = shift.getTo().atOffset(ZoneOffset.UTC).with(LocalTime.MAX).toInstant();
        List<Shift> userShiftsInFiveDays = repository.findUserShiftsBetween(userId, shiftFirstMoment5DaysAgo, shiftLastMoment);

        // No user can work more than 5 days in a row in the same shop
        int daysWorkedInShopIn5Days = countDaysWorkedInRow(shift, userShiftsInFiveDays);
        if (daysWorkedInShopIn5Days > MAX_DAYS_IN_SAME_SHOP_IN_PAST_5_DAYS) {
            throw new InvalidStateException("Cannot add user '" + userId + "' to shift '" + shiftId + "' at shop '" + shift.getShopId() +
                    "' because the user has already worked in that shop more than " + MAX_DAYS_IN_SAME_SHOP_IN_PAST_5_DAYS + " in a row.");
        }

        // No user is allowed to work in the same shop for more than 8 hours, within a 24 hour window
        long millisecondsWorkedInShopIn24Hours = countMillisecondsWorkedInShopIn24Hours(shift, userShiftsInFiveDays);
        if (millisecondsWorkedInShopIn24Hours >= MAX_WORK_MILLISECONDS_IN_PAST_24_HOURS) {
            throw new InvalidStateException("Cannot add user '" + userId + "' to shift '" + shiftId + "' at shop '" + shift.getShopId() +
                    "' because the user has already worked in that shop more than " + MAX_WORK_MILLISECONDS_IN_PAST_24_HOURS / 3600000 + " hour in the past 24 hours.");
        }

        // A user can not work in multiple shops at the same time
        Optional<Shift> shiftAtDifferentShopAtSameTime = findShiftAtSameTimeInAnotherShop(shift, userShiftsInFiveDays);
        if (shiftAtDifferentShopAtSameTime.isPresent()) {
            throw new InvalidStateException("Cannot add user '" + userId + "' to shift '" + shiftId + "' at shop '" + shift.getShopId() +
                    "' because the user already has shift at the same time in shop " + shiftAtDifferentShopAtSameTime.get().getShopId());
        }

        shift.addUser(userId);
        repository.persist(shift);
    }

    private int countDaysWorkedInRow(Shift shift, List<Shift> userShifts) {
        Set<LocalDate> daysWorkedInShopIn5Days = userShifts.stream()
                .map(oldShift -> Arrays.asList(oldShift.getFrom().atZone(ZoneOffset.UTC).toLocalDate(), oldShift.getTo().atZone(ZoneOffset.UTC).toLocalDate()))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        daysWorkedInShopIn5Days.add(shift.getFrom().atZone(ZoneOffset.UTC).toLocalDate());
        daysWorkedInShopIn5Days.add(shift.getTo().atZone(ZoneOffset.UTC).toLocalDate());
        return daysWorkedInShopIn5Days.size();
    }

    private long countMillisecondsWorkedInShopIn24Hours(Shift shift, List<Shift> userShifts) {
        Instant twentyFourHoursBefore = shift.getTo().minus(24, ChronoUnit.HOURS);
        return userShifts.stream()
                .filter(oldShift -> oldShift.getShopId().equals(shift.getShopId()))
                .filter(oldShift -> !oldShift.getTo().isBefore(twentyFourHoursBefore) || !oldShift.getFrom().isAfter(shift.getTo()))
                .map(oldShift -> {
                    Instant from = oldShift.getFrom().isAfter(twentyFourHoursBefore) ? oldShift.getFrom() : twentyFourHoursBefore;
                    Instant to = oldShift.getTo().isAfter(shift.getTo()) ? shift.getTo() : oldShift.getTo();
                    return Duration.between(from, to);
                })
                .mapToLong(duration -> duration.toMillis() + 1) // +1 because duration end is exclusive
                .sum();
    }

    private Optional<Shift> findShiftAtSameTimeInAnotherShop(Shift shift, List<Shift> userShifts) {
        return userShifts.stream()
                .filter(oldShift -> !oldShift.getShopId().equals(shift.getShopId()))
                .filter(oldShift -> !oldShift.getTo().isBefore(shift.getFrom()) || !oldShift.getFrom().isAfter(shift.getTo()))
                .findAny();
    }
}
