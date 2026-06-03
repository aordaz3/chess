package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import client.ServerFacade;
import client.WebSocketFacade;
import model.AuthData;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class GamePlayUI implements UI{
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 1;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 1;

    private static final String EMPTY = "   ";
    private static final String BLACK_PAWN = " P ";
    private static final String BLACK_ROOK = " R ";
    private static final String BLACK_KNIGHT = " N ";
    private static final String BLACK_BISHOP = " B ";
    private static final String BLACK_QUEEN = " Q ";
    private static final String BLACK_KING = " K ";

    private static final String WHITE_PAWN = " P ";
    private static final String WHITE_ROOK = " R ";
    private static final String WHITE_KNIGHT = " N ";
    private static final String WHITE_BISHOP = " B ";
    private static final String WHITE_QUEEN = " Q ";
    private static final String WHITE_KING = " K ";

    private final ServerFacade server;
    private final AuthData auth;
    private final Integer gameID;
    private final String role;
    private final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private ChessGame game;
    private boolean isRunning = true;
    private final ChessGame.TeamColor perspective;
    private final WebSocketFacade ws;

    public GamePlayUI(ServerFacade server, AuthData authdata, Integer gameID, String role) {
        this.server = server;
        this.auth = authdata;
        this.gameID = gameID;
        this.role = role;
        this.perspective = ("BLACK".equalsIgnoreCase(role)) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
        game = new ChessGame();
        this.ws = new WebSocketFacade("http://localhost:8080");
    }

    @Override
    public UI run() {
        refreshGame();
        redrawBoard();

        while (isRunning) {
            try {
                System.out.print("Enter command or help for hints: ");
                String command = in.readLine();
                if (command == null) {
                    return new PostloginUI(server, auth);
                }

                switch (command.toLowerCase()) {
                    case "help" -> showHelp();
                    case "redraw" -> redrawBoard();
                    case "move" -> makeMove();
                    case "leave" -> {
                        return leaveGame();
                    }
                    case "resign" -> {
                        UI next = resign();
                        if (next != null) {
                            return next;
                        }
                    }
                    case "highlight" -> highlight();
                    default -> System.out.println("Unknown command");
                }
            } catch (Exception e) {
                System.out.println("Error reading input.");
            }
        }

        return new PostloginUI(server, auth);
    }

    private void refreshGame() {
        try {
            //game = server.getGame(gameID).game();
        }
        catch (Exception e) {
            System.out.println("Could not refresh game state.");
        }
    }

    private void redrawBoard() {
        refreshGame();
        if (game == null || game.getBoard() == null) {
            System.out.println("No game board available.");
            return;
        }
        drawChessBoard(System.out, game.getBoard(), perspective);
    }

    private UI leaveGame() {
        try {
            ws.leave(auth.authToken(), gameID);
            System.out.println("Leaving game...");
            isRunning = false;
            return new PostloginUI(server, auth);
        } catch (Exception e) {
            System.out.println("Could not leave game: " + e.getMessage());
            return null;
        }
    }

    private UI resign() {
        try {
            System.out.print("Are you sure you want to resign? (yes/no): ");
            String response = in.readLine();
            if (response == null || !response.equalsIgnoreCase("yes")) {
                System.out.println("Resign cancelled.");
                return null;
            }

            ws.resign(auth.authToken(), gameID);

            System.out.println("You resigned.");
            isRunning = false;
            return new PostloginUI(server, auth);
        }
        catch (Exception e) {
                System.out.println("Could not resign: " + e.getMessage());
                return null;
            }
    }

    private void makeMove() {
        var move = null;
        ws.makeMove(auth.authToken(), gameID, move);
    }

    private void highlight() {

    }
    private void showHelp() {
        System.out.println();
        System.out.println("Help");
        System.out.println("----");
        System.out.println("Help: Displays text informing the user what actions they can take.");
        System.out.println("Redraw Chess Board: Redraws the chess board upon request.");
        System.out.println("Leave: Removes you from the game and returns to the post-login screen.");
        System.out.println("Make Move: Prompts for a move and updates the board.");
        System.out.println("Resign: Prompts for confirmation and ends the game if accepted.");
        System.out.println("Highlight Legal Moves: Prompts for a piece and highlights its legal moves.");
        System.out.println();
    }


    private static void drawChessBoard(PrintStream out, ChessBoard board, ChessGame.TeamColor perspective) {
        drawColumnLabels(out, perspective);

        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; boardRow++) {
            drawRow(out, board, boardRow, perspective);
        }

        drawColumnLabels(out, perspective);
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void colorPiece(PrintStream out, ChessGame.TeamColor color) {
        if (color == ChessGame.TeamColor.WHITE) {
            out.print(SET_TEXT_COLOR_RED);
        }
        else {
            out.print(SET_TEXT_COLOR_BLUE);
        }
    }

    private static void drawColumnLabels(PrintStream out, ChessGame.TeamColor perspective) {
        out.print("   ");
        if (perspective == ChessGame.TeamColor.WHITE) {
            for (char col = 'a'; col <= 'h'; col++) {
                out.print(" " + col + " ");
            }
        }
        else {
            for (char col = 'h'; col >= 'a'; col--) {
                out.print(" " + col + " ");
            }
        }
        out.println();
    }

    private static void drawRow(PrintStream out, ChessBoard board, int boardRow, ChessGame.TeamColor perspective) {
        int chessRow = (perspective == ChessGame.TeamColor.WHITE) ? 8 - boardRow : boardRow + 1;

        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
        out.print(" " + chessRow + " ");

        for (int col = 0; col < BOARD_SIZE_IN_SQUARES; col++) {
            int boardCol = (perspective == ChessGame.TeamColor.WHITE) ? col : 7 - col;

            boolean isWhiteSquare = (boardRow + col) % 2 == 0;
            if (isWhiteSquare) {
                setWhite(out);
            }
            else {
                setBlack(out);
            }

            ChessPiece piece = board.getPiece(new ChessPosition(chessRow, boardCol + 1));

            if (piece == null) {
                out.print(EMPTY);
            }
            else {
                colorPiece(out, piece.getTeamColor());
                out.print(pieceToString(piece));
            }
        }

        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
        out.print(" " + chessRow);
        out.println();
    }

    private static String pieceToString(ChessPiece piece) {
        if (piece == null) {
            return EMPTY;
        }
        return switch (piece.getPieceType()) {
            case PAWN -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_PAWN : BLACK_PAWN;
            case ROOK -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_ROOK : BLACK_ROOK;
            case KNIGHT -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KNIGHT : BLACK_KNIGHT;
            case BISHOP -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_BISHOP : BLACK_BISHOP;
            case QUEEN -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_QUEEN : BLACK_QUEEN;
            case KING -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KING : BLACK_KING;
        };
    }
}