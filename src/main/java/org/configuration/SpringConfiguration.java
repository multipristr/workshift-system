package org.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.repository.InMemoryShiftRepository;
import org.repository.InMemoryShopRepository;
import org.repository.InMemoryUserRepository;
import org.repository.ShiftRepository;
import org.repository.ShopRepository;
import org.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .findAndRegisterModules();
    }

    @Bean
    public ShiftRepository shiftRepository() {
        return new InMemoryShiftRepository();
    }

    @Bean
    public ShopRepository shopRepository() {
        return new InMemoryShopRepository();
    }

    @Bean
    public UserRepository userRepository() {
        return new InMemoryUserRepository();
    }
}
