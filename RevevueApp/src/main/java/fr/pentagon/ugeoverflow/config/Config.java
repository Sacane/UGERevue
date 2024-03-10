package fr.pentagon.ugeoverflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ComponentScan
public class Config {
  @Bean
  public MultipartResolver multipartResolver() {
    return new StandardServletMultipartResolver();
  }
  @Bean
  public WebClient getWebClient(WebClient.Builder defaultBuilder) {
    return WebClient.create("http://localhost:7777/api");
  }
}
