package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsMessageContext;
import model.*;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GameWebSocketService {
    private final Gson gson = new Gson();
    private final MySQLAuthDAO authDAO = new MySQLAuthDAO();
    private final MySQLGameDAO gameDAO = new MySQLGameDAO();

    private final Map<Integer, Set<Connection>> connectionsByGame = new ConcurrentHashMap<>();
    private final Set<Integer> finishedGames = ConcurrentHashMap.newKeySet();

    public void handleClose(WsCloseContext ctx) {

    }

    private record Connection(String username, WsMessageContext ctx) {}

    public void handleMessage(WsMessageContext ctx) {
        try {
            String json = ctx.message();
            UserGameCommand base = gson.fromJson(json, UserGameCommand.class);

            if (base == null || base.getCommandType() == null) {
                sendError(ctx, "Error: invalid websocket command");
                return;
            }

            switch (base.getCommandType()) {
                case CONNECT -> handleConnect(ctx, base);
                case MAKE_MOVE -> handleMove(ctx, gson.fromJson(json, MakeMoveCommand.class));
                case LEAVE -> handleLeave(ctx, base);
                case RESIGN -> handleResign(ctx, base);
            }
        } catch (DataAccessException e) {
            sendError(ctx, "Error: " + e.getMessage());
        } catch (Exception e) {
            sendError(ctx, "Error: " + e.getMessage());
        }
    }


    private void handleConnect(WsMessageContext ctx, UserGameCommand command) throws DataAccessException {
        AuthData auth = requireAuth(command.getAuthToken());
        GameData gameData = requireGame(command.getGameID());

        addConnection(gameData.gameID(), auth.username(), ctx);

        sendToSender(ctx, new LoadGameMessage(gameData));
        sendToOthers(gameData.gameID(), auth.username(), new NotificationMessage(""));
    }

    private void handleMove(WsMessageContext ctx, MakeMoveCommand command) throws DataAccessException, InvalidMoveException {
        AuthData auth = requireAuth(command.getAuthToken());
        GameData gameData = requireGame(command.getGameID());

        if (finishedGames.contains(gameData.gameID())) {
            throw new DataAccessException("game already over");
        }

        String username = auth.username();
        ChessGame game = gameData.game();
        ChessGame.TeamColor playerColor = getPlayerColor(gameData, username);

        if (playerColor == null) {
            throw new DataAccessException("observer cannot make move");
        }
        if (game.getTeamTurn() != playerColor) {
            throw new DataAccessException("wrong turn");
        }

        ChessMove move = command.getMove();
        if (move == null) {
            throw new DataAccessException("bad request");
        }

        // Make the move
        game.makeMove(move);

        // Save updated game back to DB
        GameData updated = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
        gameDAO.updateGame(updated);

        // If checkmate, mark game over
        ChessGame.TeamColor opponent = (playerColor == ChessGame.TeamColor.WHITE) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;

        if (game.isInCheckmate(opponent)) {
            finishedGames.add(gameData.gameID());
        }

        sendToSender(ctx, new LoadGameMessage(updated));
        sendToOthers(gameData.gameID(), username, new LoadGameMessage(updated));
        sendToOthers(gameData.gameID(), username, new NotificationMessage(username + " made a move."));
    }

    private void handleLeave(WsMessageContext ctx, UserGameCommand command) throws DataAccessException {
        AuthData auth = requireAuth(command.getAuthToken());
        GameData gameData = requireGame(command.getGameID());

        removeConnection(gameData.gameID(), auth.username());
        sendToOthers(gameData.gameID(), auth.username(), new NotificationMessage(auth.username() + " left the game."));
    }

    private void handleResign(WsMessageContext ctx, UserGameCommand command) throws DataAccessException {
        AuthData auth = requireAuth(command.getAuthToken());
        GameData gameData = requireGame(command.getGameID());

        if (finishedGames.contains(gameData.gameID())) {
            throw new DataAccessException("game already over");
        }

        ChessGame.TeamColor playerColor = getPlayerColor(gameData, auth.username());
        if (playerColor == null) {
            throw new DataAccessException("observer cannot resign");
        }

        finishedGames.add(gameData.gameID());

        // Everybody in the game gets a notification, including sender
        sendToAll(gameData.gameID(),
                new NotificationMessage(auth.username() + " resigned the game."));
    }

    public void sendError(WsMessageContext ctx, String message) {
        try {
            sendToSender(ctx, new ErrorMessage(message));
        } catch (Exception e) {
            System.out.println("Could not send error message: " + e.getMessage());
        }
    }

    private AuthData requireAuth(String authToken) throws DataAccessException {
        if (authToken == null || authToken.isBlank()) {
            throw new DataAccessException("unauthorized");
        }
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new DataAccessException("unauthorized");
        }
        return auth;
    }

    private GameData requireGame(Integer gameID) throws DataAccessException {
        if (gameID == null || gameID <= 0) {
            throw new DataAccessException("bad request");
        }
        GameData game = gameDAO.getGame(gameID);
        if (game == null) {
            throw new DataAccessException("bad request");
        }
        return game;
    }

    private ChessGame.TeamColor getPlayerColor(GameData gameData, String username) {
        if (username == null) return null;
        if (username.equals(gameData.whiteUsername())) return ChessGame.TeamColor.WHITE;
        if (username.equals(gameData.blackUsername())) return ChessGame.TeamColor.BLACK;
        return null;
    }

    private void addConnection(Integer gameID, String username, WsMessageContext ctx) {
        connectionsByGame.computeIfAbsent(gameID, k -> ConcurrentHashMap.newKeySet())
                .add(new Connection(username, ctx));
    }

    private void removeConnection(Integer gameID, String username) {
        Set<Connection> connections = connectionsByGame.get(gameID);
        if (connections == null) return;

        connections.removeIf(c -> c.username().equals(username));

        if (connections.isEmpty()) {
            connectionsByGame.remove(gameID);
        }
    }

    private void sendToSender(WsMessageContext ctx, ServerMessage message) {
        ctx.send(gson.toJson(message));
    }

    private void sendToAll(Integer gameID, ServerMessage message) {
        Set<Connection> connections = connectionsByGame.get(gameID);
        if (connections == null) return;

        String json = gson.toJson(message);
        for (Connection c : connections) {
            c.ctx().send(json);
        }
    }

    private void sendToOthers(Integer gameID, String username, ServerMessage message) {
        Set<Connection> connections = connectionsByGame.get(gameID);
        if (connections == null) return;

        String json = gson.toJson(message);
        for (Connection c : connections) {
            if (!c.username().equals(username)) {
                c.ctx().send(json);
            }
        }
    }
}