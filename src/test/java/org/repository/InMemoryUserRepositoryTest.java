package org.repository;

import org.junit.jupiter.api.BeforeEach;

class InMemoryUserRepositoryTest implements UserRepositoryTest {
    private InMemoryUserRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
    }

    @Override
    public UserRepository getRepository() {
        return repository;
    }
}