package service;

import model.*;

public class UserService {

    public RegisterResult register(RegisterRequest request) {
        // validate input, check duplicates, create user, generate token
        return new RegisterResult(request.username(), "authtoken");
    }

    public LoginResult login(LoginRequest request) {
        //ADD LOGIC
        return new LoginResult(request.username(), request.password());
    }

    public LogoutResult logout(UserData request) {
        //ADD LOGIC
        return new LogoutResult("authtoken");
    }

}