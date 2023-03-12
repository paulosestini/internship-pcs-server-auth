package com.poli.internship.data.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poli.internship.api.error.CustomError;
import com.poli.internship.domain.models.GoogleOAuthModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GoogleOAuthClient {
    private HttpClient httpClient;

    @Value("${oauth.url}")
    private String googleUrl;
    @Value("${oauth.client-secret}")
    private String clientSecret;
    @Value("${oauth.client-id}")
    private String clientId;
    @Value("${oauth.redirect-uri}")
    private String redirectUri;

    @Autowired
    private Environment env;
    public GoogleOAuthClient() {
        this.httpClient = HttpClient.newHttpClient();
    }
    public GoogleOAuthModel authenticateUser(String code) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("code", code);
        parameters.put("client_id", this.clientId);
        parameters.put("client_secret", this.clientSecret);
        parameters.put("redirect_uri", this.redirectUri);
        parameters.put("grant_type", "authorization_code");

        String form = parameters.entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(this.googleUrl))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();

        try {
            HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            Map map = new ObjectMapper().readValue(response.body(), HashMap.class);

            GoogleOAuthModel loginInfo = new GoogleOAuthModel();
            loginInfo.setAccessToken((String) map.get("access_token"));
            loginInfo.setIdToken((String) map.get("id_token"));
            loginInfo.setExpiresIn((int) map.get("expires_in"));
            return loginInfo;
        } catch (Exception e) {
            throw new CustomError("Could not login at Google OAuth.", ErrorType.INTERNAL_ERROR);
        }
    }


}
