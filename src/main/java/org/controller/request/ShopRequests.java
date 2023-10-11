package org.controller.request;

import java.io.Serializable;

public class ShopRequests {
    private ShopRequests() throws IllegalAccessException {
        throw new IllegalAccessException("Single instance protection");
    }

    public static final class Create implements Serializable {
        private static final long serialVersionUID = 1156937865797521504L;
        private String name;

        public String getName() {
            return name;
        }

        public Create setName(String name) {
            this.name = name;
            return this;
        }
    }
}
