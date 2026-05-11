package handler;

import io.javalin.http.Context;
import model.*;import org.jetbrains.annotations.NotNull;
import service.UserService;

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
        try{
            UserData request = ctx.bodyAsClass(UserData.class);
            LogoutResult result = userService.logout(request);
            ctx.status(200).json(result);
        }
        catch (IllegalArgumentException e){
            ctx.status(400).json(new ErrorResponse("Error: bad request"));
        }
        catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    public void listGames(Context ctx) {
        try{
            UserData request = ctx.bodyAsClass(UserData.class);
            LogoutResult result = userService.logout(request);
            ctx.status(200).json(result);
        }
        catch (IllegalArgumentException e){
            ctx.status(400).json(new ErrorResponse("Error: bad request"));
        }
        catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Error: " + e.getMessage()));
        }
    }


    private record ErrorResponse(String message) {}
}
