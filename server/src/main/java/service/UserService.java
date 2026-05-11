package service;

import dataaccess.UserDAO;
import model.*;

import java.util.ArrayList;
import java.util.Collection;

public class UserService {
    private final UserDAO DAO = new UserDAO();

    public RegisterResult register(RegisterRequest request) {

        model.UserData user = DAO.getUser(request.username());
        if(user != null)
            throw new IllegalArgumentException("already taken");
        else{
            UserData newUser = new UserData(request.username(), request.password(), request.email());
            DAO.createUser(newUser);
        }
        String authToken = java.util.UUID.randomUUID().toString();
        return new RegisterResult(request.username(), authToken);
    }

    public LoginResult login(LoginRequest request) {

        return new LoginResult(request.username(), "authtoken");
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
    public void clear(){
        //DELETE EVERYTHING IN THE DB
    }

}