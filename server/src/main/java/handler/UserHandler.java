package handler;

import io.javalin.http.Context;
import model.*;import org.jetbrains.annotations.NotNull;
import service.UserService;

import java.util.Collection;

public class UserHandler {

    private final UserService userService = new UserService();

    public void register(Context ctx) {
        //pass over USERDATA: {username, password, email}
        try {
            RegisterRequest request = ctx.bodyAsClass(RegisterRequest.class);
            RegisterResult result = userService.register(request);
            ctx.status(200).json(result);
        }
        catch (IllegalArgumentException e) {
            ctx.status(400).json(new ErrorResponse("Error: bad request"));
        }
        catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    public void login(Context ctx) {
        //passover LoginRequest{username, password}
        try{
            LoginRequest request = ctx.bodyAsClass(LoginRequest.class);
            LoginResult result = userService.login(request);
            ctx.status(200).json(result);
        }
        catch (IllegalArgumentException e){
            ctx.status(400).json(new ErrorResponse("Error: bad request"));
        }
        catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    public void logout(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            userService.logout(authToken);
            ctx.status(200).result("{}");
        }
        catch (IllegalArgumentException e) {
            ctx.status(401).json(new ErrorResponse("Error: unauthorized"));
        }
        catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    public void listGames(Context ctx){
        try{
            String authToken = ctx.header("authorization");
            Collection<GameData> games = userService.listGames(authToken);
            ctx.status(200).json(new ListGamesResult(games));
        }
        catch (IllegalArgumentException e) {
            ctx.status(401).json(new ErrorResponse("Error: unauthorized"));
        }
        catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    public void createGame(Context ctx){
        try{
            CreateGameRequest request = ctx.bodyAsClass(CreateGameRequest.class);
            CreateGameResult result = userService.createGame(request);
            ctx.status(200).json(result);
        }
        catch (IllegalArgumentException e) {
            ctx.status(401).json(new ErrorResponse("Error: unauthorized"));
        }
        catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    public void joinGame(Context ctx){
        try{
            JoinGameRequest request = ctx.bodyAsClass(JoinGameRequest.class);
            userService.joinGame(request);
            ctx.status(200).result("{}");
        }
        catch (IllegalArgumentException e) {
            ctx.status(401).json(new ErrorResponse("Error: unauthorized"));
        }
        catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    public void clear(Context ctx){
        try {
            userService.clear();
            ctx.status(200).result("{}");
        }
        catch (Exception e){
            ctx.status(500).json(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    private record ErrorResponse(String message) {}
}
