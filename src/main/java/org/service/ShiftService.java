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
import java.time.Period;
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
    private static final Duration MAX_WORKED_IN_PAST_24_HOURS = Duration.ofHours(8); // TODO FIXME maybe move to properties
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

        Period fiveDays = Period.ofDays(5);
        Instant fiveDayWindowStart = shift.getTo().minus(fiveDays).truncatedTo(ChronoUnit.DAYS);
        Instant fiveDayWindowEnd = shift.getFrom().plus(fiveDays).atOffset(ZoneOffset.UTC).with(LocalTime.MAX).toInstant();
        List<Shift> userShiftsInFiveDays = repository.findUserShiftsBetween(userId, fiveDayWindowStart, fiveDayWindowEnd);

        // No user can work more than 5 days in a row in the same shop
        int daysWorkedInShopIn5Days = countDaysWorkedInRow(shift, userShiftsInFiveDays, fiveDayWindowStart, fiveDayWindowEnd);
        if (daysWorkedInShopIn5Days > MAX_DAYS_IN_SAME_SHOP_IN_PAST_5_DAYS) {
            throw new InvalidStateException("Cannot add user '" + userId + "' to shift '" + shiftId + "' at shop '" + shift.getShopId() +
                    "' because the user has already worked in that shop more than " + MAX_DAYS_IN_SAME_SHOP_IN_PAST_5_DAYS + " in a row.");
        }

        // No user is allowed to work in the same shop for more than 8 hours, within a 24 hour window
        Duration workedInShopIn24Hours = countWorkedInShopInPeriod(shift, userShiftsInFiveDays, Duration.ofHours(24));
        if (workedInShopIn24Hours.compareTo(MAX_WORKED_IN_PAST_24_HOURS) > 0) {
            throw new InvalidStateException("Cannot add user '" + userId + "' to shift '" + shiftId + "' at shop '" + shift.getShopId() +
                    "' because the user has already worked in that shop more than " + MAX_WORKED_IN_PAST_24_HOURS.toHours() + " hour in the past 24 hours.");
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

    private int countDaysWorkedInRow(Shift shift, List<Shift> userShifts, Instant start, Instant end) {
        Set<LocalDate> daysWorkedInShopIn5Days = userShifts.stream()
                .filter(oldShift -> oldShift.getShopId().equals(shift.getShopId()))
                .map(oldShift -> Arrays.asList(oldShift.getFrom().atZone(ZoneOffset.UTC).toLocalDate(), oldShift.getTo().atZone(ZoneOffset.UTC).toLocalDate()))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        daysWorkedInShopIn5Days.add(shift.getFrom().atZone(ZoneOffset.UTC).toLocalDate());
        daysWorkedInShopIn5Days.add(shift.getTo().atZone(ZoneOffset.UTC).toLocalDate());

        LocalDate endDay = end.atZone(ZoneOffset.UTC).toLocalDate();
        int longestDaysInRow = 0;
        int daysInRow = 0;
        for (LocalDate day = start.atZone(ZoneOffset.UTC).toLocalDate(); !day.isAfter(endDay); day = day.plusDays(1)) {
            if (daysWorkedInShopIn5Days.contains(day)) {
                ++daysInRow;
            } else {
                longestDaysInRow = Math.max(longestDaysInRow, daysInRow);
                daysInRow = 0;
            }
        }
        longestDaysInRow = Math.max(longestDaysInRow, daysInRow);
        return longestDaysInRow;
    }

    private Duration countWorkedInShopInPeriod(Shift shift, List<Shift> userShifts, Duration duration) {
        Instant windowStart = shift.getTo().minus(duration);
        Instant windowEnd = shift.getFrom().plus(duration);
        return countMillisecondsWorkedInShop(shift, userShifts, shift.getFrom(), windowEnd) // shifts 24 hours after start
                .plus(countMillisecondsWorkedInShop(shift, userShifts, windowStart, shift.getTo())) // shifts 24 hours before end
                .plus(Duration.between(shift.getFrom(), shift.getTo()));
    }

    private Duration countMillisecondsWorkedInShop(Shift shift, List<Shift> userShifts, Instant start, Instant end) {
        return userShifts.stream()
                .filter(oldShift -> oldShift.getShopId().equals(shift.getShopId()))
                .filter(oldShift -> !oldShift.getTo().isBefore(start) && !oldShift.getFrom().isAfter(end))
                .map(oldShift -> {
                    Instant from = oldShift.getFrom().isAfter(start) ? oldShift.getFrom() : start;
                    Instant to = oldShift.getTo().isAfter(end) ? end : oldShift.getTo();
                    return Duration.between(from, to);
                })
                .reduce(Duration.ofHours(0), Duration::plus);
    }

    private Optional<Shift> findShiftAtSameTimeInAnotherShop(Shift shift, List<Shift> userShifts) {
        return userShifts.stream()
                .filter(oldShift -> !oldShift.getShopId().equals(shift.getShopId()))
                .filter(oldShift -> (!oldShift.getFrom().isAfter(shift.getFrom()) && !oldShift.getTo().isBefore(shift.getFrom())) // old shift before & during new shift
                        || (!oldShift.getFrom().isAfter(shift.getTo()) && !oldShift.getTo().isBefore(shift.getTo()))) // old shift during & after new shift
                .findAny();
    }
}
