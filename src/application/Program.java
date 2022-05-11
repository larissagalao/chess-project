package application;

import chess.ChessException;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Program {

    public static  void main(String[] args){

        Scanner sc = new Scanner(System.in);
        ChessMatch c = new ChessMatch();
        List<ChessPiece> capturedPieces = new ArrayList<>();

        while (!c.getCheckMate()){

            try{

                UI.clearScreen();
                UI.printMatch(c, capturedPieces);
                System.out.println();
                System.out.print("Source: ");
                ChessPosition s = UI.readPosition(sc); // s -> initial position

                boolean[][] possibleMoves = c.possibleMovesMatriz(s);
                UI.printPossibleBoard(c.getPieces(), possibleMoves);

                System.out.println();
                System.out.print("Target: ");
                ChessPosition t = UI.readPosition(sc); // t -> final position

                ChessPiece captured = c.performChessMove(s, t);

                if(captured != null){
                    capturedPieces.add(captured);
                }

                if (c.getPromoted() != null) {
                    System.out.print("Enter piece for promotion (B/N/R/Q): ");
                    String type = sc.nextLine().toUpperCase();
                    while (!type.equals("B") && !type.equals("N") && !type.equals("R") & !type.equals("Q")) {
                        System.out.print("Invalid value! Enter piece for promotion (B/N/R/Q): ");
                        type = sc.nextLine().toUpperCase();
                    }
                    c.replacePromotedPiece(type);
                }

            }catch (ChessException e1){
                System.out.println(e1.getMessage());
                sc.nextLine();
            }catch (InputMismatchException e2){
                System.out.println(e2.getMessage());
                sc.nextLine();
            }
        }
        UI.clearScreen();
        UI.printMatch(c, capturedPieces);


    }
}
