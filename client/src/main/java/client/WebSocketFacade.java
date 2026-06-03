package client;

import chess.ChessMove;
import com.google.gson.Gson;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.Objects;
import java.util.concurrent.CompletionStage;

public class WebSocketFacade {


    private final String websocketUrl;
    private final Gson gson = new Gson();
    private WebSocket webSocket;

    public WebSocketFacade(String websocketUrl) {
        this.websocketUrl = websocketUrl;
    }

    public void connect(String authToken, Integer gameID) throws Exception {

    }

    public void makeMove(String authToken, Integer gameID, ChessMove move) {
        MakeMoveCommand command = new MakeMoveCommand(authToken, gameID, move);
        sendRaw(command);
    }

    public void leave(String authToken, Integer gameID) {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
        sendRaw(command);
        close();
    }

    public void resign(String authToken, Integer gameID) {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
        sendRaw(command);
    }

    public void close() {
        try {
            if (webSocket != null) {
                webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "bye").join();
            }
        }
        catch (Exception e) {
            System.out.println("couldnt leave");
        }

    }

    private void sendRaw(Object command) {
        if (webSocket == null) {
            throw new IllegalStateException("WebSocket is not connected.");
        }
        String json = gson.toJson(command);
        webSocket.sendText(json, true).join();
    }

}
