package service;

import chess.ChessGame;
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
    private int nextGameID = 1;
    public RegisterResult register(RegisterRequest request) {
        if (request.username() == null || request.password() == null || request.email() == null) {
            throw new IllegalArgumentException("bad request");
        }

        model.UserData user = userDAO.getUser(request.username());

        if(user != null) {
            throw new IllegalArgumentException("unauthorized");
        }
        else{
            if(request.username().isEmpty() || request.password().isEmpty() || request.email().isEmpty()){
                throw new IllegalArgumentException("bad request");
            }
            UserData newUser = new UserData(request.username(), request.password(), request.email());
            userDAO.createUser(newUser);
        }
        String authToken = java.util.UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, request.username());
        authDAO.createAuth(authData);

        return new RegisterResult(request.username(), authToken);
    }

    public LoginResult login(LoginRequest request) {
        if (request.username() == null || request.password() == null) {
            throw new IllegalArgumentException("bad request");
        }

        UserData user = userDAO.getUser(request.username());

        if(user == null || !user.password().equals(request.password())) {
            throw new IllegalArgumentException("unauthorized");
        }

        String authToken = java.util.UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, request.username());
        authDAO.createAuth(authData);

        return new LoginResult(request.username(), authToken);
    }

    public void logout(String authToken) {
        if (authToken == null) {
            throw new IllegalArgumentException("bad request");
        }
        AuthData userAuthData = authDAO.getAuth(authToken);
        if(userAuthData == null){
            throw new IllegalArgumentException("unauthorized");
        }
        authDAO.deleteAuth(authToken);
    }

    public Collection<GamesSummary> listGames(String authToken){
        //ADD LOGIC
        if(authToken == null){
            throw new IllegalArgumentException("unauthorized");
        }
        AuthData userAuthData = authDAO.getAuth(authToken);
        if(userAuthData == null){
            throw new IllegalArgumentException("unauthorized");
        }
        Collection<GameData> unfiltered = gameDAO.listGames();
        Collection<GamesSummary> filtered = new ArrayList<>();
        for(GameData gameInfo : unfiltered){
            filtered.add(new GamesSummary(gameInfo.gameID(), gameInfo.gameName(),gameInfo.whiteUsername(),gameInfo.blackUsername()));
        }
        return filtered;
    }

    public CreateGameResult createGame(String authToken, CreateGameRequest request){
        //ADD LOGIC
        if(authToken == null || request == null || request.gameName() == null){
            throw new IllegalArgumentException("bad request");
        }

        AuthData userAuthData = authDAO.getAuth(authToken);
        if(userAuthData == null){
            throw new IllegalArgumentException("unauthorized");
        }
        if(request.gameName().isEmpty()){
            throw new IllegalArgumentException("bad request");
        }
        int gameID = nextGameID++;
        ChessGame game = new ChessGame();
        GameData gameInfo = new GameData(gameID, null, null, request.gameName(), game);
        gameDAO.createGame(gameID, gameInfo);
        return new CreateGameResult(gameID);
    }

    public void joinGame(String authToken, JoinGameRequest request){
        //ADD LOGIC
        if(authToken == null || request == null || request.gameID() < 0 || request.playerColor() == null){
            throw new IllegalArgumentException("bad request");
        }

        AuthData userAuthData = authDAO.getAuth(authToken);
        if(userAuthData == null){
            throw new IllegalArgumentException("unauthorized");
        }
        GameData targetGame = gameDAO.getGame(request.gameID());
        //check to see if we can join game
        if(targetGame == null || targetGame.game() == null || (targetGame.blackUsername() != null && targetGame.whiteUsername() != null)){
            throw new IllegalArgumentException("bad request");
        }
        if (request.playerColor().equals("WHITE") || request.playerColor().equals("WHITE/BLACK")) {
            if (targetGame.whiteUsername() != null) {
                throw new IllegalArgumentException("already taken");
            }
            GameData updateWhite = new GameData(request.gameID(), userAuthData.username(), targetGame.blackUsername(),
                                                targetGame.gameName(),targetGame.game());
            gameDAO.updateGame(updateWhite);
            return;
        }
        if (request.playerColor().equals("BLACK")) {
            if (targetGame.blackUsername() != null) {
                throw new IllegalArgumentException("already taken");
            }
            GameData updatedBlack = new GameData(request.gameID(), targetGame.whiteUsername(), userAuthData.username(),
                                                targetGame.gameName(), targetGame.game());
            gameDAO.updateGame(updatedBlack);
            return;
        }
        throw new IllegalArgumentException("bad request");
    }
    public void clear(){
        //DELETE EVERYTHING IN THE DB
        gameDAO.clear();
        authDAO.clear();
        userDAO.clear();
        nextGameID = 1;
    }

}