package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.*;

import java.util.ArrayList;
import java.util.Collection;

public class UserService {
    private final UserDAO userDAO = new UserDAO();
    private final AuthDAO authDAO = new AuthDAO();
    private final GameDAO gameDAO = new GameDAO();
    public RegisterResult register(RegisterRequest request) {
        if (request.username() == null || request.password() == null || request.email() == null)
            throw new IllegalArgumentException("bad request");

        model.UserData user = userDAO.getUser(request.username());

        if(user != null)
            throw new IllegalArgumentException("unauthorized");
        else{
            UserData newUser = new UserData(request.username(), request.password(), request.email());
            userDAO.createUser(newUser);
        }
        String authToken = java.util.UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, request.username());
        authDAO.createAuth(authData);

        return new RegisterResult(request.username(), authToken);
    }

    public LoginResult login(LoginRequest request) {
        if (request.username() == null || request.password() == null)
            throw new IllegalArgumentException("bad request");

        UserData user = userDAO.getUser(request.username());

        if(user == null || !user.password().equals(request.password()))
            throw new IllegalArgumentException("unauthorized");

        String authToken = java.util.UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, request.username());
        authDAO.createAuth(authData);

        return new LoginResult(request.username(), authToken);
    }

    public void logout(String authToken) {
        if (authToken == null) {
            throw new IllegalArgumentException("unauthorized");
        }
        AuthData userAuthData = authDAO.getAuth(authToken);
        if(userAuthData == null){
            throw new IllegalArgumentException("unauthorized");
        }
        authDAO.deleteAuth(authToken);
    }

    public Collection<GameData> listGames(String authToken){
        //ADD LOGIC
        if(authToken == null){
            throw new IllegalArgumentException("unauthorized");
        }
        AuthData userAuthData = authDAO.getAuth(authToken);
        if(userAuthData == null){
            throw new IllegalArgumentException("unauthorized");
        }

        return gameDAO.listGames();
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