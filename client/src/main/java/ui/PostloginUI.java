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

    public PostloginUI(ServerFacade server, AuthData authData) {
        this.server = server;
        this.authData = authData;
    }

    @Override
    public UI run() {
        System.out.println("WELCOME " + authData.username() + ". TYPE HELP TO GET STARTED.");

        while (true) {
            System.out.print("[LOGGED_IN] >>> ");

            String input;
            try {
                input = in.readLine();
            }
            catch (IOException e) {
                System.out.println("Input error.");
                return this;
            }
            if (input == null) {
                return null;
            }
            input = input.trim();
            if (input.isEmpty()) {
                continue;
            }

            String[] parts = input.split("\\s+");
            String command = parts[0].toLowerCase();

            switch (command) {
                case "help" -> printHelp();

                case "list" -> {
                    try {
                        System.out.println(server.listGames());
                    }
                    catch (Exception e) {
                        System.out.println("List failed: " + e.getMessage());
                    }
                }

                case "create" -> {
                    if (parts.length != 2) {
                        System.out.println("Usage: create <NAME>");
                        break;
                    }
                    try {
                        server.createGame(parts[1]);
                        System.out.println("Game created.");
                    }
                    catch (Exception e) {
                        System.out.println("Create failed: " + e.getMessage());
                    }
                }

                case "join" -> {
                    if (parts.length != 3) {
                        System.out.println("Usage: join <ID> <WHITE|BLACK>");
                        break;
                    }
                    try {
                        int gameId = Integer.parseInt(parts[1]);
                        String color = parts[2].toUpperCase();
                        server.joinGame(gameId, color);
                        return new BoardUI(server, authData, gameId, color);
                    }
                    catch (Exception e) {
                        System.out.println("Join failed: " + e.getMessage());
                    }
                }

                case "observe" -> {
                    if (parts.length != 2) {
                        System.out.println("Usage: observe <ID>");
                        break;
                    }
                    try {
                        int gameId = Integer.parseInt(parts[1]);
                        server.observeGame(gameId);
                        return new BoardUI(server, authData, gameId, "OBSERVER");
                    }
                    catch (Exception e) {
                        System.out.println("Observe failed: " + e.getMessage());
                    }
                }
                case "logout" -> {
                    try {
                        server.logout();
                        return new PreloginUI(server);
                    }
                    catch (Exception e) {
                        System.out.println("Logout failed: " + e.getMessage());
                    }
                }
                case "quit" -> {
                    System.out.println("Goodbye.");
                    return null;
                }
                default -> System.out.println("Unknown command. Type Help.");
            }
        }
    }

    private void printHelp() {
        System.out.println("create <NAME> - a game " +
                "\nlist - games " +
                "\njoin <ID> [WHITE|BLACK] - a game " +
                "\nobserve < ID> - a game " +
                "\nlogout - when you are done " +
                "\nquit - playing chess " +
                "\nhelp - with possible commands");
    }
}