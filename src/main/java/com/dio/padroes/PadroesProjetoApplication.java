package com.dio.padroes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PadroesProjetoApplication {

    public static void main(String[] args) {
        SpringApplication.run(PadroesProjetoApplication.class, args);
    }
}
