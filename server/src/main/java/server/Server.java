package server;

import chess.ChessGame;
import io.javalin.*;

import javax.naming.Context;
import java.util.zip.CheckedInputStream;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        //javalin.post("/user", context -> register(context));

    }


    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
