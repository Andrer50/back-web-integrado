package com.utp.backwebintegrado.config;

import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {
    @Value("${api.auth.uri}")
    private String authBaseUri;

    @Bean
    public WebClient authWebClient() {
        return buildWebClient(authBaseUri);
    }

    private WebClient buildWebClient(String baseUrl) {
        try {
            var sslContext = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
            return WebClient.builder()
                    .baseUrl(baseUrl)
                    .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                            .secure(ssl -> ssl.sslContext(sslContext))))
                    .build();
        } catch (Exception e) {
            throw new IllegalStateException("Error creando WebClient para " + baseUrl, e);
        }
    }
}
