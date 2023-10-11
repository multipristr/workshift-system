package org.controller.request;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public class ShiftRequests {
    private ShiftRequests() throws IllegalAccessException {
        throw new IllegalAccessException("Single instance protection");
    }

    public static final class Create implements Serializable {
        private static final long serialVersionUID = -5126303383544957293L;
        private UUID shopId; // TODO FIXME can shift exist without a shop?
        private Instant from;
        private Instant to;

        public UUID getShopId() {
            return shopId;
        }

        public Create setShopId(UUID shopId) {
            this.shopId = shopId;
            return this;
        }

        public Instant getFrom() {
            return from;
        }

        public Create setFrom(Instant from) {
            this.from = from;
            return this;
        }

        public Instant getTo() {
            return to;
        }

        public Create setTo(Instant to) {
            this.to = to;
            return this;
        }
    }
}
