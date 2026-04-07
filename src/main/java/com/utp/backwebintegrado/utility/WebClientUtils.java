package com.utp.backwebintegrado.utility;

import com.utp.backwebintegrado.exception.ApiValidateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebClientUtils {

    private final ObjectMapper objectMapper;

    public Mono<? extends Throwable> handleError(ClientResponse response) {
        return response.bodyToMono(String.class)
                .defaultIfEmpty("Sin detalle")
                .flatMap(errorBody -> {
                    String message = errorBody;
                    try {
                        JsonNode json = objectMapper.readTree(errorBody);
                        if (json.has("message")) {
                            message = json.get("message").asText();
                        }
                    } catch (Exception e) {
                        log.warn("No se pudo parsear errorBody como JSON: {}", errorBody);
                    }
                    log.error("WebClient error: {} - {}", response.statusCode(), message);
                    return Mono.error(new ApiValidateException(message));
                });
    }
}