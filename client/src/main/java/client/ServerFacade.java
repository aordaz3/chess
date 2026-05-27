package client;

import com.google.gson.Gson;
import model.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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

        if (response.statusCode() != 200) {
            throw new Exception("Login failed: " + response.body());
        }

        LoginResult result = gson.fromJson(response.body(), LoginResult.class);
        authToken = result.authToken();
        return new AuthData(result.authToken(), result.username());
    }

    public AuthData register(String username, String password, String email) throws Exception {
        RegisterRequest request = new RegisterRequest(username, password, email);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/user"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Register failed: " + response.body());
        }

        RegisterResult result = gson.fromJson(response.body(), RegisterResult.class);
        authToken = result.authToken();
        return new AuthData(result.authToken(), result.username());
    }

    public void logout() throws Exception {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/session"))
                .header("authorization", authToken)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new Exception("Logout failed: " + response.body());
        }
        authToken = null;
    }
    public void createGame(String gameName){
        //create request
    }
    public ListGamesResponse listGames(){
        return null;
    }
    public void joinGame(int gameId, String playerColor){

    }
    public void observeGame(int gameId){

    }
}