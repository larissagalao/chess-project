package application;

import chess.ChessException;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Program {

    public static  void main(String[] args){

        Scanner sc = new Scanner(System.in);
        ChessMatch c = new ChessMatch();

        while (true){

            try{

                UI.clearScreen();
                UI.printMatch(c);
                System.out.println();
                System.out.print("Source: ");
                ChessPosition s = UI.readPosition(sc); // s -> initial position

                boolean[][] possibleMoves = c.possibleMovesMatriz(s);
                UI.printPossibleBoard(c.getPieces(), possibleMoves);

                System.out.println();
                System.out.print("Target: ");
                ChessPosition t = UI.readPosition(sc); // t -> final position

                ChessPiece captured = c.performChessMove(s, t);

            }catch (ChessException e1){
                System.out.println(e1.getMessage());
                sc.nextLine();
            }catch (InputMismatchException e2){
                System.out.println(e2.getMessage());
                sc.nextLine();
            }
        }

    }
}
