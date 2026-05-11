package service;

import model.*;

import java.util.ArrayList;
import java.util.Collection;

public class UserService {

    public RegisterResult register(RegisterRequest request) {
        // validate input, check duplicates, create user, generate token
        return new RegisterResult(request.username(), "authtoken");
    }

    public LoginResult login(LoginRequest request) {
        //ADD LOGIC
        return new LoginResult(request.username(), request.password());
    }

    public void logout(String authToken) {
        if (authToken == null) {
            throw new IllegalArgumentException("unauthorized");
        }
        // remove token from auth DAO
    }

    public Collection<GameData> listGames(String authToken){
        //ADD LOGIC
        return new ArrayList<>();
    }

    public CreateGameResult createGame(CreateGameRequest request){
        //ADD LOGIC
        return new CreateGameResult("000");
    }

    public void joinGame(JoinGameRequest request){
        //ADD LOGIC
        return;
    }

}