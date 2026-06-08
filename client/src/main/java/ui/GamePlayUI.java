package ui;

import chess.*;
import client.ServerFacade;
import client.WebSocketFacade;
import model.AuthData;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static ui.EscapeSequences.*;

public class GamePlayUI implements UI {
    private static final String HIGHLIGHT_BG = "\u001B[43m";
    private static final String HIGHLIGHT_TEXT = "\u001B[30m";

    private static final int BOARD_SIZE_IN_SQUARES = 8;
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
    private final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private final WebSocketFacade ws;

    private volatile ChessGame game;
    private volatile boolean isRunning = true;
    private final ChessGame.TeamColor perspective;
    private final boolean isObserver;

    public GamePlayUI(ServerFacade server, AuthData authdata, Integer gameID, String role) {
        this(server, authdata, gameID, role, null);
    }
    public GamePlayUI(ServerFacade server, AuthData authdata, Integer gameID, String role, ChessGame initialGame) {
        this.server = server;
        this.auth = authdata;
        this.gameID = gameID;
        this.isObserver = "OBSERVER".equalsIgnoreCase(role);
        this.perspective = "BLACK".equalsIgnoreCase(role) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
        this.ws = new WebSocketFacade("http://localhost:8080");
        this.game = initialGame;
    }

    @Override
    public UI run() {
        try {
            ws.connect(auth.authToken(), gameID, new WebSocketFacade.GameMessageHandler() {
                @Override
                public void onLoadGame(LoadGameMessage message) {
                    //System.out.println("CLIENT GOT LOAD_GAME");
                    game = message.getGame().game();
                    redrawBoard();
                }

                @Override
                public void onNotification(NotificationMessage message) {
                    System.out.println(message.getMessage());
                }

                @Override
                public void onError(ErrorMessage message) {
                    System.out.println(message.getErrorMessage());
                }

                @Override
                public void onClose() {
                    //System.out.println("WebSocket closed.");
                    isRunning = false;
                }
            });
        } catch (Exception e) {
            System.out.println("Could not connect to game: " + e.getMessage());
            return new PostloginUI(server, auth);
        }
        if (game != null) {
            redrawBoard();
        }
        //System.out.println("Connected to game.");

        while (isRunning) {
            if (!handleCommand()) {
                return new PostloginUI(server, auth);
            }
        }
        return new PostloginUI(server, auth);
    }

    private boolean handleCommand() {
        try {
            System.out.print("Enter command or help for hints: \n");
            String command = in.readLine();

            if (command == null) {
                return false;
            }

            switch (command.toLowerCase()) {
                case "help" -> showHelp();
                case "redraw" -> redrawBoard();
                case "leave" -> {
                    leaveGame();
                    return false;
                }
                case "move" -> helperMove();
                case "resign" -> {
                    UI next = helperResign();
                    if (next != null) {return false;}
                }
                case "highlight" -> highlight();
                default -> System.out.println("Unknown command");
            }

            return true;
        } catch (Exception e) {
            System.out.println("Error reading input.");
            return true;
        }
    }

    private UI helperResign() {
        if (isObserver) {
            System.out.println("Observers cannot resign.");
            return null;
        }
        return resign();
    }
    private void helperMove() {
        if (isObserver) {
            System.out.println("Observers cannot make moves.");
            return;
        }
        makeMove();
    }
    private UI leaveGame() {
        try {
            ws.leave(auth.authToken(), gameID);
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
            ws.close();
            isRunning = false;
            return new PostloginUI(server, auth);
        } catch (Exception e) {
            System.out.println("Could not resign: " + e.getMessage());
            return null;
        }
    }

    private void makeMove() {
        try {
            if (game == null || game.getBoard() == null) {
                System.out.println("Game is not loaded yet.");
                return;
            }

            System.out.print("From square (example: e2): ");
            String from = in.readLine();
            System.out.print("To square (example: e4): ");
            String to = in.readLine();
            System.out.print("Promotion piece (queen, rook, bishop, knight, or blank): ");
            String promo = in.readLine();

            if (from == null || to == null || from.length() != 2 || to.length() != 2) {
                System.out.println("Invalid move input.");
                return;
            }

            ChessPosition start = parsePosition(from);
            ChessPosition end = parsePosition(to);
            if (start == null || end == null) {
                System.out.println("Invalid square.");
                return;
            }

            ChessPiece.PieceType promotion = parsePromotion(promo);

            ChessMove move = new ChessMove(start, end, promotion);
            ws.makeMove(auth.authToken(), gameID, move);
        } catch (Exception e) {
            System.out.println("Could not make move: " + e.getMessage());
        }
    }

    private void highlight() {
        try {
            if (game == null || game.getBoard() == null) {
                System.out.println("Game is not loaded yet.");
                return;
            }

            System.out.print("Piece square (example: e2): ");
            String square = in.readLine();
            ChessPosition start = parsePosition(square);

            if (start == null) {
                System.out.println("Invalid square.");
                return;
            }

            ChessPiece piece = game.getBoard().getPiece(start);
            if (piece == null) {
                System.out.println("There is no piece on that square.");
                return;
            }

            Collection<ChessMove> moves = game.validMoves(start);
            if (moves.isEmpty()) {
                System.out.println("That piece has no legal moves.");
                return;
            }

            Set<ChessPosition> highlights = new HashSet<>();
            highlights.add(start);
            for (ChessMove move : moves) {
                highlights.add(move.getEndPosition());
            }

            redrawBoard(highlights);
        } catch (Exception e) {
            System.out.println("Could not highlight moves: " + e.getMessage());
        }
    }

    private ChessPosition parsePosition(String square) {
        square = square.trim().toLowerCase();
        if (square.length() != 2) {
            return null;
        }

        char file = square.charAt(0);
        char rank = square.charAt(1);

        int col = file - 'a' + 1;
        int row = rank - '0';

        if (col < 1 || col > 8 || row < 1 || row > 8) {
            return null;
        }

        return new ChessPosition(row, col);
    }

    private ChessPiece.PieceType parsePromotion(String promo) {
        if (promo == null || promo.isBlank()) {
            return null;
        }

        return switch (promo.trim().toLowerCase()) {
            case "queen" -> ChessPiece.PieceType.QUEEN;
            case "rook" -> ChessPiece.PieceType.ROOK;
            case "bishop" -> ChessPiece.PieceType.BISHOP;
            case "knight" -> ChessPiece.PieceType.KNIGHT;
            default -> null;
        };
    }

    private void showHelp() {
        System.out.println();
        System.out.println("----");
        System.out.println("Help: Displays text informing the user what actions they can take.");
        System.out.println("Redraw Chess Board: Redraws the chess board upon request.");
        System.out.println("Leave: Removes you from the game and returns to the post-login screen.");
        System.out.println("Move: Prompts for a move and updates the board.");
        System.out.println("Resign: Prompts for confirmation and ends the game if accepted.");
        System.out.println("Highlight Legal Moves: Prompts for a piece and highlights its legal moves.");
        System.out.println();
    }

    private void redrawBoard() {
        redrawBoard(Set.of());
    }

    private void redrawBoard(Set<ChessPosition> highlights) {
        if (game == null || game.getBoard() == null) {
            System.out.println("No game board available yet.");
            return;
        }
        drawChessBoard(System.out, game.getBoard(), perspective, highlights);
    }

    private static void drawChessBoard(PrintStream out, ChessBoard board,
                                       ChessGame.TeamColor perspective,
                                       Set<ChessPosition> highlights) {
        drawColumnLabels(out, perspective);

        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; boardRow++) {
            drawRow(out, board, boardRow, perspective, highlights);
        }

        drawColumnLabels(out, perspective);
    }

    private static void drawColumnLabels(PrintStream out, ChessGame.TeamColor perspective) {
        out.print("   ");
        if (perspective == ChessGame.TeamColor.WHITE) {
            for (char col = 'a'; col <= 'h'; col++) {
                out.print(" " + col + " ");
            }
        } else {
            for (char col = 'h'; col >= 'a'; col--) {
                out.print(" " + col + " ");
            }
        }
        out.println();
    }

    private static void drawRow(PrintStream out, ChessBoard board, int boardRow,
                                ChessGame.TeamColor perspective,
                                Set<ChessPosition> highlights) {
        int chessRow = (perspective == ChessGame.TeamColor.WHITE) ? 8 - boardRow : boardRow + 1;

        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
        out.print(" " + chessRow + " ");

        for (int col = 0; col < BOARD_SIZE_IN_SQUARES; col++) {
            int boardCol = (perspective == ChessGame.TeamColor.WHITE) ? col : 7 - col;
            ChessPosition pos = new ChessPosition(chessRow, boardCol + 1);
            boolean highlighted = highlights != null && highlights.contains(pos);
            boolean isWhiteSquare = (boardRow + col) % 2 == 0;

            if (highlighted) {
                out.print(HIGHLIGHT_BG);
                out.print(HIGHLIGHT_TEXT);
            } else if (isWhiteSquare) {
                out.print(SET_BG_COLOR_WHITE);
                out.print(SET_TEXT_COLOR_WHITE);
            } else {
                out.print(SET_BG_COLOR_BLACK);
                out.print(SET_TEXT_COLOR_BLACK);
            }

            ChessPiece piece = board.getPiece(pos);
            if (piece == null) {
                out.print(EMPTY);
            } else {
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    out.print(SET_TEXT_COLOR_RED);
                } else {
                    out.print(SET_TEXT_COLOR_BLUE);
                }
                out.print(pieceToString(piece));
            }
        }

        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
        out.print(" " + chessRow);
        out.println();
    }

    private static String pieceToString(ChessPiece piece) {
        if (piece == null){
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