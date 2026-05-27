package client;

import model.AuthData;
import model.ListGamesResponse;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public AuthData login(String username, String password) throws Exception {
        // create request object
        return null;
    }

    public AuthData register(String username, String password, String email) throws Exception {
        // create request object
        return null;
    }

    public void logout(){
        //create request
    }
    public void createGame(String gameName){
        //create request
    }
    public ListGamesResponse listGames(){

    }
    public void joinGame(int gameId, String playerColor){

    }
    public void observeGame(int gameId){

    }
}