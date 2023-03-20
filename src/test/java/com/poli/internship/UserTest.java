package com.poli.internship;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.poli.internship.api.context.JWTService;
import com.poli.internship.data.entity.UserEntity;
import com.poli.internship.data.http.GoogleOAuthClient;
import com.poli.internship.data.repository.UserRepository;
import com.poli.internship.domain.models.AuthTokenPayload;
import com.poli.internship.domain.models.GoogleOAuthModel;
import com.poli.internship.domain.models.LoginModel;
import com.poli.internship.domain.models.UserModel;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.context.ActiveProfiles;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@AutoConfigureHttpGraphQlTester
@ActiveProfiles("test")
public class UserTest {
    @Autowired
    private HttpGraphQlTester tester;
    @Autowired
    private UserRepository repository;
    @Autowired
    private JWTService jwtService;
    @MockBean
    private GoogleOAuthClient mockGoogleOAuthClient;


    @BeforeEach
    public void beforeEach(){
        this.repository.deleteAll();
    }

    @Test
    public void getUser() {
        UserEntity userEntity = this.repository.save(new UserEntity("Paulo", "paulo@teste.com"));
        AuthTokenPayload tokenPayload = new AuthTokenPayload(userEntity.getId().toString(), userEntity.getEmail(), 3600);
        String authToken = this.jwtService.createAuthorizationToken(tokenPayload);

        HttpGraphQlTester testerWithAuth = this.tester.mutate().header("Authorization", authToken).build();
        UserModel user = testerWithAuth.documentName("getUser")
                .execute()
                .path("getUser")
                .entity(UserModel.class)
                .get();

        assertThat(user.getId()).isEqualTo(userEntity.getId().toString());
        assertThat(user.getName()).isEqualTo(userEntity.getName());
        assertThat(user.getEmail()).isEqualTo(userEntity.getEmail());
        assertThat(user).hasOnlyFields("id", "name", "email");
    }

    @Test
    public void getUserWhenNoAuth() {
        this.tester.documentName("getUser")
                .execute()
                .errors()
                .expect(error -> error.getErrorType() == ErrorType.UNAUTHORIZED)
                .expect(error -> error.getMessage().contentEquals("Missing or invalid authorization token."));
    }

    @Test public void loginWhenUserExists() {
        UserEntity userEntity = this.repository.save(new UserEntity("Paulo", "paulo@teste.com"));
        String code = "my-oauth-code-1234";
        String userIdTokenInfo = "{\"name\": \"Paulo\", \"email\": \"paulo@teste.com\"}";
        String idToken = "firstPart." + Base64.getEncoder().encodeToString(userIdTokenInfo.getBytes(StandardCharsets.UTF_8)) + ".thirdPart";
        GoogleOAuthModel mockGoogleOAuthModel = new GoogleOAuthModel();
        mockGoogleOAuthModel.setAccessToken("access-token");
        mockGoogleOAuthModel.setIdToken(idToken);
        mockGoogleOAuthModel.setExpiresIn(3600);
        Mockito.when(mockGoogleOAuthClient.authenticateUser(code)).thenReturn(mockGoogleOAuthModel);
        Map<String, Object> input = new HashMap<String, Object>();
        input.put("code", code);

        LoginModel login = this.tester.documentName("login")
                .variable("input", input)
                .execute()
                .path("login")
                .entity(LoginModel.class)
                .get();

        DecodedJWT jwt = this.jwtService.decodeAuthorizationToken(login.getToken());
        assertThat(jwt.getClaim("userId").asString()).isEqualTo(userEntity.getId().toString());
        assertThat(jwt.getClaim("email").asString()).isEqualTo(userEntity.getEmail());
        assertThat(jwt.getExpiresAtAsInstant()).isCloseTo(Instant.now().plusSeconds(3600), within(5, ChronoUnit.SECONDS));
    }

    @Test public void loginWhenUserDoesntExist() {
        String code = "my-oauth-code-1234";
        String userIdTokenInfo = "{\"name\": \"Paulo\", \"email\": \"paulo@teste.com\"}";
        String idToken = "firstPart." + Base64.getEncoder().encodeToString(userIdTokenInfo.getBytes(StandardCharsets.UTF_8)) + ".thirdPart";
        GoogleOAuthModel mockGoogleOAuthModel = new GoogleOAuthModel();
        mockGoogleOAuthModel.setAccessToken("access-token");
        mockGoogleOAuthModel.setIdToken(idToken);
        mockGoogleOAuthModel.setExpiresIn(3600);
        Mockito.when(mockGoogleOAuthClient.authenticateUser(code)).thenReturn(mockGoogleOAuthModel);
        Map<String, Object> input = new HashMap<String, Object>();
        input.put("code", code);

        LoginModel login = this.tester.documentName("login")
                .variable("input", input)
                .execute()
                .path("login")
                .entity(LoginModel.class)
                .get();

        UserEntity user = this.repository.findAll().iterator().next();
        DecodedJWT jwt = this.jwtService.decodeAuthorizationToken(login.getToken());
        assertThat(jwt.getClaim("userId").asString()).isEqualTo(user.getId().toString());
        assertThat(jwt.getClaim("email").asString()).isEqualTo(user.getEmail());
        assertThat(jwt.getExpiresAtAsInstant()).isCloseTo(Instant.now().plusSeconds(3600), within(5, ChronoUnit.SECONDS));
    }
}
