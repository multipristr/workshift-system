package org.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("shifts")
public class ShiftController {

    @PostMapping
    public void createShift() {
    }
}
