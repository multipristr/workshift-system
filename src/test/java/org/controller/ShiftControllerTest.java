package org.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.configuration.SpringConfiguration;
import org.controller.request.ShiftRequests;
import org.junit.jupiter.api.Test;
import org.model.Shop;
import org.repository.ShopRepository;
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
    private ShopRepository shopRepository;

    @Test
    void createShift() throws Exception {
        Shop shop = new Shop(UUID.randomUUID(), "name");
        shopRepository.persist(shop);
        ShiftRequests.Create create = new ShiftRequests.Create()
                .setFrom(Instant.MIN)
                .setTo(Instant.MAX)
                .setShopId(shop.getId());
        mockMvc.perform(MockMvcRequestBuilders.post("/shifts")
                        .content(objectMapper.writeValueAsString(create))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists("Location"));
    }

    @Test
    void createShift_missingCompany() throws Exception {
        ShiftRequests.Create create = new ShiftRequests.Create()
                .setFrom(Instant.MIN)
                .setTo(Instant.MAX)
                .setShopId(UUID.randomUUID());
        mockMvc.perform(MockMvcRequestBuilders.post("/shifts")
                        .content(objectMapper.writeValueAsString(create))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
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
}