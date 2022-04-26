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

    public Board(int rows, int columns) {       //construtor com argumentos | seta rows, columns e a matriz pieces
        if (rows < 1 || columns < 1){
            throw new BoardException("Error creating board! Rows and columns must be >= 1 ");
        }
        this.rows = rows;
        this.columns = columns;
        pieces = new Piece[rows][columns];

    }

    public Piece[][] getPieces(){
        return pieces;
    }

    private Boolean positionIsValid(int row, int column){
        return row >=0 && row < rows && column >=0 && column < columns;
    }

    public Piece pieceReturn(int row, int column){
        if (!positionIsValid(row, column)){
            throw new BoardException("Position is  not valid!");
        }
        return pieces[row][column];
    }

    public Piece pieceReturnPosition(Position position){
        return pieces[position.getRow()][position.getColumn()];
    }

    public Boolean positionExists(Position position){
        return positionIsValid(position.getRow(), position.getColumn());
    }

    public Boolean thereIsAPiece(Position position){
        if (!positionExists(position)){
            throw new BoardException("Position not on the board!");
        }else{
            return pieceReturnPosition(position) != null;
        }
    }

    public void placePiece(Piece piece, Position position){
        if(thereIsAPiece(position)){
            throw new BoardException("There is already a piece on position " + position);
        }
        pieces[position.getRow()][position.getColumn()] = piece;
        piece.position = position;
    }

    public Piece removePiece(Position position){
        if(!positionExists(position)){
            throw new BoardException("Position not on the board");
        }if(pieceReturnPosition(position) == null){
            return null;
        }
        Piece aux = pieceReturnPosition(position) ;
        aux.position = null;
        pieces[position.getRow()][position.getColumn()] = null;
        return aux;
    }
}
