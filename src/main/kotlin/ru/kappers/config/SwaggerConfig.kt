package ru.kappers.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.swagger2.annotations.EnableSwagger2
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.BasicAuth
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket

@Configuration
@EnableSwagger2
open class SwaggerConfig {
    @Bean
    open fun productApi(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .securitySchemes(arrayListOf(BasicAuth("basicAuth")))
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("ru.kappers.logic.controller"))
                .build()
    }

    private fun apiInfo(): ApiInfo {
        return ApiInfo("Kappers API Documents", "Kappers service API Documents",
                "v1", "https://github.com/soufee/kappers",
                Contact("Ashamaz Shomakhov", "https://github.com/soufee/kappers", "soufee@mail.ru"),
                "Kappers Copyright", "https://github.com/soufee/kappers", emptyList())
    }
}