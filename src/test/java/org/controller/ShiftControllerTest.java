package org.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.configuration.SpringConfiguration;
import org.controller.request.ShiftRequests;
import org.junit.jupiter.api.Test;
import org.model.Shift;
import org.repository.ShiftRepository;
import org.service.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;
import java.util.UUID;

@WebMvcTest(ShiftController.class)
@Import({ShiftController.class, ShiftService.class, RestExceptionHandler.class})
@ContextConfiguration(classes = SpringConfiguration.class)
class ShiftControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ShiftRepository shiftRepository;

    @Test
    void createShift() throws Exception {
        ShiftRequests.Create create = new ShiftRequests.Create()
                .setFrom(Instant.MIN)
                .setTo(Instant.MAX)
                .setShopId(UUID.randomUUID());
        mockMvc.perform(MockMvcRequestBuilders.post("/shifts")
                        .content(objectMapper.writeValueAsString(create))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists("Location"));
    }

    @Test
    void createShift_wrongTime() throws Exception {
        ShiftRequests.Create create = new ShiftRequests.Create()
                .setTo(Instant.MIN)
                .setFrom(Instant.MIN)
                .setShopId(UUID.randomUUID());
        mockMvc.perform(MockMvcRequestBuilders.post("/shifts")
                        .content(objectMapper.writeValueAsString(create))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    void addUserToShift() throws Exception {
        Shift shift = new Shift(UUID.randomUUID(), UUID.randomUUID(), Instant.now().minusSeconds(9), Instant.now().plusSeconds(9));
        shiftRepository.persist(shift);
        mockMvc.perform(MockMvcRequestBuilders.put("/shifts/{shiftId}/user/{userId}", shift.getId(), UUID.randomUUID()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void addUserToShift_multipleAtSameTime() throws Exception {
        UUID userId = UUID.randomUUID();
        Shift shift1 = new Shift(UUID.randomUUID(), UUID.randomUUID(), Instant.now().minusSeconds(9), Instant.now().plusSeconds(9));
        shift1.addUser(userId);
        shiftRepository.persist(shift1);
        Shift shift2 = new Shift(UUID.randomUUID(), UUID.randomUUID(), Instant.now().minusSeconds(9), Instant.now().plusSeconds(9));
        shiftRepository.persist(shift2);
        mockMvc.perform(MockMvcRequestBuilders.put("/shifts/{shiftId}/user/{userId}", shift2.getId(), userId))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }
}