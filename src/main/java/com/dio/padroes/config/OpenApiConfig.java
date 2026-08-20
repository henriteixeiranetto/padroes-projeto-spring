package com.dio.padroes.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Padrões de Projeto na Prática - API")
                        .version("1.0.0")
                        .description("""
                                API de estudo do desafio "Explorando Padroes de Projetos na Pratica com Java" (DIO),
                                evoluída a partir do laboratório lab-padroes-projeto-spring.

                                Doze padrões aplicados em código real: Singleton, Strategy, Factory, Facade,
                                Chain of Responsibility, Observer, Template Method, Adapter, Decorator, Builder,
                                Repository e DTO.

                                Comece por POST /api/clientes e depois consulte /api/padroes/eventos para ver
                                os observadores reagindo ao cadastro.
                                """)
                        .contact(new Contact().name("Desafio DIO - Padrões de Projeto"))
                        .license(new License().name("MIT")))
                .servers(List.of(new Server().url("/").description("Servidor local")));
    }
}
