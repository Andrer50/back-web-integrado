package com.utp.backwebintegrado.shared.client;

import com.utp.backwebintegrado.user.application.dto.AuthRegisterRequest;
import com.utp.backwebintegrado.shared.utility.WebClientUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthClient {
    private final WebClient authWebClient;
    private final WebClientUtils webClientUtils;

    public void register(AuthRegisterRequest request) {
        authWebClient.post()
                .uri("/auth/register")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, webClientUtils::handleError)
                .bodyToMono(Void.class)
                .block();
    }
}
