package ui;

import chess.ChessGame;
import chess.ChessPiece;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class BoardUI {
    //dim
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 1;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 1;

    //padded char
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

        drawChessBoard(out);
    }

    private static void drawChessBoard(PrintStream out) {
        drawColumnLabels(out);
        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
            drawRow(out, boardRow);
        }
        drawColumnLabels(out);
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }
    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }
    private static void colorPiece(PrintStream out, ChessGame.TeamColor color){
        if(color == ChessGame.TeamColor.WHITE){
            out.print(SET_TEXT_COLOR_RED);
        }
        out.print(SET_TEXT_COLOR_BLUE);
    }
    private static void drawColumnLabels(PrintStream out) {
        out.print("   ");
        for (char col = 'a'; col <= 'h'; col++) {
            out.print(" " + col + " ");
        }
        out.println();
    }
    private static void drawRow(PrintStream out, int boardRow) {

        int chessRow = 8 - boardRow;
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
        out.print(" " + chessRow + " ");

        for (int col = 0; col < BOARD_SIZE_IN_SQUARES; col++) {

            boolean isWhiteSquare = (boardRow + col) % 2 == 0;

            if (isWhiteSquare) {
                setWhite(out);
            } else {
                setBlack(out);
            }
            colorPiece(out, ChessGame.TeamColor.BLACK);
            out.print(BLACK_PAWN);
        }

        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);

        out.print(" " + chessRow);

        out.println();
    }

}
