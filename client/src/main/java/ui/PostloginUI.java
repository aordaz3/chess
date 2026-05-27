package ui;

import client.ServerFacade;
import model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PostloginUI implements UI {
    private final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private final ServerFacade server;
    private final AuthData authData;
    private List<GamesSummary> lastListedGames = new ArrayList<>();

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
                        ListGamesResponse response = server.listGames();
                        lastListedGames = (List<GamesSummary>) response.games();

                        if (lastListedGames.isEmpty()) {
                            System.out.println("No games available.");
                            break;
                        }
                        for (int i = 0; i < lastListedGames.size(); i++) {
                            GamesSummary game = lastListedGames.get(i);
                            System.out.println((i + 1) + ". " + game.gameName()
                                    + " | White: " + (game.whiteUsername() == null ? "-" : game.whiteUsername())
                                    + " | Black: " + (game.blackUsername() == null ? "-" : game.blackUsername()));
                        }
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
                        System.out.println("Usage: join <NUMBER> <WHITE|BLACK>");
                        break;
                    }
                    try {
                        int selection = Integer.parseInt(parts[1]);
                        String color = parts[2].toUpperCase();

                        if (selection < 1 || selection > lastListedGames.size()) {
                            System.out.println("Invalid game number.");
                            break;
                        }

                        GamesSummary game = lastListedGames.get(selection - 1);
                        server.joinGame(game.gameID(), color);

                        return new BoardUI(server, authData, game.gameID(), color);
                    }
                    catch (NumberFormatException e) {
                        System.out.println("Please enter a valid number.");
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
                        return new BoardUI(server, authData, gameId, "WHITE");
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