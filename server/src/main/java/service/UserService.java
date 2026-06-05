package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;

import java.util.ArrayList;
import java.util.Collection;

public class UserService {
    private final MySQLAuthDAO authDAO;
    private final MySQLGameDAO gameDAO;
    private final MySQLUserDAO userDAO;

    public UserService() {
        this.authDAO = new MySQLAuthDAO();
        this.gameDAO = new MySQLGameDAO();
        this.userDAO = new MySQLUserDAO();

    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        if (request.username() == null || request.password() == null || request.email() == null ||
                request.username().isEmpty() || request.password().isEmpty() || request.email().isEmpty()) {
            throw new DataAccessException("bad request");
        }

        UserData user = userDAO.getUser(request.username());
        if (user != null) {
            throw new DataAccessException("already taken");
        }

        UserData newUser = new UserData(request.username(), request.password(), request.email());
        userDAO.createUser(newUser);

        String authToken = java.util.UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, request.username());
        authDAO.createAuth(authData);

        return new RegisterResult(request.username(), authToken);
    }

    public LoginResult login(LoginRequest request) throws DataAccessException {
        if (request.username() == null || request.password() == null || request.username().isEmpty() || request.password().isEmpty()) {
            throw new DataAccessException("bad request");
        }

        UserData user = userDAO.getUser(request.username());

        if (user == null || !org.mindrot.jbcrypt.BCrypt.checkpw(request.password(), user.password())) {
            throw new DataAccessException("unauthorized");
        }

        String authToken = java.util.UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, request.username());
        authDAO.createAuth(authData);

        return new LoginResult(request.username(), authToken);
    }


    public void logout(String authToken) throws DataAccessException {
        if (authToken == null || authToken.isEmpty()) {
            throw new DataAccessException("bad request");
        }
        AuthData userAuthData = authDAO.getAuth(authToken);
        if (userAuthData == null) {
            throw new DataAccessException("unauthorized");
        }
        authDAO.deleteAuth(authToken);
    }

    public Collection<GamesSummary> listGames(String authToken) throws DataAccessException {
        if (authToken == null || authToken.isEmpty()) {
            throw new DataAccessException("unauthorized");
        }
        AuthData userAuthData = authDAO.getAuth(authToken);
        if (userAuthData == null) {
            throw new DataAccessException("unauthorized");
        }

        Collection<GameData> unfiltered = gameDAO.listGames();
        Collection<GamesSummary> filtered = new ArrayList<>();
        for (GameData gameInfo : unfiltered) {
            filtered.add(new GamesSummary(gameInfo.gameID(), gameInfo.gameName(), gameInfo.whiteUsername(), gameInfo.blackUsername()));
        }
        return filtered;
    }

    public CreateGameResult createGame(String authToken, CreateGameRequest request) throws DataAccessException {
        if (authToken == null || request == null || request.gameName() == null || request.gameName().isEmpty()) {
            throw new DataAccessException("bad request");
        }

        AuthData userAuthData = authDAO.getAuth(authToken);
        if (userAuthData == null) {
            throw new DataAccessException("unauthorized");
        }

        int gameID = Math.abs((request.gameName() + System.currentTimeMillis()).hashCode());
        ChessGame game = new ChessGame();

        GameData gameInfo = new GameData(gameID, null, null, request.gameName(), game);
        gameDAO.createGame(gameID, gameInfo);
        return new CreateGameResult(gameID);
    }

    public GameData joinGame(String authToken, JoinGameRequest request) throws DataAccessException {
        if (authToken == null || request == null || request.gameID() <= 0 || request.playerColor() == null) {
            throw new DataAccessException("bad request");
        }

        AuthData userAuthData = authDAO.getAuth(authToken);
        if (userAuthData == null) {
            throw new DataAccessException("unauthorized");
        }

        GameData targetGame = gameDAO.getGame(request.gameID());
        if (targetGame == null) {
            throw new DataAccessException("bad request");
        }

        String color = request.playerColor().toUpperCase();
        GameData updatedGame;

        if (color.equals("WHITE")) {
            if (targetGame.whiteUsername() != null) {
                throw new DataAccessException("already taken");
            }
            updatedGame = new GameData(
                    request.gameID(),
                    userAuthData.username(),
                    targetGame.blackUsername(),
                    targetGame.gameName(),
                    targetGame.game()
            );
        } else if (color.equals("BLACK")) {
            if (targetGame.blackUsername() != null) {
                throw new DataAccessException("already taken");
            }
            updatedGame = new GameData(
                    request.gameID(),
                    targetGame.whiteUsername(),
                    userAuthData.username(),
                    targetGame.gameName(),
                    targetGame.game()
            );
        } else {
            throw new DataAccessException("bad request");
        }

        gameDAO.updateGame(updatedGame);
        return updatedGame;
    }

    public void clear() throws DataAccessException {
        gameDAO.clear();
        authDAO.clear();
        userDAO.clear();
    }
}
