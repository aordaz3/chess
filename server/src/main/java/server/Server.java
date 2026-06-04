package server;

import io.javalin.Javalin;
import handler.UserHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.json.JsonMapper;
import dataaccess.DataAccessException;
import handler.WebSocketHandler;

import java.lang.reflect.Type;

public class Server {

    private final Javalin javalin;
    private final UserHandler userHandler = new UserHandler();

    public Server() {

        Gson gson = new GsonBuilder().create();
        JsonMapper gsonMapper = new JsonMapper() {
            @Override
            public String toJsonString(Object obj, Type type) {
                return gson.toJson(obj, type);
            }

            @Override
            public <T> T fromJsonString(String json, Type targetType) {
                return gson.fromJson(json, targetType);
            }
        };

        javalin = Javalin.create(config -> {
            config.jsonMapper(gsonMapper);
            config.staticFiles.add("web");
        });

        // Routing Configuration
        javalin.post("/user", ctx -> userHandler.register(ctx));
        javalin.post("/session", ctx -> userHandler.login(ctx));
        javalin.delete("/session", ctx -> userHandler.logout(ctx));
        javalin.get("/game", ctx -> userHandler.listGames(ctx));
        javalin.post("/game", ctx -> userHandler.createGame(ctx));
        javalin.put("/game", ctx -> userHandler.joinGame(ctx));
        javalin.delete("/db", ctx -> userHandler.clear(ctx));

        //websoscket
        WebSocketHandler wsHandler = new WebSocketHandler();
        javalin.ws("/ws", ws -> {
            ws.onConnect(wsHandler);
            ws.onMessage(wsHandler);
            ws.onClose(wsHandler);
            ws.onError(wsHandler);
        });

        // Unified Exception Mapper for DataAccessException
        javalin.exception(DataAccessException.class, (e, ctx) -> {
            String msg = e.getMessage();
            if (msg == null) {
                ctx.status(500);
                ctx.json(new ErrorResponse("Error: Internal Server Failure"));
                return;
            }

            switch (msg.toLowerCase()) {
                case "bad request" -> {
                    ctx.status(400);
                    ctx.json(new ErrorResponse("Error: bad request"));
                }
                case "unauthorized" -> {
                    ctx.status(401);
                    ctx.json(new ErrorResponse("Error: unauthorized"));
                }
                case "already taken" -> {
                    ctx.status(403);
                    ctx.json(new ErrorResponse("Error: already taken"));
                }
                default -> {
                    ctx.status(500);
                    ctx.json(new ErrorResponse("Error: " + msg));
                }
            }
        });
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
