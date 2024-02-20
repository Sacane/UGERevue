package fr.pentagon.ugeoverflow.config;

import fr.pentagon.ugeoverflow.controllers.dtos.requests.UserRegisterDTO;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import fr.pentagon.ugeoverflow.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

@Configuration
@ComponentScan
public class Config {
    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    @Bean
    public CommandLineRunner cmd(UserRepository userRepository, UserService userService) {
        return args -> {
            /*userRepository.deleteAll();
            userService.register(new UserRegisterDTO("quentin", "quentin", "quentin", "quentin"));*/
        };
    }
}
