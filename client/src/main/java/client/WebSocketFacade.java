package client;

import com.google.gson.Gson;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.PongMessage;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WebSocketFacade extends Endpoint {

    public interface GameMessageHandler {
        void onLoadGame(LoadGameMessage message);
        void onNotification(NotificationMessage message);
        void onError(ErrorMessage message);
        void onClose();
    }

    private final Gson gson = new Gson();
    private Session session;
    private final String serverUrl;
    private GameMessageHandler handler;

    private String pendingAuthToken;
    private Integer pendingGameID;

    private ScheduledExecutorService keepAlive;

    public WebSocketFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public void connect(String authToken, Integer gameID, GameMessageHandler handler) throws Exception {
        this.handler = handler;
        this.pendingAuthToken = authToken;
        this.pendingGameID = gameID;

        URI uri = new URI(serverUrl.replace("http", "ws") + "/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.setDefaultMaxSessionIdleTimeout(0);
        container.connectToServer(this, uri);
    }

    public void makeMove(String authToken, Integer gameID, chess.ChessMove move) throws IOException {
        MakeMoveCommand command = new MakeMoveCommand(authToken, gameID, move);
        send(command);
    }

    public void leave(String authToken, Integer gameID) throws IOException {
        UserGameCommand command = new UserGameCommand(
                UserGameCommand.CommandType.LEAVE,
                authToken,
                gameID
        );
        send(command);
        close();
    }

    public void resign(String authToken, Integer gameID) throws IOException {
        UserGameCommand command = new UserGameCommand(
                UserGameCommand.CommandType.RESIGN,
                authToken,
                gameID
        );
        send(command);
    }

    private void send(Object command) throws IOException {
        if (session == null || !session.isOpen()) {
            throw new IllegalStateException("WebSocket is not connected.");
        }
        session.getBasicRemote().sendText(gson.toJson(command));
    }

    private void handleMessage(String message) {
        System.out.println("CLIENT RAW WS: " + message);
        try {
            ServerMessage base = gson.fromJson(message, ServerMessage.class);
            System.out.println("CLIENT TYPE: " + (base == null ? "null" : base.getServerMessageType()));

            if (base == null || base.getServerMessageType() == null) {
                return;
            }

            switch (base.getServerMessageType()) {
                case LOAD_GAME -> {
                    System.out.println("CLIENT LOAD_GAME RECEIVED");
                    LoadGameMessage load = gson.fromJson(message, LoadGameMessage.class);
                    if (handler != null) {handler.onLoadGame(load);}
                }
                case NOTIFICATION -> {
                    NotificationMessage note = gson.fromJson(message, NotificationMessage.class);
                    if (handler != null) {handler.onNotification(note);}
                }
                case ERROR -> {
                    ErrorMessage err = gson.fromJson(message, ErrorMessage.class);
                    if (handler != null) {handler.onError(err);}
                }
            }
        } catch (Exception e) {
            System.out.println("Could not parse websocket message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (keepAlive != null) {
                keepAlive.shutdownNow();
                keepAlive = null;
            }
            if (session != null && session.isOpen()) {
                session.close();
            }
        } catch (Exception e) {
            System.out.println("Could not close websocket.");
        } finally {
            session = null;
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.out.println("CLIENT MESSAGE HANDLER ADDED");
        this.session = session;
        session.setMaxIdleTimeout(0);

        session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                handleMessage(message);
            }
        });

        session.addMessageHandler(PongMessage.class, pong -> {});

        keepAlive = Executors.newSingleThreadScheduledExecutor();
        keepAlive.scheduleAtFixedRate(() -> {
            try {
                if (this.session != null && this.session.isOpen()) {
                    this.session.getBasicRemote().sendPing(ByteBuffer.allocate(0));
                }
            } catch (Exception e) {
                System.out.println("CLIENT keepalive failed: " + e.getMessage());
            }
        }, 10, 10, TimeUnit.SECONDS);

        new Thread(() -> {
            try {
                UserGameCommand connect = new UserGameCommand(
                        UserGameCommand.CommandType.CONNECT,
                        pendingAuthToken,
                        pendingGameID
                );
                send(connect);
            } catch (Exception e) {
                System.out.println("CLIENT failed to send CONNECT: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onClose(Session session, jakarta.websocket.CloseReason closeReason) {
        if (keepAlive != null) {
            keepAlive.shutdownNow();
            keepAlive = null;
        }
        this.session = null;
        if (handler != null) {
            handler.onClose();
        }
    }

    @Override
    public void onError(Session session, Throwable thr) {
        System.out.println("CLIENT WS ERROR: " + thr.getMessage());
        thr.printStackTrace();
    }
}