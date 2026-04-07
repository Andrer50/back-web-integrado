package com.utp.backwebintegrado.client;

import com.utp.backwebintegrado.dto.request.AuthRegisterRequest;
import com.utp.backwebintegrado.utility.WebClientUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.JsonNode;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthClient {
    private final WebClient authWebClient;
    private final WebClientUtils webClientUtils;

    public JsonNode register(AuthRegisterRequest request) {
        return authWebClient.post()
                .uri("/auth/register")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, webClientUtils::handleError)
                .bodyToMono(JsonNode.class)
                .block();
    }
}
