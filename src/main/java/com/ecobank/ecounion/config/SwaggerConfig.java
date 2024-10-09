package com.ecobank.ecounion.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;


import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;


@EnableWebSecurity
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "EcoAjo API",
                version = "Version 1.2",
                contact = @Contact(
                        name = "Innovations", email = "olahammed@ecobank.com", url = "https://olahammed.ecobank.org"
                ),
                license = @License(
                        name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0"
                ),
                termsOfService = "https://ecobank.com/TOS",
                description = "Spring Boot Restful API demo by innovations team"
        )
)
public class SwaggerConfig {
//
//    @Bean
//    public Docket api() {
//        return new Docket(DocumentationType.OAS_30)
//                .apiInfo(apiInfo())
//                .securitySchemes(basicAuthScheme())
//                .securityContexts(Arrays.asList(securityContext()))
//                .select()
//                .apis(RequestHandlerSelectors.basePackage("com.ecobank.ecounion"))
//                .paths(PathSelectors.any())
//                .build();
//    }
//
//    private ApiInfo apiInfo() {
//        return new ApiInfoBuilder()
//                .title("EcoAjo API")
//                .description("Spring Boot Restful API demo by innovations team")
//                .version("1.0.0")
//                .termsOfServiceUrl("https://ecobank.com/TOS")
//                .license("Apache 2.0")
//                .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0")
//                .build();
//    }
//
//    private List<SecurityScheme> basicAuthScheme() {
//        return Arrays.asList(new BasicAuth("basicAuth"));
//    }
//
//    private SecurityContext securityContext() {
//        return SecurityContext.builder()
//                .securityReferences(basicAuthReference())
//                .operationSelector(o -> o.requestMappingPattern().matches("/.*"))
//                .build();
//    }
//
//    private List<SecurityReference> basicAuthReference() {
//        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
//        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
//        authorizationScopes[0] = authorizationScope;
//        return Arrays.asList(new SecurityReference("basicAuth", authorizationScopes));
//    }

}