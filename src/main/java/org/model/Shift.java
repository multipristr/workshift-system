package org.model;


import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Shift implements Serializable {
    private static final long serialVersionUID = -7952534656869L;
    private final Set<User> users = new HashSet<>(); // junction table
    private final UUID id;
    private Shop shop; // foreign key
    private Instant from;
    private Instant to;

    public Shift(UUID id, Shop shop, Instant from, Instant to) {
        this.id = id;
        this.shop = shop;
        this.from = from;
        this.to = to;
    }

    public UUID getId() {
        return id;
    }

    public Shop getShop() {
        return shop;
    }

    public Shift setShop(Shop shop) {
        this.shop = shop;
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

    public Shift addUser(User user) {
        users.add(user);
        return this;
    }

    public Set<User> getUsers() {
        return users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shift shift = (Shift) o;
        return Objects.equals(users, shift.users) && Objects.equals(id, shift.id) && Objects.equals(shop, shift.shop) && Objects.equals(from, shift.from) && Objects.equals(to, shift.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(users, id, shop, from, to);
    }

    @Override
    public String toString() {
        return "Shift{id=" + id + '}';
    }
}
