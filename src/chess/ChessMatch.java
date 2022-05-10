package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

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

    public ChessMatch() {

        board1 = new Board(8,8);
        turn = 1;
        currentPlayer = Color.WHITE;
        check = false;
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
                System.out.println("true");

                return true;
            }
        }
        System.out.println("false");

        return false;
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

    public ChessPiece performChessMove(ChessPosition start, ChessPosition end){
        Position s = start.toPosition();
        Position e = end.toPosition();
        validateSourcePosition(s);
        validateTargetPosition(s, e);
        Piece capturedPiece = makeMove(s, e);

        if(testCheck(currentPlayer)){
            undoMove(s, e, capturedPiece);
            throw new ChessException("You can't put yourself in check");
        }
        check = (testCheck(opponent(currentPlayer))) ? true : false;

        nextTurn();
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
        if(currentPlayer != ((ChessPiece)board1.pieceReturnPosition(p)).getColor()){
            throw new ChessException("The chosen piece is not yours");
        }

        if (!board1.pieceReturn(p.getRow(), p.getColumn()).isThereAnyPossibleMove()) {
            throw new ChessException("There is no possible move for the chosen piece");
        }
    }

    private Piece makeMove(Position start, Position end){
        Piece p = board1.removePiece(start);
        Piece capturedPiece = board1.removePiece(end);
        board1.placePiece(p, end);

        if(capturedPiece != null){
            piecesOn.remove(capturedPiece);
            piecesOff.add(capturedPiece);
        }

        return  (ChessPiece)capturedPiece;
    }

    private void undoMove(Position start, Position end, Piece captured){
        Piece p = board1.removePiece(end);
        board1.placePiece(p, start);

        if(captured != null){
            board1.placePiece(captured, end);
            piecesOff.remove(captured);
            piecesOn.add(captured);
        }

    }

    private void initialSetup(){
        placeNewPiece('a', 1, new Rook(board1, Color.WHITE));
        placeNewPiece('c', 1, new Rook(board1, Color.BLACK));
        placeNewPiece('c', 4, new King(board1, Color.BLACK));
        placeNewPiece('h', 4, new King(board1, Color.WHITE));

    }
}
