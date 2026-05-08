package handler;

import io.javalin.http.Context;
import model.UserData;
import model.RegisterResult;
import service.UserService;

public class UserHandler {

    private final UserService userService = new UserService();

    public void register(Context ctx) {
        try {
            UserData request = ctx.bodyAsClass(UserData.class);
            RegisterResult result = userService.register(request);
            ctx.status(200).json(result);
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(new ErrorResponse("Error: bad request"));
        } catch (Exception e) {
            ctx.status(500).json(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    private record ErrorResponse(String message) {}
}
