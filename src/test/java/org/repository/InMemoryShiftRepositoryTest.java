package org.repository;

import org.junit.jupiter.api.BeforeEach;

class InMemoryShiftRepositoryTest implements ShiftRepositoryTest {
    private InMemoryShiftRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryShiftRepository();
    }

    @Override
    public ShiftRepository getRepository() {
        return repository;
    }
}