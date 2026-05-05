package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn;
    private ChessBoard board;


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
    }

    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece moving = board.getPiece(startPosition);
        if(moving == null)
            return new ArrayList<>();

        Collection<ChessMove> allMoves = moving.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();

        for(ChessMove move : allMoves){
            if(moveIsValid(move)){
                validMoves.add(move);
            }
        }
        return validMoves;
    }

    private boolean moveIsValid(ChessMove move){
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece moving = board.getPiece(start);
        ChessPiece target = board.getPiece(end);
        TeamColor team = moving.getTeamColor();

        //test move
        board.addPiece(end, moving);
        board.addPiece(start, null);

        boolean bad = isInCheck(team);

        board.addPiece(start, moving);
        board.addPiece(end, target);

        return !bad;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece moving = board.getPiece(start);
        Collection<ChessMove> validMoves = validMoves(start);
        //check if piece exists and its their turn
        if (moving == null || moving.getTeamColor() != this.teamTurn) {
            throw new InvalidMoveException("Not your turn");
        }
        // check valid moves
        if (!validMoves.contains(move)) {
            throw new InvalidMoveException("Invalid move");
        }

        //if we passed execute move
        if (move.getPromotionPiece() != null) {
            moving = new ChessPiece(moving.getTeamColor(), move.getPromotionPiece());
        }

        board.addPiece(end, moving);
        board.addPiece(start, null);
        this.teamTurn = (this.teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = findKing(teamColor);
        if(kingPos == null){
            return false;
        }
        TeamColor enemy = (teamColor == TeamColor.WHITE)? TeamColor.BLACK : TeamColor.WHITE;
        //get all moves from opent see if they can land on king
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessPosition currentPos = new ChessPosition(r, c);
                ChessPiece piece = board.getPiece(currentPos);

                // check if piece exists and is mean
                if (piece != null && piece.getTeamColor() == enemy) {
                    // get its moves
                    Collection<ChessMove> enemyMoves = piece.pieceMoves(board, currentPos);
                    //check to see if it lines up
                    for (ChessMove move : enemyMoves) {
                        if (move.getEndPosition().equals(kingPos)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private ChessPosition findKing(TeamColor teamColor) {
        for(int r = 1; r <= 8; r++){
            for(int c = 1; c <= 8; c++){
                ChessPiece cur = board.getPiece(new ChessPosition(r, c));
                if(cur !=null && cur.getTeamColor() == teamColor && cur.getPieceType() == ChessPiece.PieceType.KING){
                    return new ChessPosition(r, c);
                }
            }
        }
        return null;
    }

    private boolean hasNoMoves(TeamColor team){
        for(int r = 1; r <= 8; r++){
            for (int c = 1; c<=8; c++){
                ChessPosition cur = new ChessPosition(r, c);
                ChessPiece onSqaure = board.getPiece(cur);
                //if our current square is on our team find its moves
                if(onSqaure != null && onSqaure.getTeamColor() == team){
                    //if we have moves left
                    if(!(validMoves(cur).isEmpty())){
                        return false;
                    }
                }
            }
        }
        return true;
    }
    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        //in CM if in check and no valid moves left
        if (!isInCheck(teamColor))
            return false;
        return hasNoMoves(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        //in SM if not in check and no moves left
        if(isInCheck(teamColor))
            return false;
        return hasNoMoves(teamColor);
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {

        return board;
    }
}
