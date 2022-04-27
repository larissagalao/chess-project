package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.Rook;

public class ChessMatch {

    private Board board1;

    public ChessMatch() {

        board1 = new Board(8,8);
        initialSetup();

    }

    public ChessPiece[][] getPieces(){
        ChessPiece[][] m = new ChessPiece[board1.getRows()][board1.getColumns()];
        for (int i=0; i< board1.getRows(); i++){
            for(int j=0; j< board1.getColumns(); j++){
                m[i][j] = (ChessPiece) board1.pieceReturn(i, j); //cast pra ter o retorno correto da func
            }
        }
        return m;
    }

    private void placeNewPiece(char column, int row, ChessPiece piece){
        board1.placePiece(piece, new ChessPosition(column, row).toPosition());
    }

    public ChessPiece performChessMove(ChessPosition start, ChessPosition end){
        Position s = start.toPosition();
        Position e = end.toPosition();
        validateSourcePosition(s);
        validateTargetPosition(s, e);
        Piece capturedPiece = makeMove(s, e);
        return (ChessPiece)capturedPiece;
    }

    public boolean[][] possibleMovesMatriz(ChessPosition sourcePosition){
        Position position = sourcePosition.toPosition();
        validateSourcePosition(position);
        return board1.pieceReturnPosition(position).possibleMoves();
    }

    private void validateTargetPosition(Position s, Position e){
       if(!board1.pieceReturnPosition(s).possibleMove(e)){
           throw new ChessException("This move is not allowed");
       }
    }

    private void validateSourcePosition(Position p){
        if(!board1.thereIsAPiece(p)){
            throw new ChessException("There is no piece on this position");
        }
        if (!board1.pieceReturn(p.getRow(), p.getColumn()).isThereAnyPossibleMove()) {
            throw new ChessException("There is no possible move for the chosen piece");
        }
    }

    private Piece makeMove(Position start, Position end){
        Piece p = board1.removePiece(start);
        Piece capturedPiece = board1.removePiece(end);
        board1.placePiece(p, end);
        return  (ChessPiece)capturedPiece;
    }

    private void initialSetup(){
        placeNewPiece('a', 1, new Rook(board1, Color.WHITE));
        placeNewPiece('c', 1, new Rook(board1, Color.BLACK));
    }
}
