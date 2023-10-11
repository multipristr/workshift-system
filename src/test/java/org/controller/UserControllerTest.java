package org.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.configuration.SpringConfiguration;
import org.controller.request.UserRequests;
import org.junit.jupiter.api.Test;
import org.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(UserController.class)
@Import({UserController.class, UserService.class, RestExceptionHandler.class})
@ContextConfiguration(classes = SpringConfiguration.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUser() throws Exception {
        UserRequests.Create create = new UserRequests.Create().setName("name");
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(objectMapper.writeValueAsString(create))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists("Location"));
    }
}