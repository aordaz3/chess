package service;

import model.UserData;
import model.RegisterResult;

public class UserService {

    public RegisterResult register(UserData request) {
        // validate input, check duplicates, create user, generate token
        return new RegisterResult(request.username(), "some-auth-token");
    }
}