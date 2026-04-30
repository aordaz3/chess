package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class ChessPiece {
    private PieceType type;
    private ChessGame.TeamColor color;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return type == that.type && color == that.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, color);
    }

    public ChessPiece(ChessGame.TeamColor color, PieceType type){
        this.type = type;
        this.color = color;
    }
    public enum PieceType{
        ROOK, KNIGHT, BISHOP,
        QUEEN, KING,
        PAWN
    }

    public PieceType getPieceType() {
        return type;
    }

    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition start){
        return switch (this.type){
            case ROOK -> rookMoves(board, start);
            case KNIGHT -> knightMoves(board, start);
            case BISHOP -> bishopMoves(board, start);
            case KING -> kingMoves(board, start);
            case QUEEN -> queenMoves(board, start);
            case PAWN -> pawnMoves(board, start);
        };
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition start){
        int[][] directions = {
                {1,1}, {-1, 1}, {1, -1}, {-1, -1}
        };
        Collection<ChessMove> moves = new ArrayList<>();
        slidingMoves(directions, board, start, moves);
        return moves;
    }

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition start){
        int[][] directions = {
                {1,0}, {-1, 0}, {0, 1}, {0, -1}
        };
        Collection<ChessMove> moves = new ArrayList<>();
        slidingMoves(directions, board, start, moves);
        return moves;
    }

    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition start){
        int[][] directions = {
                {1,0}, {-1, 0}, {0, 1}, {0, -1},
                {1,1}, {-1, 1}, {1, -1}, {-1, -1}
        };
        Collection<ChessMove> moves = new ArrayList<>();
        slidingMoves(directions, board, start, moves);
        return moves;
    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition start){
        int row = start.getRow();
        int col = start.getColumn();
        int[][] directions = {
                {1,0}, {-1, 0}, {0, 1}, {0, -1},
                {1,1}, {-1, 1}, {1, -1}, {-1, -1}
        };
        Collection<ChessMove> moves = new ArrayList<>();
        for(int[] dir : directions){
            tryMove(board, start, row+dir[0], col+dir[1], moves);
        }
        return moves;
    }

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition start){
        int row = start.getRow();
        int col = start.getColumn();
        int[][] directions = {
                {2,1}, {1,2},
                {2,-1}, {-1,2},
                {-2,1}, {-2,-1},
                {1,-2}, {-1,-2}
        };
        Collection<ChessMove> moves = new ArrayList<>();
        for(int[] dir : directions){
            tryMove(board, start, row + dir[0], col + dir[1], moves);
        }
        return moves;
    }

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition start){
        int dir = (color == ChessGame.TeamColor.WHITE)? 1 : -1;
        int startRow = (color == ChessGame.TeamColor.WHITE)? 2: 7;
        int promotionRow = (color == ChessGame.TeamColor.WHITE)? 8 : 1;

        Collection<ChessMove> moves = new ArrayList<>();

        int row = start.getRow();
        int col = start.getColumn();

        int forwardRow = row + dir;

        //if we can go forward one
        if(forwardRow<=8 && forwardRow >=1) {
            ChessPosition forwardOne = new ChessPosition(forwardRow, col);
            ChessPiece target1 = board.getPiece(forwardOne);
            //if nothing there we can move, so check for promo
            if (target1 == null) {
                if (forwardRow == promotionRow) {
                    promotionMove(moves, start, forwardOne);
                }
                else
                    moves.add(new ChessMove(start, forwardOne, null));

                //now lets check if we can move double
                if(startRow == row){
                    ChessPosition forwardTwo = new ChessPosition(row + (dir * 2), col);
                    ChessPiece target2 = board.getPiece(forwardTwo);
                    //if nothing there we can move up 2
                    if(target2 == null)
                        moves.add(new ChessMove(start, forwardTwo, null));
                }
            }
        }

        //captures
        int[] captureMoves = {col-1, col + 1};
        for(int cap : captureMoves){
            //check to see if capture is still on board
            if(cap >= 1 && cap <= 8) {
                ChessPosition diag = new ChessPosition(forwardRow, cap);
                ChessPiece target3 = board.getPiece(diag);
                //make sure something is there to capture
                if(target3 != null && target3.getTeamColor() != this.color){
                    if(forwardRow == promotionRow){
                        promotionMove(moves,start, diag);
                    }
                    else
                        moves.add(new ChessMove(start, diag, null));
                }
            }
        }
        return moves;
    }

    private void slidingMoves(int[][] directions, ChessBoard board, ChessPosition start, Collection<ChessMove> moves){
        for(int[] dir : directions){
            int row = start.getRow();
            int col = start.getColumn();

            while (true){
                row = row + dir[0];
                col = col + dir[1];

                if(row < 1 || row > 8 || col < 1 || col >8)
                    break;
                ChessPosition end = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(end);
                if(target == null){
                    moves.add(new ChessMove(start, end, null));
                }
                else {
                    if(target.getTeamColor() != this.color){
                        moves.add(new ChessMove(start, end, null));
                    }
                    break;
                }
            }
        }
    }

    private void tryMove(ChessBoard board, ChessPosition start, int row, int col, Collection<ChessMove> moves){

        if(row<1 || row>8 || col<1 || col>8)
            return;
        ChessPosition end = new ChessPosition(row, col);
        ChessPiece target = board.getPiece(end);
        if(target == null || target.getTeamColor() != this.color){
            moves.add(new ChessMove(start, end, null));
        }
    }

    private void promotionMove(Collection<ChessMove> moves, ChessPosition start, ChessPosition end){
        //need to add queen etc
        moves.add(new ChessMove(start, end, PieceType.QUEEN));
        moves.add(new ChessMove(start, end, PieceType.ROOK));
        moves.add(new ChessMove(start, end, PieceType.BISHOP));
        moves.add(new ChessMove(start, end, PieceType.KNIGHT));
    }
}