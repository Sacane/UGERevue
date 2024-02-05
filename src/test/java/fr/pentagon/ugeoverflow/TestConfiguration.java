package fr.pentagon.ugeoverflow;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ComponentScan
@Profile(value = "test")
public class TestConfiguration {
}
