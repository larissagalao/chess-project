package boardgame;

public class Piece {

    protected Position position;
    private  Board board;

    public Piece(Board board) {
    }

    protected Board getBoard() { //apenas classes dentro do pacote boardgame e subclasses de Piece podem acessar
        return board;
    }
}
