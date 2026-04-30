package chess;

import java.util.Objects;

public class ChessMove {
    private ChessPosition start;
    private ChessPosition end;
    private ChessPiece.PieceType promotion;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(start, chessMove.start) && Objects.equals(end, chessMove.end) && promotion == chessMove.promotion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, promotion);
    }

    public ChessMove(ChessPosition start, ChessPosition end, ChessPiece.PieceType promotion){
        this.start = start;
        this.end = end;
        this.promotion = promotion;
    }

    public ChessPiece.PieceType getPromotionPiece() {
        return promotion;
    }

    public ChessPosition getStartPosition() {
        return start;
    }

    public ChessPosition getEndPosition(){
        return end;
    }
}