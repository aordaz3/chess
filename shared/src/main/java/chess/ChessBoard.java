package chess;

import java.util.Objects;
import java.util.Arrays;

public class ChessBoard {
    private ChessPiece[][] board;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    public ChessBoard(){
        this.board = new ChessPiece[8][8];
    }
    public ChessPiece getPiece(ChessPosition pos){
        int r = pos.getRow();
        int c = pos.getColumn();

        return board[r-1][c-1];
    }

    public void addPiece(ChessPosition pos, ChessPiece p){
        int r = pos.getRow();
        int c = pos.getColumn();

        this.board[r-1][c-1] = p;
    }

    public void resetBoard(){
        this.board = new ChessPiece[8][8];

        ChessPiece.PieceType[] backRow = {
                ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.KING,
                ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.ROOK
        };

        for(int i = 1; i<=8; i++){
            //pawns
            addPiece(new ChessPosition(2, i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
            addPiece(new ChessPosition(7, i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));

            //back row
            addPiece(new ChessPosition(1, i), new ChessPiece(ChessGame.TeamColor.WHITE, backRow[i-1]));
            addPiece(new ChessPosition(8, i), new ChessPiece(ChessGame.TeamColor.BLACK, backRow[i-1]));
        }
    }
}