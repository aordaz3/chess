package chess;

import java.util.Collection;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn;
    private ChessBoard board;
    private ChessPosition blackKing;
    private ChessPosition whiteKing;

    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        board = new ChessBoard();
        blackKing = new ChessPosition(8, 5);
        whiteKing = new ChessPosition(1, 5);
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
        if(moving == null || this.isInCheck(moving.getTeamColor()))
            return null;
        Collection<ChessMove> allMoves = moving.pieceMoves(board, startPosition);
        return allMoves;
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
            moving = new ChessPiece(this.teamTurn, move.getPromotionPiece());
        }
        if (moving.getPieceType() == ChessPiece.PieceType.KING){
            if (moving.getTeamColor() == TeamColor.WHITE) {
                whiteKing = end;
            } else {
                blackKing = end;
            }
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
        ChessPosition kingPos = (teamColor == TeamColor.WHITE) ? whiteKing : blackKing;
        TeamColor enemy = (teamColor == TeamColor.WHITE)? TeamColor.BLACK : TeamColor.WHITE
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

    private ChessPosition getKingPos(TeamColor teamColor) {
        for(int r = 1; r <= 8; r++){
            for(int c = 1; c <= 8; c++){
                ChessPiece cur = board.getPiece(new ChessPosition(r, c));
                if(cur.getTeamColor() == this.teamTurn && cur.getPieceType() == ChessPiece.PieceType.KING){
                    return new ChessPosition(r, c);
                }
            }
        }
        return null;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        boolean isCheck = isInCheck(teamColor);
        ChessPosition kingPos = (teamColor == TeamColor.WHITE)? whiteKing : blackKing;
        boolean canMove = validMoves(kingPos) != null;
        return isCheck && !canMove;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        //if is not in check and vailid moves == null return true
        return false;
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
