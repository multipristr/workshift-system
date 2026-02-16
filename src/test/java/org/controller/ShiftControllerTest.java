package org.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.configuration.SpringConfiguration;
import org.controller.request.ShiftRequests;
import org.exception.InvalidStateException;
import org.exception.LogicalValidationException;
import org.exception.MissingEntityException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.model.Shift;
import org.service.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;
import java.util.UUID;

@WebMvcTest(ShiftController.class)
@Import({ShiftController.class, RestExceptionHandler.class})
@ContextConfiguration(classes = SpringConfiguration.class)
class ShiftControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ShiftService service;

    @Test
    void createShift() throws Exception {
        ShiftRequests.Create create = new ShiftRequests.Create()
                .setFrom(Instant.now().minusSeconds(9))
                .setTo(Instant.now().plusSeconds(9))
                .setShopId(UUID.randomUUID());
        UUID id = UUID.randomUUID();
        Mockito.when(service.createShift(Mockito.any())).thenReturn(new Shift(
                id,
                create.getShopId(),
                create.getFrom(),
                create.getTo()
        ));
        mockMvc.perform(MockMvcRequestBuilders.post("/shifts")
                        .content(objectMapper.writeValueAsString(create))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string("Location", Matchers.endsWith("/shifts/" + id)));
    }

    @Test
    void createShift_wrongTime() throws Exception {
        ShiftRequests.Create create = new ShiftRequests.Create()
                .setTo(Instant.now().minusSeconds(9))
                .setFrom(Instant.now().plusSeconds(9))
                .setShopId(UUID.randomUUID());
        Mockito.when(service.createShift(Mockito.any())).thenThrow(LogicalValidationException.class);
        mockMvc.perform(MockMvcRequestBuilders.post("/shifts")
                        .content(objectMapper.writeValueAsString(create))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    void addUserToShift() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/shifts/{shiftId}/user/{userId}", UUID.randomUUID(), UUID.randomUUID()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void addUserToShift_multipleAtSameTime() throws Exception {
        UUID shiftId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Mockito.doThrow(InvalidStateException.class).when(service).addUserToShift(shiftId, userId);
        mockMvc.perform(MockMvcRequestBuilders.put("/shifts/{shiftId}/user/{userId}", shiftId, userId))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    void addUserToShift_missingShift() throws Exception {
        UUID shiftId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Mockito.doThrow(MissingEntityException.class).when(service).addUserToShift(shiftId, userId);
        mockMvc.perform(MockMvcRequestBuilders.put("/shifts/{shiftId}/user/{userId}", shiftId, userId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}