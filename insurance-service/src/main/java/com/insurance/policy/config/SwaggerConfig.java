package com.insurance.policy.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI insuranceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Insurance Service API")
                        .description("API for insurance policy simulation and contracting")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Insurance Platform Team")
                                .email("insurance@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
