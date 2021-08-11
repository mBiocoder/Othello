import szte.mi.Move;

import java.util.HashSet;

public class OthelloCMD {
    private IPlayer player1, player2;
    private IPlayer lastPlayer;

    public static void main(String[] args){

        OthelloCMD othello = new OthelloCMD();
        Board b = new Board();

        System.out.println("Welcome to a game of Othello!");
        System.out.println("Gamestate: \nBlack: 2"+" White: 2");
        System.out.println();
        try {
            othello.playOthello(b);
        }catch (Exception e){
            System.out.println("Fehler!");
        }

    }


    public void playOthello(Board b){
        System.out.println("Black Moves first");

        int result;

        b.displayBoard(b.getBoard());

        while(true){
            if (lastPlayer == player1)
            {
                lastPlayer = player2; //Switching players
            } else {
                lastPlayer = player1; //Switching players
            }

            HashSet<PlayerMove> blackPlaceableLocations = b.getPlaceableLocations('B', 'W');
            HashSet<PlayerMove> whitePlaceableLocations = b.getPlaceableLocations('W', 'B');

            result = b.checkForWin(whitePlaceableLocations, blackPlaceableLocations);

            b.playGame();

            if(result == 0){
                System.out.println("Game over.It is a draw.");
                break;
            }
            else if(result==1){
                System.out.println("Game over.White wins: "+b.getWScore()+":"+b.getBScore());
                break;
            }
            else if(result==-1){
                System.out.println("Game over.Black wins: "+b.getBScore()+":"+b.getWScore());
                break;
            }


        }
    }
}


