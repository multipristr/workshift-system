package org.service;

import org.repository.ShiftRepository;
import org.springframework.stereotype.Service;

@Service
public class ShiftService {
    private final ShiftRepository repository;

    public ShiftService(ShiftRepository repository) {
        this.repository = repository;
    }
}
