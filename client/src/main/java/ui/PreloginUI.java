package ui;

import client.ServerFacade;
import model.AuthData;

import javax.imageio.IIOException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PreloginUI implements UI{
    private final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private final ServerFacade server;

    public PreloginUI(ServerFacade server) {

        this.server = server;

    }
    @Override
    public UI run() {
        System.out.println("WELCOME TO CHESS. TYPE HELP TO GET STARTED.");
        while (true){

            System.out.println("[LOGGED_OUT]>>>>");
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
            switch (command) {
                case "help" -> printHelp();
                case "quit" -> {
                    System.out.println("Goodbye.");
                    System.exit(0);
                }
                case "login" -> {
                    if (parts.length != 3) {
                        System.out.println("Usage: login <USERNAME> <PASSWORD>");
                        break;
                    }
                    String username = parts[1];
                    String password = parts[2];
                    try {
                        AuthData auth = server.login(username, password);
                        System.out.println("Login successful.");
                        return new PostloginUI(server, auth);
                    } catch (Exception e) {
                        System.out.println("Login failed: " + e.getMessage());
                    }
                }
                case "register" -> {
                    if (parts.length != 4) {
                        System.out.println("Usage: register <USERNAME> <PASSWORD> <EMAIL>");
                        break;
                    }
                    String username = parts[1];
                    String password = parts[2];
                    String email = parts[3];
                    try {
                        AuthData auth = server.register(username, password, email);
                        System.out.println("Registration successful.");
                        return new PostloginUI(server, auth);
                    } catch (Exception e) {
                        System.out.println("Registration failed: " + e.getMessage());
                    }

                }

                default -> System.out.println("Unknown command. Type Help.");

            }

        }
    }

    private void printHelp(){
        System.out.println("register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
        System.out.println("login <USERNAME> <PASSWORD> - to play chess");
        System.out.println("quit - playing chess");
        System.out.println("help - with possible commands");
    }
}
