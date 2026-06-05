package handler;

import io.javalin.http.Context;
import model.*;
import service.UserService;
import dataaccess.DataAccessException;

import java.util.Collection;

public class UserHandler {

    private final UserService userService = new UserService();

    public void register(Context ctx) throws DataAccessException {
        RegisterRequest request = ctx.bodyAsClass(RegisterRequest.class);
        RegisterResult result = userService.register(request);
        ctx.status(200).json(result);
    }

    public void login(Context ctx) throws DataAccessException {
        LoginRequest request = ctx.bodyAsClass(LoginRequest.class);
        LoginResult result = userService.login(request);
        ctx.status(200).json(result);
    }

    public void logout(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");
        userService.logout(authToken);
        ctx.status(200).result("{}");
    }

    public void listGames(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");
        Collection<GamesSummary> games = userService.listGames(authToken);
        ctx.status(200).json(new ListGamesResponse(games));
    }

    public void createGame(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");
        CreateGameRequest request = ctx.bodyAsClass(CreateGameRequest.class);
        CreateGameResult result = userService.createGame(authToken, request);
        ctx.status(200).json(result);

    }

    public void joinGame(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");
        JoinGameRequest request = ctx.bodyAsClass(JoinGameRequest.class);
        GameData game = userService.joinGame(authToken, request);
        ctx.status(200).json(game);
    }

    public void clear(Context ctx) throws DataAccessException {
        userService.clear();
        ctx.status(200).result("{}");
    }
}
