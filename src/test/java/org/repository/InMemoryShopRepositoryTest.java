package org.repository;

import org.junit.jupiter.api.BeforeEach;

class InMemoryShopRepositoryTest implements ShopRepositoryTest {
    private InMemoryShopRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryShopRepository();
    }

    @Override
    public ShopRepository getRepository() {
        return repository;
    }
}