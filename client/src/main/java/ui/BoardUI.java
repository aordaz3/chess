package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class BoardUI {
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

    private static final String WHITE_PAWN = " p ";
    private static final String WHITE_ROOK = " r ";
    private static final String WHITE_KNIGHT = " n ";
    private static final String WHITE_BISHOP = " b ";
    private static final String WHITE_QUEEN = " q ";
    private static final String WHITE_KING = " k ";

    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
        ChessBoard board = new ChessBoard();
        board.resetBoard();

        ChessGame.TeamColor perspective = ChessGame.TeamColor.BLACK;
        drawChessBoard(out, board, perspective);
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
        int displayRow = (perspective == ChessGame.TeamColor.WHITE) ? boardRow : 7 - boardRow;
        int chessRow = (perspective == ChessGame.TeamColor.WHITE) ? 8 - boardRow : boardRow + 1;

        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
        out.print(" " + chessRow + " ");

        for (int col = 0; col < BOARD_SIZE_IN_SQUARES; col++) {
            int boardCol = (perspective == ChessGame.TeamColor.WHITE) ? col : 7 - col;

            boolean isWhiteSquare = (displayRow + col) % 2 == 0;
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