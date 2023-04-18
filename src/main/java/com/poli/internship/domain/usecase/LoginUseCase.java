package com.poli.internship.domain.usecase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poli.internship.api.context.JWTService;
import com.poli.internship.api.error.CustomError;
import com.poli.internship.data.datasource.UserDataSource;
import com.poli.internship.data.http.GoogleOAuthClient;
import static com.poli.internship.domain.models.CreateUserInputModel.CreateUserInput;
import com.poli.internship.domain.models.GoogleOAuthModel;

import static com.poli.internship.InternshipApplication.LOGGER;
import static com.poli.internship.domain.models.LoginInputModel.LoginInput;
import com.poli.internship.domain.models.LoginModel;
import static com.poli.internship.domain.models.UserModel.User;
import static com.poli.internship.domain.models.AuthTokenPayloadModel.AuthTokenPayload;
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

    public LoginModel exec(LoginInput input){
        GoogleOAuthModel loginInfo = this.oauthClient.authenticateUser(input.code(), input.redirectUri());
        try {
            String oauthIdToken = loginInfo.getIdToken();
            String userInfoDecoded = new String(Base64.getDecoder().decode(oauthIdToken.split("\\.")[1]));
            HashMap<String, Object> userInfo = new ObjectMapper().readValue(userInfoDecoded, HashMap.class);
            User user = this.userDataSource.getUserByEmail((String) userInfo.get("email"));

            if (user == null) {
                CreateUserInput createUserInput = new CreateUserInput(
                        (String) userInfo.get("name"),
                        (String) userInfo.get("email"),
                        input.userType(),
                        (String) userInfo.get("picture")
                );
                user = this.userDataSource.createUser(createUserInput);
            }

            if(user.userType() != input.userType()) {
                throw new CustomError("Invalid user type.", ErrorType.BAD_REQUEST);
            }

            AuthTokenPayload authTokenPayload = new AuthTokenPayload(
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
        } catch (Exception e){
            LOGGER.error(e.getMessage(), e);
            throw new CustomError("Couldn't generate JWT token", ErrorType.INTERNAL_ERROR);
        }
    }

}
