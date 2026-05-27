package ui;

import client.ServerFacade;
import model.AuthData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PostloginUI implements UI {
    private final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    public PostloginUI(ServerFacade server, AuthData auth) {
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
                    return null;
                }
                case "join" -> {
                    if (parts.length != 2) {
                        System.out.println("Usage: join <ID> [WHITE|BLACK]");
                        break;
                    }
                    return null;
                }
                case "observe" -> {
                    if (parts.length != 1) {
                        System.out.println("Usage: observe < ID>");
                        break;
                    }
                    return null;
                }
                case "logout" -> {
                    return null;}
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
