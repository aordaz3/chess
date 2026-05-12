package server;

import io.javalin.Javalin;
import handler.UserHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.json.JsonMapper;

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

        javalin.post("/user", ctx -> userHandler.register(ctx));
        javalin.post("/session", ctx -> userHandler.login(ctx));
        javalin.delete("/session", ctx -> userHandler.logout(ctx));
        javalin.get("/game", ctx -> userHandler.listGames(ctx));
        javalin.post("/game", ctx -> userHandler.createGame(ctx));
        javalin.put("/game", ctx -> userHandler.joinGame(ctx));
        javalin.delete("/db", ctx -> userHandler.clear(ctx));

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