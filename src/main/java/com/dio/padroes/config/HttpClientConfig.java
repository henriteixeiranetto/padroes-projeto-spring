package com.dio.padroes.config;

import com.dio.padroes.pattern.adapter.ViaCepClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class HttpClientConfig {

    @Bean
    public ViaCepClient viaCepClient(RestClient.Builder builder, AppProperties properties) {
        // timeout curto: sem isso a requisicao fica presa antes de cair para o modo offline
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout((int) properties.viaCep().connectTimeout().toMillis());
        requestFactory.setReadTimeout((int) properties.viaCep().readTimeout().toMillis());

        RestClient restClient = builder.clone()
                .baseUrl(properties.viaCep().url())
                .requestFactory(requestFactory)
                .build();

        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build()
                .createClient(ViaCepClient.class);
    }
}
