package chess;

import boardgame.Board;
import boardgame.Position;
import chess.pieces.Rook;

public class ChessMatch {

    private Board board;

    public ChessMatch() {

        board = new Board(8,8);
        initialSetup();

    }

    public ChessPiece[][] getPieces(){
        ChessPiece[][] m = new ChessPiece[board.getRows()][board.getColumns()];
        for (int i=0; i< board.getRows(); i++){
            for(int j=0; j< board.getColumns(); j++){
                m[i][j] = (ChessPiece) board.pieceReturn(i, j); //cast pra ter o retorno correto da func
            }
        }
        return m;
    }

    private void initialSetup(){
        board.placePiece(new Rook(board, Color.WHITE), new Position(2,1));
    }
}
