package boardgame;

public class Board {

    private int rows;
    private int columns;
    private  Piece[][] pieces;

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public Board(int rows, int columns) {
        if (rows < 1 || columns < 1){
            throw new BoardException("Error creating board! Rows and columns must be >= 1 ");
        }
        this.rows = rows;
        this.columns = columns;
        pieces = new Piece[rows][columns];

    }

    public Piece pieceReturn(int row, int column){
        if (!positionIsValid(row, column)){
            throw new BoardException("Position not on the board!");
        }
        return pieces[row][column];
    }

    public Piece pieceReturnPosition(Position position){
        return pieces[position.getRow()][position.getColumn()];
    }

    public void placePiece(Piece piece, Position position){
        if(thereIsAPiece(position)){
            throw new BoardException("There is already a piece on position " + position);
        }

        pieces[position.getRow()][position.getColumn()] = piece;
        piece.position = position;
    }

    private Boolean positionIsValid(int row, int column){
        return row >=0 && row < rows && column >=0 && column < columns;
    }

    public Boolean positionExists(Position position){
        return positionIsValid(position.getRow(), getColumns());
    }

    public Boolean thereIsAPiece(Position position){

        if (!positionExists(position)){
            throw new BoardException("Position not on the board!");
        }
        return pieceReturnPosition(position) != null;
    }

}
