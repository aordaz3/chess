package client;

import com.google.gson.Gson;
import model.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ServerFacade {

    private final String serverUrl;
    private final HttpClient client;
    private final Gson gson;
    private String authToken;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
        this.client = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public AuthData login(String username, String password) throws Exception {
        LoginRequest request = new LoginRequest(username, password);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/session"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        throwIfError(response);

        AuthData auth = gson.fromJson(response.body(), AuthData.class);
        authToken = auth.authToken();
        return auth;
    }

    public AuthData register(String username, String password, String email) throws Exception {
        RegisterRequest request = new RegisterRequest(username, password, email);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/user"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        throwIfError(response);

        AuthData auth = gson.fromJson(response.body(), AuthData.class);
        authToken = auth.authToken();
        return auth;
    }

    public void logout() throws Exception {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/session"))
                .header("authorization", authToken)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        throwIfError(response);

        authToken = null;
    }

    public CreateGameResult createGame(String gameName) throws Exception {
        CreateGameRequest request = new CreateGameRequest(gameName);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game"))
                .header("authorization", authToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        throwIfError(response);

        return gson.fromJson(response.body(), CreateGameResult.class);
    }

    public ListGamesResponse listGames() throws Exception {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game"))
                .header("authorization", authToken)
                .GET()
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        throwIfError(response);

        return gson.fromJson(response.body(), ListGamesResponse.class);
    }

    public void joinGame(int gameId, String playerColor) throws Exception {
        JoinGameRequest request = new JoinGameRequest(playerColor, gameId);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game"))
                .header("authorization", authToken)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        throwIfError(response);
    }

    public void observeGame(int gameId) throws Exception {
        ListGamesResponse games = listGames();

        for (GamesSummary game : games.games()) {
            if (game.gameID() == gameId) {
                return;
            }
        }
        throw new Exception("Error: game not found");
    }


    private void throwIfError(HttpResponse<String> response) throws Exception {
        int code = response.statusCode();
        if (code == 200 || code == 201 || code == 204) {
            return;
        }

        String body = response.body();
        if (body == null || body.isBlank()) {
            throw new Exception("Request failed with status " + code);
        }

        try {
            ErrorResponse error = gson.fromJson(body, ErrorResponse.class);
            throw new Exception(error.message());
        } catch (Exception e) {
            throw new Exception("Request failed with status " + code);
        }
    }
}