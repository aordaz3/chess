package client;

import chess.ChessMove;
import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.Objects;
import java.util.concurrent.CompletionStage;

public class WebSocketFacade {


    private final String websocketUrl;

    public WebSocketFacade(String websocketUrl) {
        this.websocketUrl = websocketUrl;
    }

    public void connect(String authToken, Integer gameID) throws Exception {

    }

    public void makeMove(String authToken, Integer gameID, ChessMove move) {
    }

    public void leave(String authToken, Integer gameID) {
    }

    public void resign(String authToken, Integer gameID) {

    }

    public void close() {
    }

}
