package org.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.configuration.SpringConfiguration;
import org.controller.request.ShopRequests;
import org.exception.MissingEntityException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.model.Shop;
import org.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

@WebMvcTest(ShopController.class)
@Import({ShopController.class, RestExceptionHandler.class})
@ContextConfiguration(classes = SpringConfiguration.class)
class ShopControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ShopService service;

    @Test
    void createShop() throws Exception {
        ShopRequests.Create create = new ShopRequests.Create().setName("name");
        UUID id = UUID.randomUUID();
        Mockito.when(service.createShop(Mockito.any())).thenReturn(new Shop(id, create.getName()));
        mockMvc.perform(MockMvcRequestBuilders.post("/shops")
                        .content(objectMapper.writeValueAsString(create))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string("Location", Matchers.endsWith("/shops/" + id)));
    }

    @Test
    void addUserToShop() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/shops/{shopId}/user/{userId}", UUID.randomUUID(), UUID.randomUUID()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void addUserToShop_missingShop() throws Exception {
        UUID shopId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Mockito.doThrow(MissingEntityException.class).when(service).addUserToShop(shopId, userId);
        mockMvc.perform(MockMvcRequestBuilders.put("/shops/{shopId}/user/{userId}", shopId, userId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}