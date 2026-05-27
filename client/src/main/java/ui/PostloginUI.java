package ui;

import client.ServerFacade;
import model.AuthData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PostloginUI implements UI {
    private final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private final ServerFacade server;
    private final AuthData authData;
    public PostloginUI(ServerFacade server, AuthData auth) {
        this.server = server;
        authData = auth;
    }

    @Override
    public UI run() {
        System.out.println("WELMCOME <USERNAME>. TYPE HELP TO GET STARTED.");
        while (true){
            System.out.println("[LOGGED_IN]>>>>");
            String input;
            try{
                input = in.readLine();
            }
            catch (IOException e){
                System.out.println("Input Error");
                return this;
            }
            if(input == null){
                return null;
            }
            if(input.isEmpty()){
                continue;
            }
            String[] parts = input.split("\\s+");
            String command = parts[0].toLowerCase();
            switch (command){
                case "help" -> printHelp();
                case "list" -> {
                    try {
                        server.listGames();
                    }
                    catch (Exception e){
                        System.out.println("List Failed: " + e.getMessage());
                    }
                }
                case "join" -> {
                    if (parts.length != 3) {
                        System.out.println("Usage: join <ID> [WHITE|BLACK]");
                        break;
                    }
                    String ID = parts[1];
                    String color = parts[2];
                    try {
                        server.playGame();
                        return new BoardUI();
                    }
                    catch (Exception e){
                        System.out.println("Play Failed: " + e.getMessage());
                    }
                }
                case "observe" -> {
                    if (parts.length != 2) {
                        System.out.println("Usage: observe < ID>");
                        break;
                    }
                    String ID = parts[1];
                    try {
                        server.observeGame();
                    }
                    catch (Exception e){
                        System.out.println("Observe Failed: " + e.getMessage());
                    }
                }
                case "logout" -> {
                    try {
                        server.logout();
                    }
                    catch (Exception e){
                        System.out.println("Logout Failed: " + e.getMessage());
                    }
                    return new PreloginUI(server);
                }
                case "quit" -> {
                    System.out.println("Goodbye.");
                    System.exit(0);
                }
            }
        }

    }
    private void printHelp(){
        System.out.println("create <NAME> - a game " +
                        "\nlist - games " +
                        "\njoin <ID> [WHITE|BLACK] - a game " +
                        "\nobserve < ID> - a game " +
                        "\nlogout - when you are done " +
                        "\nquit - playing chess " +
                        "\nhelp - with possible commands");
    }
}
