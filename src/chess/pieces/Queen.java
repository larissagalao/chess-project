package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Queen extends ChessPiece {


    public Queen(Board board, Color color) {
        super(board, color);
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];

        Position p = new Position(0,0);
        Position q = new Position(0,0);
        q.setValues(position.getRow(), position.getColumn());



        //up-left

        p.setValues(position.getRow() -1, position.getColumn()-1);
        while(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)){
            mat[p.getRow()][p.getColumn()] = true;
            p.setValues(p.getRow() - 1, p.getColumn()-1);
        }
        if(getBoard().positionExists(p) && getBoard().thereIsAPiece(p) && returnColor(p) != returnColor(q)){
            mat[p.getRow()][p.getColumn()] = true;
        }

        //up-right

        p.setValues(position.getRow() - 1, position.getColumn()+1);
        while(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)){
            mat[p.getRow()][p.getColumn()] = true;
            p.setValues(p.getRow() - 1, p.getColumn() +1);
        }
        if(getBoard().positionExists(p) && getBoard().thereIsAPiece(p) &&  returnColor(p)!=returnColor(q)){
            mat[p.getRow()][p.getColumn()] = true;
        }

        //down-right

        p.setValues(position.getRow()+1, position.getColumn() + 1 );
        while(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)){
            mat[p.getRow()][p.getColumn()] = true;
            p.setValues(p.getRow()+1, p.getColumn()+1);
        }
        if(getBoard().positionExists(p) && getBoard().thereIsAPiece(p) && returnColor(p)!=returnColor(q)){
            mat[p.getRow()][p.getColumn()] = true;
        }

        //down-left

        p.setValues(position.getRow()+1, position.getColumn() - 1 );
        while(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)){
            mat[p.getRow()][p.getColumn()] = true;
            p.setValues(p.getRow()+1, p.getColumn()-1);
        }
        if(getBoard().positionExists(p) && getBoard().thereIsAPiece(p) && returnColor(p)!=returnColor(q)){
            mat[p.getRow()][p.getColumn()] = true;
        }

        //up

        p.setValues(position.getRow() -1, position.getColumn());
        while(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)){
            mat[p.getRow()][p.getColumn()] = true;
            p.setRow(p.getRow() - 1);
        }
        if(getBoard().positionExists(p) && getBoard().thereIsAPiece(p) && returnColor(p) != returnColor(q)){
            mat[p.getRow()][p.getColumn()] = true;
        }

        //down

        p.setValues(position.getRow() + 1, position.getColumn());
        while(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)){
            mat[p.getRow()][p.getColumn()] = true;
            p.setRow(p.getRow() + 1);
        }
        if(getBoard().positionExists(p) && getBoard().thereIsAPiece(p) &&  returnColor(p)!=returnColor(q)){
            mat[p.getRow()][p.getColumn()] = true;
        }

        //left

        p.setValues(position.getRow(), position.getColumn() - 1 );
        while(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)){
            mat[p.getRow()][p.getColumn()] = true;
            p.setColumn(p.getColumn() - 1);
        }
        if(getBoard().positionExists(p) && getBoard().thereIsAPiece(p) && returnColor(p)!=returnColor(q)){
            mat[p.getRow()][p.getColumn()] = true;
        }

        //right

        p.setValues(position.getRow(), position.getColumn() + 1 );
        while(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)){
            mat[p.getRow()][p.getColumn()] = true;
            p.setColumn(p.getColumn() + 1);
        }
        if(getBoard().positionExists(p) && getBoard().thereIsAPiece(p) && returnColor(p)!=returnColor(q)){
            mat[p.getRow()][p.getColumn()] = true;
        }

        return mat;
    }

    public Color returnColor(Position p){
        ChessPiece chessPiece = (ChessPiece)getBoard().pieceReturnPosition(p);
        Color color = chessPiece.getColor();

        return color;
    }

    @Override
    public String toString() {
        return "Q";
    }
}
