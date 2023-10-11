package org.model;


import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Shift implements Serializable {
    private static final long serialVersionUID = -7952534656869L;
    private final Set<UUID> userIds = new HashSet<>();
    private final UUID id;
    private UUID shopId;
    private Instant from;
    private Instant to;

    public Shift(UUID id, UUID shopId, Instant from, Instant to) {
        this.id = id;
        this.shopId = shopId;
        this.from = from;
        this.to = to;
    }

    public UUID getId() {
        return id;
    }

    public UUID getShopId() {
        return shopId;
    }

    public Shift setShopId(UUID shopId) {
        this.shopId = shopId;
        return this;
    }

    public Instant getFrom() {
        return from;
    }

    public Shift setFrom(Instant from) {
        this.from = from;
        return this;
    }

    public Instant getTo() {
        return to;
    }

    public Shift setTo(Instant to) {
        this.to = to;
        return this;
    }

    public Shift addUser(UUID userId) {
        userIds.add(userId);
        return this;
    }

    public Set<UUID> getUserIds() {
        return userIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shift shift = (Shift) o;
        return Objects.equals(userIds, shift.userIds) && Objects.equals(id, shift.id) && Objects.equals(shopId, shift.shopId) && Objects.equals(from, shift.from) && Objects.equals(to, shift.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userIds, id, shopId, from, to);
    }
}
