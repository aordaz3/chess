package handler;

import io.javalin.http.Context;
import model.*;
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
            if ("unauthorized".equals(e.getMessage())) {
                ctx.status(403).json(new ErrorResponse("Error: already taken"));
            }
            else {
                ctx.status(400).json(new ErrorResponse("Error: bad request"));
            }
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
        catch (IllegalArgumentException f) {
            if ("unauthorized".equals(f.getMessage())) {
                ctx.status(401).json(new ErrorResponse("Error: unauthorized"));
            }
            else {
                ctx.status(400).json(new ErrorResponse("Error: bad request"));
            }
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

    public void listGames(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            Collection<GamesSummary> games = userService.listGames(authToken);
            ctx.status(200).json(new ListGamesResponse(games));
        }
        catch (IllegalArgumentException e) {
            ctx.status(401).json(new ErrorResponse("Error: unauthorized"));
        }
        catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    public void createGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            CreateGameRequest request = ctx.bodyAsClass(CreateGameRequest.class);
            CreateGameResult result = userService.createGame(authToken, request);
            ctx.status(200).json(result);
        }
        catch (IllegalArgumentException e) {
            if ("unauthorized".equals(e.getMessage())) {
                ctx.status(401).json(new ErrorResponse("Error: unauthorized"));
            }
            else {
                ctx.status(400).json(new ErrorResponse("Error: bad request"));
            }
        }
        catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    public void joinGame(Context ctx){
        try{
            String authToken = ctx.header("authorization");
            JoinGameRequest request = ctx.bodyAsClass(JoinGameRequest.class);
            userService.joinGame(authToken, request);
            ctx.status(200).result("{}");
        }
        catch (IllegalArgumentException e) {
            if ("unauthorized".equals(e.getMessage())) {
                ctx.status(401).json(new ErrorResponse("Error: unauthorized"));
            }
            else if ("already taken".equals(e.getMessage())) {
                ctx.status(403).json(new ErrorResponse("Error: already taken"));
            }
            else {
                ctx.status(400).json(new ErrorResponse("Error: bad request"));
            }
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
