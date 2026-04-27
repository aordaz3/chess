package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessGame.TeamColor color;
    private PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        chess.ChessPiece piece = board.getPiece(myPosition);
        PieceType type = piece.getPieceType();
        Collection<ChessMove> moves = new ArrayList<>();
        switch (type) {
            case PAWN -> moves.addAll(pawnMoves(board, myPosition));
            case ROOK -> moves.addAll(rookMoves(board, myPosition));
            case KNIGHT -> moves.addAll(knightMoves(board, myPosition));
            case BISHOP -> moves.addAll(bishopMoves(board, myPosition));
            case QUEEN -> moves.addAll(queenMoves(board, myPosition));
            case KING -> moves.addAll(kingMoves(board, myPosition));
        }
        return moves;

    }

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition pos){
        return null;
    }
    //slides so we iterate till end of board col and row
    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition pos){
        int r = pos.getRow();
        int c = pos.getColumn();
        Collection<ChessMove> moves = new ArrayList<>();
        //go  right
        for(int i = c + 1; i <= 8; i++){
            ChessPosition right = new ChessPosition(r, i);
            //if nothing there add
            if (board.getPiece(right) == null){
                ChessMove move = new ChessMove(pos, right, null);
                moves.add(move);
            }
            //if can capture add and break
            else if (board.getPiece(right).color != this.color) {
                ChessMove move = new ChessMove(pos, right, null);
                moves.add(move);
                break;
            }
            //if our own piece break
            else{
                break;
            }
        }
        //go left
        for(int i = c-1; i >= 1; i--){
            ChessPosition left = new ChessPosition(r, i);
            if(board.getPiece(left) == null){
                ChessMove move = new ChessMove(pos, left, null);
                moves.add(move);
            }
            else if (board.getPiece(left).color != this.color) {
                ChessMove move = new ChessMove(pos, left, null);
                moves.add(move);
                break;
            }
            else{
                break;
            }
        }
        // go up
        for(int i = r+1; i <= 8; i++){
            ChessPosition up = new ChessPosition(i, c);
            if(board.getPiece(up) == null){
                ChessMove move = new ChessMove(pos, up, null);
                moves.add(move);
            }
            else if (board.getPiece(up).color != this.color) {
                ChessMove move = new ChessMove(pos, up, null);
                moves.add(move);
                break;
            }
            else{
                break;
            }
        }
        //go down
        for(int i = r - 1; i >= 1; i--){
            ChessPosition down = new ChessPosition(i, c);
            if(board.getPiece(down) == null){
                ChessMove move = new ChessMove(pos, down, null);
                moves.add(move);
            }
            else if (board.getPiece(down).color != this.color) {
                ChessMove move = new ChessMove(pos, down, null);
                moves.add(move);
                break;
            }
            else{
                break;
            }
        }
        return moves;
    }
    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition pos){
        return null;
    }
    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition pos){
        return null;
    }
    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition pos){
        return null;
    }
    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition pos){
        return null;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return color == that.color && type == that.type;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(color, type);
    }
}
