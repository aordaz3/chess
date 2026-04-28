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
    //move straight by one, forward if white, down if black
    //move up 2 only on first turn
    //empacant?
    //promotion?
    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition pos) {
        int r = pos.getRow();
        int c = pos.getColumn();
        Collection<ChessMove> moves = new ArrayList<>();

        if (this.color == ChessGame.TeamColor.WHITE) {
            int upOne = r + 1;
            int upTwo = r + 2;

            // Move forward one
            if (upOne <= 8) {
                ChessPosition posOne = new ChessPosition(upOne, c);
                if (board.getPiece(posOne) == null) {
                    // If it hits the end, add all 4 promotions
                    if (upOne == 8) {
                        addPromotionMoves(moves, pos, posOne);
                    }
                    else {
                        moves.add(new ChessMove(pos, posOne, null));
                    }

                    // Move forward two (only if on start row and nothing is blocking)
                    if (r == 2) {
                        ChessPosition posTwo = new ChessPosition(upTwo, c);
                        if (board.getPiece(posTwo) == null) {
                            moves.add(new ChessMove(pos, posTwo, null));
                        }
                    }
                }
            }
            // White Eats
            // Left
            if (c > 1) {
                ChessPosition leftTarget = new ChessPosition(upOne, c - 1);
                ChessPiece piece = board.getPiece(leftTarget);
                if (piece != null && piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                    if (upOne == 8) addPromotionMoves(moves, pos, leftTarget);
                    else moves.add(new ChessMove(pos, leftTarget, null));
                }
            }
            // Right
            if (c < 8) {
                ChessPosition rightTarget = new ChessPosition(upOne, c + 1);
                ChessPiece piece = board.getPiece(rightTarget);
                if (piece != null && piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                    if (upOne == 8) addPromotionMoves(moves, pos, rightTarget);
                    else moves.add(new ChessMove(pos, rightTarget, null));
                }
            }
        }
        //BLACK
        else {
            int downOne = r - 1;
            int downTwo = r - 2;

            // Move forward one
            if (downOne >= 1) {
                ChessPosition posOne = new ChessPosition(downOne, c);
                if (board.getPiece(posOne) == null) {
                    if (downOne == 1) {
                        addPromotionMoves(moves, pos, posOne);
                    } else {
                        moves.add(new ChessMove(pos, posOne, null));
                    }

                    // Move forward two
                    if (r == 7) {
                        ChessPosition posTwo = new ChessPosition(downTwo, c);
                        if (board.getPiece(posTwo) == null) {
                            moves.add(new ChessMove(pos, posTwo, null));
                        }
                    }
                }
            }
            // Left
            if (c > 1) {
                ChessPosition leftTarget = new ChessPosition(downOne, c - 1);
                ChessPiece piece = board.getPiece(leftTarget);
                if (piece != null && piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    if (downOne == 1) addPromotionMoves(moves, pos, leftTarget);
                    else moves.add(new ChessMove(pos, leftTarget, null));
                }
            }
            // Right
            if (c < 8) {
                ChessPosition rightTarget = new ChessPosition(downOne, c + 1);
                ChessPiece piece = board.getPiece(rightTarget);
                if (piece != null && piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    if (downOne == 1) addPromotionMoves(moves, pos, rightTarget);
                    else moves.add(new ChessMove(pos, rightTarget, null));
                }
            }
        }
        return moves;
    }

    private void addPromotionMoves(Collection<ChessMove> moves, ChessPosition from, ChessPosition to) {
        moves.add(new ChessMove(from, to, ChessPiece.PieceType.QUEEN));
        moves.add(new ChessMove(from, to, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(from, to, ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(from, to, ChessPiece.PieceType.KNIGHT));
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
    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition pos) {
        //up/down/left/right 2
        //then check over 1
        int r = pos.getRow();
        int c = pos.getColumn();
        Collection<ChessMove> moves = new ArrayList<>();

        int up = r + 2;
        int down = r - 2;
        int left = c - 1;
        int right = c + 1;

        // UP DOWN
        if (up <= 8) {
            if (left >= 1) addKnightMove(board, pos, new ChessPosition(up, left), moves);
            if (right <= 8) addKnightMove(board, pos, new ChessPosition(up, right), moves);
        }
        if (down >= 1) {
            if (left >= 1) addKnightMove(board, pos, new ChessPosition(down, left), moves);
            if (right <= 8) addKnightMove(board, pos, new ChessPosition(down, right), moves);
        }

        // LEFT RIGHT
        up = r + 1;
        down = r - 1;
        left = c - 2;
        right = c + 2;

        if (left >= 1) {
            if (up <= 8) addKnightMove(board, pos, new ChessPosition(up, left), moves);
            if (down >= 1) addKnightMove(board, pos, new ChessPosition(down, left), moves);
        }
        if (right <= 8) {
            if (up <= 8) addKnightMove(board, pos, new ChessPosition(up, right), moves);
            if (down >= 1) addKnightMove(board, pos, new ChessPosition(down, right), moves);
        }

        return moves;
    }
    private void addKnightMove(ChessBoard board, ChessPosition start, ChessPosition end, Collection<ChessMove> moves) {
        ChessPiece targetPiece = board.getPiece(end);
        if (targetPiece == null || targetPiece.getTeamColor() != this.color) {
            moves.add(new ChessMove(start, end, null));
        }
    }
    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition pos){
        int r = pos.getRow();
        int c = pos.getColumn();
        Collection<ChessMove> moves = new ArrayList<>();
        //move left up
        int left = c - 1;
        int up = r + 1;
        while(left>=1 && up <=8) {
            ChessPosition leftUp = new ChessPosition(up, left);
            //nithing there
            if (board.getPiece(leftUp) == null){
                ChessMove move = new ChessMove(pos, leftUp, null);
                moves.add(move);
            }
            //mean guy there
            else if (board.getPiece(leftUp).color != this.color) {
                ChessMove move = new ChessMove(pos, leftUp, null);
                moves.add(move);
                break;
            }
            //friend there
            else{
                break;
            }
            left--;
            up++;
        }
        //move right up
        int right = c + 1;
        up = r + 1;
        while(right <= 8 && up <= 8) {
            ChessPosition rightUp = new ChessPosition(up, right);
            //nithing there
            if (board.getPiece(rightUp) == null){
                ChessMove move = new ChessMove(pos, rightUp, null);
                moves.add(move);
            }
            //mean guy there
            else if (board.getPiece(rightUp).color != this.color) {
                ChessMove move = new ChessMove(pos, rightUp, null);
                moves.add(move);
                break;
            }
            //friend there
            else{
                break;
            }
            right++;
            up++;
        }
        //move left down
        left = c - 1;
        int down = r - 1;
        while(left>=1 && down >= 1) {
            ChessPosition leftDown = new ChessPosition(down, left);
            //nithing there
            if (board.getPiece(leftDown) == null){
                ChessMove move = new ChessMove(pos, leftDown, null);
                moves.add(move);
            }
            //mean guy there
            else if (board.getPiece(leftDown).color != this.color) {
                ChessMove move = new ChessMove(pos, leftDown, null);
                moves.add(move);
                break;
            }
            //friend there
            else{
                break;
            }
            left--;
            down--;
        }
        //move right down
        right = c + 1;
        down = r - 1;
        while(right <=8  && down >=1) {
            ChessPosition rightDown = new ChessPosition(down, right);
            //nithing there
            if (board.getPiece(rightDown) == null){
                ChessMove move = new ChessMove(pos, rightDown, null);
                moves.add(move);
            }
            //mean guy there
            else if (board.getPiece(rightDown).color != this.color) {
                ChessMove move = new ChessMove(pos, rightDown, null);
                moves.add(move);
                break;
            }
            //friend there
            else{
                break;
            }
            right++;
            down --;
        }
        return moves;
    }
    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition pos){
        Collection<ChessMove> bishopMoves = new ArrayList<>();
        Collection<ChessMove> rookMoves = new ArrayList<>();
        Collection<ChessMove> moves = new ArrayList<>();
        bishopMoves = bishopMoves(board, pos);
        rookMoves = rookMoves(board, pos);
        moves.addAll(bishopMoves);
        moves.addAll(rookMoves);
        return moves;
    }
    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition pos){
        int r = pos.getRow();
        int c = pos.getColumn();
        Collection<ChessMove> moves = new ArrayList<>();
        int up = r + 1;
        int down = r - 1;
        int left = c - 1;
        int right = c+ 1;
        //up
        if(up <= 8){
            ChessPosition overOne = new ChessPosition(up, c);
            if (board.getPiece(overOne) == null || board.getPiece(overOne).color != this.color) {
                moves.add(new ChessMove(pos, overOne, null));
            }
        }
        //down
        if(down >= 1){
            ChessPosition overOne = new ChessPosition(down, c);
            if (board.getPiece(overOne) == null || board.getPiece(overOne).color != this.color) {
                moves.add(new ChessMove(pos, overOne, null));
            }
        }
        //left
        if(left >= 1){
            ChessPosition overOne = new ChessPosition(r, left);
            if (board.getPiece(overOne) == null || board.getPiece(overOne).color != this.color) {
                moves.add(new ChessMove(pos, overOne, null));
            }
        }
        //right
        if(right <= 8){
            ChessPosition overOne = new ChessPosition(r, right);
            if (board.getPiece(overOne) == null || board.getPiece(overOne).color != this.color) {
                moves.add(new ChessMove(pos, overOne, null));
            }
        }
        //up left
        if(left >= 1 && up <= 8){
            ChessPosition overOne = new ChessPosition(up, left);
            if (board.getPiece(overOne) == null || board.getPiece(overOne).color != this.color) {
                moves.add(new ChessMove(pos, overOne, null));
            }
        }
        //up right
        if(right <=8 && up <= 8){
            ChessPosition overOne = new ChessPosition(up, right);
            if (board.getPiece(overOne) == null || board.getPiece(overOne).color != this.color) {
                moves.add(new ChessMove(pos, overOne, null));
            }
        }
        //down left
        if(left >= 1 && down >= 1){
            ChessPosition overOne = new ChessPosition(down, left);
            if (board.getPiece(overOne) == null || board.getPiece(overOne).color != this.color) {
                moves.add(new ChessMove(pos, overOne, null));
            }
        }
        //down right
        if(right <= 8 && down >= 1){
            ChessPosition overOne = new ChessPosition(down, right);
            if (board.getPiece(overOne) == null || board.getPiece(overOne).color != this.color) {
                moves.add(new ChessMove(pos, overOne, null));
            }
        }
        return moves;
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
