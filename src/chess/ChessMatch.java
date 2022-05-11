package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.*;

import java.security.InvalidParameterException;
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
    private ChessPiece enPassantVulnerable;
    private ChessPiece promoted;


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

    public ChessPiece getEnPassantVulnerable(){
        return enPassantVulnerable;
    }

    public ChessPiece getPromoted(){
        return promoted;
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

        ChessPiece movedPiece = (ChessPiece) board1.pieceReturnPosition(end.toPosition());

        // special move promotion
        promoted = null;
        if (movedPiece instanceof Pawn) {
            System.out.println("aqui 1");
            if ((movedPiece.getColor() == Color.WHITE && e.getRow() == 0) || (movedPiece.getColor() == Color.BLACK && e.getRow() == 7)) {
                System.out.println("aqui 2");
                promoted = (ChessPiece)board1.pieceReturnPosition(end.toPosition());
                promoted = replacePromotedPiece("Q");
            }
        }

        check = (testCheck(opponent(currentPlayer))) ? true : false;

        if (testCheckMate(opponent(currentPlayer))) {
            checkMate = true;
        } else {
            nextTurn();
        }

        //special move en passant

        if (movedPiece instanceof Pawn && (end.getRow() == start.getRow() -2 || end.getRow() == start.getRow() + 2)){
            enPassantVulnerable = movedPiece;
        }else{
            enPassantVulnerable = null;
        }
        return (ChessPiece) capturedPiece;
    }

    public ChessPiece replacePromotedPiece(String type){
        if(promoted == null){
            throw new IllegalStateException("There is no piece to be promoted");
        }if(!type.equals("B") && !type.equals("N") && !type.equals("R") && !type.equals("Q")){
            throw new InvalidParameterException("Invalid type for promotion");
        }

        Position pos = promoted.getChessPosition().toPosition();
        Piece p = board1.removePiece(pos);
        piecesOn.remove(p);

        ChessPiece newPiece = newPiece(type, promoted.getColor());
        board1.placePiece(newPiece, pos);
        piecesOn.add(newPiece);

        return newPiece;

    }

    private ChessPiece newPiece(String type, Color color){
        if(type.equals("B")){
            return new Bishop(board1, color);
        }if(type.equals("N")){
            return new Knight(board1, color);
        }if(type.equals("Q")){
            return new Queen(board1, color);
        }
        return new Rook(board1, color);
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

        // castling kingside rook
        if (p instanceof King && end.getColumn() == start.getColumn() + 2) {
            Position sourceT = new Position(start.getRow(), start.getColumn() + 3);
            Position targetT = new Position(start.getRow(), start.getColumn() + 1);
            ChessPiece rook = (ChessPiece)board1.removePiece(sourceT);
            board1.placePiece(rook, targetT);
            rook.increaseMoveCount();
        }

        // castling queenside rook
        if (p instanceof King && end.getColumn() == start.getColumn() - 2) {
            Position sourceT = new Position(start.getRow(), start.getColumn() - 4);
            Position targetT = new Position(start.getRow(), start.getColumn() - 1);
            ChessPiece rook = (ChessPiece)board1.removePiece(sourceT);
            board1.placePiece(rook, targetT);
            rook.increaseMoveCount();
        }

        // en passant

        if(p instanceof  Pawn){
            if(start.getColumn() != end.getColumn() && capturedPiece == null){
                Position pawnPosition;
                if(p.getColor() == Color.WHITE){
                    pawnPosition = new Position(end.getRow() +1, end.getColumn());
                }else{
                    pawnPosition = new Position(end.getRow()-1, end.getColumn());
                }
                capturedPiece = board1.removePiece(pawnPosition);
                piecesOff.add(capturedPiece);
                piecesOn.remove(capturedPiece);
            }
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

        // castling kingside rook
        if (p instanceof King && end.getColumn() == end.getColumn() + 2) {
            Position sourceT = new Position(start.getRow(), start.getColumn() + 3);
            Position targetT = new Position(start.getRow(), start.getColumn() + 1);
            ChessPiece rook = (ChessPiece)board1.removePiece(targetT);
            board1.placePiece(rook, sourceT);
            rook.decreaseMoveCount();
        }

        // castling queenside rook
        if (p instanceof King && end.getColumn() == start.getColumn() - 2) {
            Position sourceT = new Position(start.getRow(), start.getColumn() - 4);
            Position targetT = new Position(start.getRow(), start.getColumn() - 1);
            ChessPiece rook = (ChessPiece)board1.removePiece(targetT);
            board1.placePiece(rook, sourceT);
            rook.increaseMoveCount();
        }

        // en passant

        if(p instanceof  Pawn){
            if(start.getColumn() != end.getColumn() && captured == enPassantVulnerable){
                ChessPiece pawn = (ChessPiece) board1.removePiece(end);
                Position pawnPosition;
                if(p.getColor() == Color.WHITE){
                    pawnPosition = new Position(3, end.getColumn());
                }else{
                    pawnPosition = new Position(4, end.getColumn());
                }
                board1.placePiece(pawn, pawnPosition);
            }
        }

    }

    private void initialSetup(){
        placeNewPiece('a', 1, new Rook(board1, Color.WHITE));
        placeNewPiece('b', 1, new Knight(board1, Color.WHITE));
        placeNewPiece('c', 1, new Bishop(board1, Color.WHITE));
        placeNewPiece('d', 1, new Queen(board1, Color.WHITE));
        placeNewPiece('e', 1, new King(board1, Color.WHITE, this));
        placeNewPiece('f', 1, new Bishop(board1, Color.WHITE));
        placeNewPiece('g', 1, new Knight(board1, Color.WHITE));
        placeNewPiece('h', 1, new Rook(board1, Color.WHITE));
        placeNewPiece('a', 2, new Pawn(board1, Color.WHITE, this));
        placeNewPiece('b', 2, new Pawn(board1, Color.WHITE, this));
        placeNewPiece('c', 2, new Pawn(board1, Color.WHITE, this));
        placeNewPiece('d', 2, new Pawn(board1, Color.WHITE, this));
        placeNewPiece('e', 2, new Pawn(board1, Color.WHITE, this));
        placeNewPiece('f', 2, new Pawn(board1, Color.WHITE, this));
        placeNewPiece('g', 2, new Pawn(board1, Color.WHITE, this));
        placeNewPiece('h', 2, new Pawn(board1, Color.WHITE, this));

        placeNewPiece('a', 8, new Rook(board1, Color.BLACK));
        placeNewPiece('b', 8, new Knight(board1, Color.BLACK));
        placeNewPiece('c', 8, new Bishop(board1, Color.BLACK));
        placeNewPiece('d', 8, new Queen(board1, Color.BLACK));
        placeNewPiece('e', 8, new King(board1, Color.BLACK, this));
        placeNewPiece('f', 8, new Bishop(board1, Color.BLACK));
        placeNewPiece('g', 8, new Knight(board1, Color.BLACK));
        placeNewPiece('h', 8, new Rook(board1, Color.BLACK));
        placeNewPiece('a', 7, new Pawn(board1, Color.BLACK, this));
        placeNewPiece('b', 7, new Pawn(board1, Color.BLACK, this));
        placeNewPiece('c', 7, new Pawn(board1, Color.BLACK, this));
        placeNewPiece('d', 7, new Pawn(board1, Color.BLACK, this));
        placeNewPiece('e', 7, new Pawn(board1, Color.BLACK, this));
        placeNewPiece('f', 7, new Pawn(board1, Color.BLACK, this));
        placeNewPiece('g', 7, new Pawn(board1, Color.BLACK, this));
        placeNewPiece('h', 7, new Pawn(board1, Color.BLACK, this));

    }
}
