package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChessMatch {

    private Board board1;
    private int turn;
    private Color currentPlayer;
    private List<Piece> piecesOn = new ArrayList();
    private List<Piece> piecesOff = new ArrayList();
    private Boolean check;
    private Boolean checkMate;


    public ChessMatch() {

        board1 = new Board(8,8);
        turn = 1;
        currentPlayer = Color.WHITE;
        check = false;
        checkMate = false;
        initialSetup();

    }

    public int getTurn(){
        return turn;
    }

    public Color getCurrentPlayer(){
        return currentPlayer;
    }

    private void nextTurn(){
        turn++;
        currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    private Color opponent(Color color){
        return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    public boolean getCheck(){
        return check;
    }

    public boolean getCheckMate(){
        return checkMate;
    }

    private ChessPiece kingColor(Color color){
        List<Piece> list = piecesOn.stream().filter(x ->((ChessPiece)x).getColor() == color).collect(Collectors.toList());
        for (Piece p : list){
            if(p instanceof King){
                return (ChessPiece)p;
            }
        }
        throw new IllegalStateException("There is no " + color + "king on the board");
    }

    private boolean testCheck(Color color){
        Position kingPosition = kingColor(color).getChessPosition().toPosition();
        List<Piece> opponentPiece = piecesOn.stream().filter(x ->((ChessPiece)x).getColor() == opponent(color)).collect(Collectors.toList());
        for(Piece p : opponentPiece){
            boolean[][] mat = p.possibleMoves();
            if(mat[kingPosition.getRow()][kingPosition.getColumn()]){
                return true;
            }
        }
        return false;
    }

    private boolean testCheckMate(Color color){
       if(!testCheck(color)){
           return false;
       }
        List<Piece> list = piecesOn.stream().filter(x ->((ChessPiece)x).getColor() == color).collect(Collectors.toList());
        for(Piece p : list){
            boolean[][] mat = p.possibleMoves();
            for(int i=0; i< board1.getRows(); i++){
                for(int j=0; j < board1.getColumns(); j++){
                    if(mat[i][j]){
                        Position source = ((ChessPiece)p).getChessPosition().toPosition();
                        Position target = new Position(i,j);
                        Piece captured = makeMove(source, target);
                        boolean testCheck = testCheck(color);
                        undoMove(source, target, captured);
                        if(!testCheck){
                            return false;
                        }
                    }
                }
            }
        }
        return true;
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
        piecesOn.add(piece);
    }

    public ChessPiece performChessMove(ChessPosition start, ChessPosition end) {
        Position s = start.toPosition();
        Position e = end.toPosition();
        validateSourcePosition(s);
        validateTargetPosition(s, e);
        Piece capturedPiece = makeMove(s, e);

        if (testCheck(currentPlayer)) {
            undoMove(s, e, capturedPiece);
            throw new ChessException("You can't put yourself in check");
        }
        check = (testCheck(opponent(currentPlayer))) ? true : false;

        if (testCheckMate(opponent(currentPlayer))) {
            checkMate = true;
        } else {

            nextTurn();
        }
            return (ChessPiece) capturedPiece;
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
        if(currentPlayer != ((ChessPiece)board1.pieceReturnPosition(p)).getColor()){
            throw new ChessException("The chosen piece is not yours");
        }

        if (!board1.pieceReturn(p.getRow(), p.getColumn()).isThereAnyPossibleMove()) {
            throw new ChessException("There is no possible move for the chosen piece");
        }
    }

    private Piece makeMove(Position start, Position end){
        ChessPiece p = (ChessPiece) board1.removePiece(start);
        p.increaseMoveCount();
        Piece capturedPiece = board1.removePiece(end);
        board1.placePiece(p, end);

        if(capturedPiece != null){
            piecesOn.remove(capturedPiece);
            piecesOff.add(capturedPiece);
        }

        return  (ChessPiece)capturedPiece;
    }

    private void undoMove(Position start, Position end, Piece captured){
        ChessPiece p = (ChessPiece) board1.removePiece(end);
        p.decreaseMoveCount();
        board1.placePiece(p, start);

        if(captured != null){
            board1.placePiece(captured, end);
            piecesOff.remove(captured);
            piecesOn.add(captured);
        }

    }

    private void initialSetup(){
      /*  placeNewPiece('h', 7, new Rook(board1, Color.WHITE));
        placeNewPiece('d', 1, new Rook(board1, Color.WHITE));
        placeNewPiece('e', 1, new King(board1, Color.WHITE));
        placeNewPiece('e', 2, new Pawn(board1, Color.WHITE));
        placeNewPiece('f', 2, new Bishop(board1, Color.WHITE));
        placeNewPiece('h', 2, new Knight(board1, Color.WHITE));

        placeNewPiece('b', 8, new Rook(board1, Color.BLACK));*/
        placeNewPiece('e', 1, new King(board1, Color.WHITE));

        placeNewPiece('a', 8, new King(board1, Color.BLACK));
        placeNewPiece('e', 5, new Queen(board1, Color.BLACK));


    }
}
