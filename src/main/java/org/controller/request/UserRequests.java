package org.controller.request;

import java.io.Serializable;

public class UserRequests {
    private UserRequests() throws IllegalAccessException {
        throw new IllegalAccessException("Single instance protection");
    }

    public static final class Create implements Serializable {
        private static final long serialVersionUID = -5126303383544957293L;
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
