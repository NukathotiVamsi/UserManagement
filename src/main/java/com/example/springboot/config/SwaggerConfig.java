package com.example.springboot.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

	@Bean
	protected OpenAPI customOpenAPI() {
	    return new OpenAPI()
	            // Adding security item for bearer authentication
	            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
	            // Configuring security scheme for JWT
	            .components(new io.swagger.v3.oas.models.Components().addSecuritySchemes("bearerAuth",
	                    new SecurityScheme()
	                            .type(SecurityScheme.Type.HTTP)
	                            .scheme("bearer")
	                            .bearerFormat("JWT")
	                            .in(SecurityScheme.In.HEADER)
	                            .name("Authorization")))
	            // Setting API information
	            .info(new Info()
	                    .title("User Management")
	                    .description("API Documentation for managing JWT authentication and authorization")
	                    .version("1.0")
	                    .contact(new Contact()
	                            .name("Vamsi")
	                            .url("https://www.google.com"))
	                    
	            );
	}

}


//    @Bean
//    public OpenAPI customOpenAPI() {
//        return new OpenAPI()
//                .addServersItem(new Server().url("/")) // Define the base path for your API
//                .info(new Info()
//                        .title("User Management API")
//                        .description("API for managing users")
//                        .version("1.0.0")
//                        .contact(new Contact()
//                                .name("Vamsi")
//                                .url("https://www.google.com")
//                                .email("javalucky88@gmail.com")));
//    }

