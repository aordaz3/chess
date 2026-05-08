package server;

import io.javalin.Javalin;
import handler.UserHandler;

public class Server {

    private final Javalin javalin;
    private final UserHandler userHandler = new UserHandler();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        javalin.post("/user", ctx -> userHandler.register(ctx));
    }


    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private record ErrorResponse(String message) {}
}