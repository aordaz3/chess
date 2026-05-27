package client;

import model.AuthData;

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
    public void createGame(){
        //create request
    }
    public void listGames(){

    }
    public void playGame(){

    }
    public void observeGame(){

    }
}