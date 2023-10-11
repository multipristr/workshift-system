package org.controller;

import org.controller.request.ShiftRequests;
import org.model.Shift;
import org.service.ShiftService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("shifts")
public class ShiftController {
    private final ShiftService service;

    public ShiftController(ShiftService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Object> createShift(@RequestBody ShiftRequests.Create request) {
        Shift shift = service.createShift(request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(shift.getId())
                        .toUri())
                .build();
    }

    @PutMapping("{shiftId}/user/{userId}")
    public ResponseEntity<Object> addUserToShift(UUID shiftId, UUID userId) {
        service.addUserToShift(shiftId, userId);
        return ResponseEntity.noContent().build();
    }
}
