package org.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.configuration.SpringConfiguration;
import org.controller.request.ShopRequests;
import org.junit.jupiter.api.Test;
import org.model.Shop;
import org.repository.ShopRepository;
import org.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

@WebMvcTest(ShopController.class)
@Import({ShopController.class, ShopService.class, RestExceptionHandler.class})
@ContextConfiguration(classes = SpringConfiguration.class)
class ShopControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ShopRepository shopRepository;

    @Test
    void createShop() throws Exception {
        ShopRequests.Create create = new ShopRequests.Create().setName("name");
        mockMvc.perform(MockMvcRequestBuilders.post("/shops")
                        .content(objectMapper.writeValueAsString(create))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists("Location"));
    }

    @Test
    void addUserToShop() throws Exception {
        Shop shop = new Shop(UUID.randomUUID(), "name");
        shopRepository.persist(shop);
        mockMvc.perform(MockMvcRequestBuilders.put("/shops/{shopId}/user/{userId}", shop.getId(), UUID.randomUUID()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void addUserToShop_missingShop() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/shops/{shopId}/user/{userId}", UUID.randomUUID(), UUID.randomUUID()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}