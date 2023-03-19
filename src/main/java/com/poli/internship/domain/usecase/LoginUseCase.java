package com.poli.internship.domain.usecase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poli.internship.api.context.JWTService;
import com.poli.internship.api.error.CustomError;
import com.poli.internship.data.datasource.UserDataSource;
import com.poli.internship.data.http.GoogleOAuthClient;
import com.poli.internship.domain.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.HashMap;

@Service
public class LoginUseCase {
    @Autowired
    private GoogleOAuthClient oauthClient;
    @Autowired
    private UserDataSource userDataSource;
    @Autowired
    private JWTService jwtService;

    public LoginModel exec(String code, UserType userType){
        GoogleOAuthModel loginInfo = this.oauthClient.authenticateUser(code);
        try {
            String oauthIdToken = loginInfo.getIdToken();
            String userInfoDecoded = new String(Base64.getDecoder().decode(oauthIdToken.split("\\.")[1]));
            HashMap<String, Object> userInfo = new ObjectMapper().readValue(userInfoDecoded, HashMap.class);
            UserModel.User user = this.userDataSource.getUserByEmail((String) userInfo.get("email"));

            if (user == null) {
                user = this.userDataSource.createUser(
                        (String) userInfo.get("name"),
                        (String) userInfo.get("email"),
                        userType
                );
            }

            if(user.userType() != userType) {
                throw new CustomError("Invalid user type.", ErrorType.BAD_REQUEST);
            }

            AuthTokenPayloadModel.AuthTokenPayload authTokenPayload = new AuthTokenPayloadModel.AuthTokenPayload(
                    user.id(),
                    (String) userInfo.get("email"),
                    user.userType(),
                    loginInfo.getExpiresIn());
            String token = this.jwtService.createAuthorizationToken(authTokenPayload);

            LoginModel loginModel = new LoginModel();
            loginModel.setToken(token);
            return loginModel;
        } catch (CustomError error) {
            throw error;
        } catch (Exception exception){
            throw new CustomError("Couldn't generate JWT token", ErrorType.INTERNAL_ERROR);
        }
    }

}
