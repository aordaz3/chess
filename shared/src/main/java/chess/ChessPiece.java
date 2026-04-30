package chess;

import java.util.Collection;
import java.util.Objects;
import java.util.ArrayList;

public class ChessPiece {
    private ChessGame.TeamColor teamColor;
    private PieceType type;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return teamColor == that.teamColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, type);
    }

    public ChessPiece(ChessGame.TeamColor teamColor, PieceType type){
        this.type = type;
        this.teamColor = teamColor;
    }

    public enum PieceType{
        ROOK, BISHOP, KNIGHT, KING, QUEEN, PAWN
    }

    public PieceType getPieceType() {
        return type;
    }

    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition pos){
        return switch (this.type){
            case QUEEN -> queenMove(board, pos);
            case KING -> kingMove(board, pos);
            case BISHOP -> bishopMove(board, pos);
            case KNIGHT -> knightMove(board, pos);
            case ROOK -> rookMove(board, pos);
            case PAWN -> pawnMove(board, pos);
        };
    }
    private Collection<ChessMove> queenMove(ChessBoard board, ChessPosition pos){
        int[][] directions = {
                {1, 0}, {0,1}, {-1, 0}, {0, -1},
                {1,1}, {-1, 1}, {1, -1}, {-1, -1}
        };
        Collection<ChessMove> moves = new ArrayList<>();
        addSilding(board, pos, directions, moves);
        return moves;
    }

    private Collection<ChessMove> kingMove(ChessBoard board, ChessPosition pos){
        int[][] directions = {
                {1, 0}, {0,1}, {-1, 0}, {0, -1},
                {1,1}, {-1, 1}, {1, -1}, {-1, -1}
        };
        Collection<ChessMove> moves = new ArrayList<>();
        for(int [] d : directions)
            tryAdd(board, pos, d[0] + pos.getRow(), d[1] + pos.getColumn(), moves);
        return moves;
    }
    private Collection<ChessMove> bishopMove(ChessBoard board, ChessPosition pos){
        int[][] directions = {
                {1,1}, {-1, 1}, {1, -1}, {-1, -1}
        };
        Collection<ChessMove> moves = new ArrayList<>();
        addSilding(board, pos, directions, moves);
        return moves;
    }
    private Collection<ChessMove> knightMove(ChessBoard board, ChessPosition pos){
        int[][] directions = {
                {2, 1}, {2,-1}, {-2, 1}, {-2, -1},
                {1,2}, {-1, 2}, {1, -2}, {-1, -2}
        };
        Collection<ChessMove> moves = new ArrayList<>();
        for(int [] d : directions)
            tryAdd(board, pos, d[0] + pos.getRow(), d[1] + pos.getColumn(), moves);
        return moves;
    }
    private Collection<ChessMove> rookMove(ChessBoard board, ChessPosition pos){
        int[][] directions = {
                {1, 0}, {0,1}, {-1, 0}, {0, -1}
        };
        Collection<ChessMove> moves = new ArrayList<>();
        addSilding(board, pos, directions, moves);
        return moves;
    }
    private Collection<ChessMove> pawnMove(ChessBoard board, ChessPosition pos){
        Collection<ChessMove> moves = new ArrayList<>();

        int dir = (teamColor == ChessGame.TeamColor.WHITE)? 1 : -1;
        int startRow = (teamColor == ChessGame.TeamColor.WHITE)? 2 : 7;
        int promotionRow = (teamColor == ChessGame.TeamColor.WHITE)? 8: 1;

        int row = pos.getRow();
        int col = pos.getColumn();

        int forwardRow = row + dir;

        if(forwardRow>=1 && forwardRow <=8){
            ChessPosition forward = new ChessPosition(forwardRow, col);
            if(board.getPiece(forward) == null) {
                if (forwardRow == promotionRow) {
                    addPromotionMoves(moves, pos, forward);
                } else {
                    moves.add(new ChessMove(pos, forward, null));
                }

                if (row == startRow) {
                    ChessPosition twoUp = new ChessPosition(row + 2 * dir, col);
                    if (board.getPiece(twoUp) == null) {
                        moves.add(new ChessMove(pos, twoUp, null));
                    }
                }
            }
        }

        int [] captureSquares = {col - 1, col + 1};
        for(int capture : captureSquares){
            if(capture < 1 || capture > 8)
                continue;
            ChessPosition diag = new ChessPosition(forwardRow, capture);
            ChessPiece target = board.getPiece(diag);
            if(target != null && target.getTeamColor() != this.teamColor){
                if(forwardRow == promotionRow)
                    addPromotionMoves(moves, pos, diag);
                else
                    moves.add(new ChessMove(pos, diag, null));
            }
        }
        return moves;
    }

    private void tryAdd(ChessBoard board, ChessPosition from, int row, int col, Collection<ChessMove> moves){
        if(row<1 || row>8 || col<1 || col>8)
            return;
        ChessPosition to = new ChessPosition(row, col);
        ChessPiece target = board.getPiece(to);
        if(target == null || target.getTeamColor() != this.teamColor){
            moves.add(new ChessMove(from, to, null));
        }
    }
    private void addSilding(ChessBoard board, ChessPosition from, int[][] directions, Collection<ChessMove> moves){
        for(int[] dir : directions){
            int row = from.getRow();
            int col = from.getColumn();
            while(true){
                row = row + dir[0];
                col = col +dir[1];
                if(row < 1 || row > 8 || col < 1 || col > 8)
                    break;
                ChessPosition to = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(to);

                if(target == null)
                    moves.add(new ChessMove(from, to, null));
                else {
                    if(target.getTeamColor() != this.getTeamColor())
                        moves.add(new ChessMove(from, to, null));
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
}