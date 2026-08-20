package com.dio.padroes.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app")
public record AppProperties(ViaCep viaCep, Seed seed) {

    public AppProperties {
        if (viaCep == null) {
            viaCep = new ViaCep(null, null, null, true);
        }
        if (seed == null) {
            seed = new Seed(true);
        }
    }

    public record ViaCep(String url, Duration connectTimeout, Duration readTimeout, boolean fallbackOffline) {

        public ViaCep {
            if (url == null || url.isBlank()) {
                url = "https://viacep.com.br/ws";
            }
            if (connectTimeout == null) {
                connectTimeout = Duration.ofSeconds(3);
            }
            if (readTimeout == null) {
                readTimeout = Duration.ofSeconds(5);
            }
        }
    }

    public record Seed(boolean enabled) {
    }
}
