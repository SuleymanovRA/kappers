package ru.kappers.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.security.SecurityRequirement
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SwaggerConfig {
    @Bean
    open fun productOpenApi(): OpenAPI = OpenAPI()
            .info(productInfo())
            .addSecurityItem(SecurityRequirement()
                .addList("basicAuth"))

    private fun productInfo(): Info = Info()
        .title("Kappers API Documents")
        .description("Kappers service API Documents")
        .version("v1")
        .contact(Contact()
            .name("Ashamaz Shomakhov")
            .url("https://github.com/soufee/kappers")
            .email("soufee@mail.ru"))
        .license(License()
            .name("Kappers Copyright")
            .url("https://github.com/soufee/kappers"))
}