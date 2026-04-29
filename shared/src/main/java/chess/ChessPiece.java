package chess;

import java.util.Collection;
import java.util.Objects;
import java.util.ArrayList;

public class ChessPiece {
    private ChessGame.TeamColor color;
    private PieceType type;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return color == that.color && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type);
    }

    public ChessPiece(ChessGame.TeamColor color, ChessPiece.PieceType type) {
        this.color = color;
        this.type = type;
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

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPos){
        return switch(this.type){
            case PAWN -> pawnMoves(board, myPos);
            case ROOK -> rookMoves(board, myPos);
            case KNIGHT -> knightMoves(board, myPos);
            case BISHOP -> bishopMoves(board, myPos);
            case QUEEN -> queenMoves(board, myPos);
            case KING -> kingMoves(board, myPos);
        };
    }

    private void tryAddMove(ChessBoard board, ChessPosition from, int r, int c, Collection<ChessMove> moves) {
       //if out of bounds we cant go there
        if (r < 1 || r > 8 || c < 1 || c > 8)
            return;
        //find the piece on tested pos
        ChessPosition to = new ChessPosition(r, c);
        ChessPiece target = board.getPiece(to);
        //if nothing there or mean guy add move
        if (target == null || target.getTeamColor() != this.color) {
            moves.add(new ChessMove(from, to, null));
        }
    }

    private void addSlidingMoves(ChessBoard board, ChessPosition start, Collection<ChessMove> moves, int[][] directions) {
        //for each direction in directions
        for (int[] dir : directions) {
            //get the start row and col
            int r = start.getRow();
            int c = start.getColumn();
            //keep testing
            while (true) {
                //add the directions to our start
                r += dir[0];
                c += dir[1];
                //check edge
                if (r < 1 || r > 8 || c < 1 || c > 8)
                    break;
                //take a look at the sqaure
                ChessPosition next = new ChessPosition(r, c);
                ChessPiece piece = board.getPiece(next);
                //if nothing there add and keep going
                if (piece == null) {
                    moves.add(new ChessMove(start, next, null));
                }
                //if soemthing there
                else {
                    //if its mean guy add move
                    if (piece.getTeamColor() != this.color) {
                        moves.add(new ChessMove(start, next, null));
                    }
                    //if its our own piece stop
                    break;
                }
            }
        }
    }

    private void addPromotionMoves(Collection<ChessMove> moves, ChessPosition from, ChessPosition to) {
        moves.add(new ChessMove(from, to, PieceType.QUEEN));
        moves.add(new ChessMove(from, to, PieceType.ROOK));
        moves.add(new ChessMove(from, to, PieceType.BISHOP));
        moves.add(new ChessMove(from, to, PieceType.KNIGHT));
    }




    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition pos) {
        Collection<ChessMove> moves = new ArrayList<>();

        //which direction should we head, from what square, where are we promoting
        int dir = (color == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (color == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int promotionRow = (color == ChessGame.TeamColor.WHITE) ? 8 : 1;

        //get our actual start
        int r = pos.getRow();
        int c = pos.getColumn();
        int forwardRow = r + dir;

        // check to see if were on edge
        if (forwardRow >= 1 && forwardRow <= 8) {
            //establish the 1 square move
            ChessPosition forward = new ChessPosition(forwardRow, c);
            //check to see if its empty
            if (board.getPiece(forward) == null) {
                //check if we can promote
                if (forwardRow == promotionRow) {
                    addPromotionMoves(moves, pos, forward);
                }
                else {
                    moves.add(new ChessMove(pos, forward, null));
                }
                //check to see if were on start line
                if (r == startRow) {
                    //then we can go up 2
                    ChessPosition doubleForward = new ChessPosition(r + 2 * dir, c);
                    //if nothing there
                    if (board.getPiece(doubleForward) == null) {
                        moves.add(new ChessMove(pos, doubleForward, null));
                    }
                }
            }
        }
        //check diagnals
        int[] cols = {c - 1, c + 1};
        //for the col in cols
        for (int newCol : cols) {
            //if were on the edge skip
            if (newCol < 1 || newCol > 8)
                continue;
            //establish square
            ChessPosition diag = new ChessPosition(forwardRow, newCol);
            ChessPiece target = board.getPiece(diag);
            //if thers an enemy there
            if (target != null && target.getTeamColor() != this.color) {
                //check promotion
                if (forwardRow == promotionRow) {
                    addPromotionMoves(moves, pos, diag);
                }
                else {
                    moves.add(new ChessMove(pos, diag, null));
                }
            }
        }
        return moves;
    }

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition pos) {

        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1}
        };

        addSlidingMoves(board, pos, moves, directions);
        return moves;
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition pos) {

        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        addSlidingMoves(board, pos, moves, directions);
        return moves;

    }

    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition pos) {

        Collection<ChessMove> moves = new ArrayList<>();

        int[][] directions = {
            {1,0},{-1,0},{0,1},{0,-1},
            {1,1},{1,-1},{-1,1},{-1,-1}
        };

        addSlidingMoves(board, pos, moves, directions);

        return moves;

    }

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition pos) {

        Collection<ChessMove> moves = new ArrayList<>();

        int[][] offsets = {

            {2,1},{2,-1},{-2,1},{-2,-1},

            {1,2},{1,-2},{-1,2},{-1,-2}

        };

        for (int[] o : offsets) {

            tryAddMove(board, pos, pos.getRow() + o[0], pos.getColumn() + o[1], moves);

        }

        return moves;

    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition pos) {

        Collection<ChessMove> moves = new ArrayList<>();

        int[][] offsets = {

            {1,0},{-1,0},{0,1},{0,-1},

            {1,1},{1,-1},{-1,1},{-1,-1}

        };

        for (int[] o : offsets) {

            tryAddMove(board, pos, pos.getRow() + o[0], pos.getColumn() + o[1], moves);

        }

        return moves;

    }
}