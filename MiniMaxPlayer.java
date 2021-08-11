import szte.mi.Move;
import szte.mi.Player;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

public class MiniMaxPlayer implements Player {
    //Attributes
    //Board1 (for gui) and board
    public Board board;
    public Board board1 = new Board();

    //Players
    protected char myColor;
    protected char oppColor;

    //Getters and setters
    public void setMyColor(char myColor) {
        this.myColor = myColor;
    }
    public void setOppColor(char oppColor) {
        this.oppColor = oppColor;
    }

    int passCounter = 0;

    //Constructor
    public MiniMaxPlayer() {
    }

    @Override
    public boolean equals(Object o) {
        return o.hashCode() == this.hashCode();
    }

    //methods
    //Initialize board and players
    @Override
    public void init(int order, long t, Random rnd) {
        board = new Board();
        board.init(); //this will reset the game
        passCounter = 0;

        if (order == 0) {
            myColor = Board.BLACK;
            this.oppColor = Board.WHITE;
        } else {
            if (order != 1) {
                throw new IllegalArgumentException();
            }
            this.myColor = Board.WHITE;
            this.oppColor = Board.BLACK;

        }
    }

    @Override
    public Move nextMove(Move prevMove, long tOpponent, long t) {
        if (prevMove != null ) {
            try {
                if (board instanceof Board){ //checking if instance of Board for the pure Minimax Game
                    board.setMove(prevMove.x, prevMove.y, oppColor, myColor, board.getBoard());
                    //board.displayBoard(board.getBoard());
                }
                else { //We are dealing with the GUI
                    board1.setMove(prevMove.x, prevMove.y, oppColor, myColor, board1.getBoard());
                    board1.displayBoard(board1.getBoard());
                }
            } catch (IllegalArgumentException e) {
                e.getCause();
            }
        } else {
            passCounter++;
        }
        Move receivedMove = minimaxDecision(); //calls minimaxDecision
        if (receivedMove != null) {
            try {
                if (board instanceof Board){
                    board.setMove(receivedMove.x, receivedMove.y, myColor, oppColor, board.getBoard());
                    board.displayBoard(board.getBoard());
                }else {
                    board1.setMove(receivedMove.x, receivedMove.y, myColor, oppColor, board1.getBoard());
                    board1.displayBoard(board1.getBoard());
                }
            } catch (IllegalArgumentException e) {
                e.getCause();
            }
        }
        return receivedMove;
    }

    //Decide on what is the best move and pass it on to nextMove
    //This method calls minimaxValue method and utility for all placeable locations
    public Move minimaxDecision() {
        System.out.println(board1.getPlaceableLocations(myColor, oppColor));

        int bestScore = Integer.MIN_VALUE;
        Move bestMove = null;
        try {
            if (board instanceof Board) {
                Iterator itr = board.getPlaceableLocations(myColor, oppColor).iterator();
                while (itr.hasNext()) {
                    Move m = (Move) itr.next();
                    char[][] newNode = deepCopyCharMatrix(board.getBoard(), m.x, m.y, myColor, oppColor);
                    int currentScore = minimaxValue(m.x, m.y, 3, true, newNode); //searchdepth is 3

                    if (currentScore >= bestScore) {
                        bestScore = currentScore;
                        bestMove = m;
                    }
                }
            } else { //for gui
                Iterator itr = board1.getPlaceableLocations(myColor, oppColor).iterator();
                while (itr.hasNext()) {
                    Move m = (Move) itr.next();
                    char[][] newNode = deepCopyCharMatrix(board1.getBoard(), m.x, m.y, myColor, oppColor);
                    int currentScore = minimaxValue(m.x, m.y, 4, true, newNode);

                    if (currentScore >= bestScore) {
                        bestScore = currentScore;
                        bestMove = m;
                    }
                }
            }
        } catch (Exception e) {
            e.getCause();
        }
        System.out.println(bestScore);
        return bestMove;
    }

    //Make a deepCopy of our gameboard
    public char[][] deepCopyCharMatrix(char[][] input, int x, int y, char player, char opponent) {
        if (input == null)
            return null;
        char[][] result = new char[input.length][];
        for (int r = 0; r < input.length; r++) {
            result[r] = input[r].clone();
        }
        if (board instanceof Board){
            board.setMove(x,y,player, opponent, result);
        }else { //for gui
            board1.setMove(x,y,player, opponent, result);
        }
        return result;
    }

    //MinimaxValue calls utility and is a recursive function and determines value of each move and returns this to minimaxDecision
 public int minimaxValue(int x, int y, int searchdepth, boolean maximizingPlayer, char[][] newNode) {
            if (searchdepth == 0) { //reached end of searchdepth
                return utility(newNode);
            }
            //If we cant make any moves pass or game is over
            if (getPlaceableLocations(myColor, oppColor,newNode).isEmpty()){
                    if (getPlaceableLocations(oppColor, myColor,newNode).isEmpty()){
                        return  utility(newNode);
                    }
                    else {
                        //System.out.println("We are passing to the opponent!");
                        return minimaxValue(x,y, searchdepth -1, false, newNode); //call minimax for the opponent
                    }
            }
            //if maximizing player is true...
            if (maximizingPlayer){
                int max = Integer.MIN_VALUE;
                 if (getPlaceableLocations(oppColor, myColor, newNode).isEmpty()){
                    return 0;
                }
                Iterator itr = getPlaceableLocations(oppColor, myColor, newNode).iterator();
                while(itr.hasNext()) {
                    Move m = (Move) itr.next();
                    char [][] newfield = deepCopyCharMatrix(newNode, m.x, m.y, oppColor, myColor);
                    int currentScore = minimaxValue(m.x,m.y, searchdepth - 1, false, newfield);
                    max = Math.max(currentScore, max);
                }
                return max;

            }
            else { //maximizing player is false
                int min = Integer.MAX_VALUE;
                if (getPlaceableLocations(myColor, oppColor, newNode).isEmpty()){
                    return 0;
                }

                Iterator itr = getPlaceableLocations(myColor, oppColor, newNode).iterator();
                while(itr.hasNext()) {
                    Move m = (Move) itr.next();
                    char [][] newfield = deepCopyCharMatrix(newNode, m.x, m.y, myColor, oppColor);
                    int currentScore = minimaxValue(m.x,m.y, searchdepth - 1, true, newfield);
                    min = Math.min(currentScore, min);
                }
                return min;
            }
    }

    //Get placeableLocations here
    public HashSet<PlayerMove> getPlaceableLocations(char player, char opponent, char [][] gameboard){
        HashSet<PlayerMove> placeablePositions = new HashSet<>();
        if (board instanceof Board){
            board.findPlaceableLocations(player, opponent, placeablePositions, gameboard);
        }else {
            board1.findPlaceableLocations(player, opponent, placeablePositions, gameboard);
        }
        return placeablePositions;
    }

    //Accounting for value of field locations as well as how many coins are in place
    /*public int utility(char [][] gamefield) {
        int rating = 0;
        int[][] eval_table = {
                {100, -28, 28, 26, 26, 28, -28, 100},
                {-28, -50, -24, -23, -23, -24, -50, -28},
                {28, -24, 27, 24, 24, 27, -24, 28},
                {26, -23, 24, 0, 0, 24, -23, 26},
                {26, -23, 24, 0, 0, 24, -23, 26},
                {28, -24, 27, 24, 24, 27, -24, 28},
                {-28, -50, -24, -23, -23, -24, -50, -28},
                {100, -28, 28, 26, 26, 28, -28, 100}
        };
        for (int x = 0; x < 8; x++)
            for (int y = 0; y < 8; y++) {
                if (gamefield[x][y] == myColor) {
                    rating = eval_table[x][y] + result;
                    //ratingMaxPlayer = ratingMaxPlayer + eval_table[x][y];
                    //rating++;
                }
            }
        return rating;
    }*/


    public int utility(char [][] gamefield) {
        //static board evaluation
        int ratingMaxPlayer = 0;
        int ratingMinPlayer = 0;
        int totalRating = 0;
        //For coinparity with coin count
        int myPlayer = board1.count(myColor);
        int otherPlayer = board1.count(oppColor);
        //int score = myPlayer - otherPlayer;
        int coinParityValue = 100 * (myPlayer - otherPlayer)/ (myPlayer + otherPlayer);

        int[][] eval_table = {
                {100,  -28,  28,  26,  26,  28,  -28, 100},
                {-28, -50, -24, -23, -23, -24, -50, -28},
                { 28,  -24,  27,  24,  24,  27,  -24, 28},
                { 26,  -23,  24,  0,  0,  24,  -23,  26},
                { 26,  -23,  24,  0,  0,  24,  -23,  26},
                { 28,  -24,  27,  24,  24,  27,  -24, 28},
                {-28, -50, -24, -23, -23, -24, -50, -28},
                {100,  -28,  28,  26,  26,  28,  -28, 100}
        };
        for (int x=0; x<8; x++) for (int y=0;y<8;y++)
        {
            if(gamefield[x][y] == myColor) {
                //rating = eval_table[x][y] + result;
                ratingMaxPlayer = ratingMaxPlayer + eval_table[x][y];
                //rating++;
            }
            else if (gamefield[x][y] == Board.EMPTY){
                continue;
            }
            else {
                ratingMinPlayer = ratingMinPlayer + eval_table[x][y];
            }
        }
        totalRating = ratingMaxPlayer - ratingMinPlayer;
        //System.out.println(totalRating);
        return totalRating;
    }


    /*public int utility(char [][] gameboard){
        int stablePos = 1;
        int semistablePos = 0;
        int unstablePos = -1;

    }
     */
    /*public int utility(char [][] gameboard){
        int stand = 0;
        for(int i=0; i<8; i++) {
            for (int j=0; j<8; j++) {
                if(gameboard[i][j] == Board.EMPTY) continue;
                if(gameboard[i][j] == myColor) {
                    //System.out.println("doinf for player!");
                    if((i==0 && j==0) || (i==0 && j==7) || (i==7 && j==0) || (i==7 && j==7)){
                        stand = stand + 5;
                    } else if((i==1 && j==1) || (i==1 && j==6) || (i==6 && j==1) || (i==6 && j==6)) {
                        stand = stand - 1;
                    } else if (i+j == 7 || i==j) {
                        stand = stand + 2;
                    } else if (i==0 || j==0 || i==7 || j==7) {
                        stand = stand + 3;
                    } else {
                        stand = stand + 1;
                    }
                }
                else {
                    //System.out.println("doing for opponent!");
                    if((i==0 && j==0) || (i==0 && j==7) || (i==7 && j==0) || (i==7 && j==7)){
                        stand = stand - 5;
                    } else if((i==1 && j==1) || (i==1 && j==6) || (i==6 && j==1) || (i==6 && j==6)) {
                        stand = stand + 1;
                    } else if (i+j == 7 || i==j) {
                        stand = stand - 2;
                    } else if (i==0 || j==0 || i==7 || j==7) {
                        stand = stand - 3;
                    } else {
                        stand = stand - 1;
                    }
                }
            }
        }
        System.out.println(stand);
        return stand;
    }

     */

    /*public int utility(char [][] helpBoard){
        int score1 = 0;
        //int score2 = 0;

        int plainScore = 1;
        int stableScore = 1000;
        int nextToOpponentCornerScore = 100;
        int cornerScore = 1000;
        int borderScore = 80;
        int innerCircleCornerSCore = 50;
        int innerCircleBorderScore = 20;
        int nextToEmptyCornerPenalty = -500;
        int NextToBoarder = -10;


        for(int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){

                boolean isCorner = (i == 0 && j == 0) || (i == 0 && j == 7) || (i == 7 && j == 0) || (i == 7 && j == 7);
                boolean isBoarder = !isCorner && ((i == 0) || (j == 0) || (i == 7) || (j == 7));

                boolean isInnerCircleCorner = i == 2 && j == 2 || i == 2 && j == 5 || i == 5 && j == 2 || i == 5 && j == 5;
                boolean isInnerCircleBorder = !isInnerCircleCorner && ((i == 2) || (i == 5) || (j == 2) || (j == 5));

                boolean isBoarderNextToCorner = (i == 0 && j == 1) || (i == 1 && j == 0) || (i == 6 && j == 0) || (i == 7 && j == 0) || (i == 0 && j == 6) || (i == 0 && j == 7) || (i == 7 && j == 6) || (i == 6 && j == 7);

                boolean isNextToUpL = (i == 0 && j == 1) || (i == 1 && j == 0) || (i == 1 && j == 1);
                boolean isNextToUpR = (i == 6 && j == 0) || (i == 7 && j == 0) || (i == 6 && j == 1);
                boolean isNextToDoL = (i == 0 && j == 6) || (i == 0 && j == 7) || (i == 1 && j == 6);
                boolean isNextToDoR = (i == 7 && j == 6) || (i == 6 && j == 7) || (i == 6 && j == 6);
                boolean isNextToCorner = isNextToUpL || isNextToUpR || isNextToDoL || isNextToDoR;

                boolean isNextToEmptyUpL = helpBoard[0][0] == 0 && isNextToUpL;
                boolean isNextToEmptyUpR = helpBoard[7][0] == 0 && isNextToUpR;
                boolean isNextToEmptyDoL = helpBoard[0][7] == 0 && isNextToDoL;
                boolean isNextToEmptyDoR = helpBoard[7][7] == 0 && isNextToDoR;
                boolean isNextToEmptyCorner = isNextToEmptyUpL || isNextToEmptyUpR || isNextToEmptyDoL || isNextToEmptyDoR;

                boolean isNextTo1UpL = helpBoard[0][0] == 1 && isNextToUpL;
                boolean isNextTo1UpR = helpBoard[7][0] == 1 && isNextToUpR;
                boolean isNextTo1DoL = helpBoard[0][7] == 1 && isNextToDoL;
                boolean isNextTo1DoR = helpBoard[7][7] == 1 && isNextToDoR;
                boolean isNextTo1Corner = isNextTo1UpL || isNextTo1UpR || isNextTo1DoL || isNextTo1DoR;

                boolean isNextTo2UpL = helpBoard[0][0] == 2 && isNextToUpL;
                boolean isNextTo2UpR = helpBoard[7][0] == 2 && isNextToUpR;
                boolean isNextTo2DoL = helpBoard[0][7] == 2 && isNextToDoL;
                boolean isNextTo2DoR = helpBoard[7][7] == 2 && isNextToDoR;
                boolean isNextTo2Corner = isNextTo2UpL || isNextTo2UpR || isNextTo2DoL || isNextTo2DoR;


                if (helpBoard[j][i] == myColor){
                    score1 += plainScore;
                    if(isCorner) score1 += borderScore;
                    else if(isNextTo1Corner) score1 += stableScore;
                    else if (isNextTo2Corner) score1 += nextToOpponentCornerScore;
                    else if(isBoarder) score1 += cornerScore;
                    else if(isInnerCircleCorner) score1 += innerCircleCornerSCore;
                    else if(isInnerCircleBorder) score1 += innerCircleBorderScore;
                    else if(isNextToEmptyCorner) score1 += nextToEmptyCornerPenalty;
                }
            }
        }
        return score1;
    }

     */

    //Main method to play the game
       public static void main (String[] args){
            System.out.println("Welcome to a game of Othello!");
            System.out.println("Gamestate: \nBlack: 2" + " White: 2");
            System.out.println();

            Random rnd = new Random();

            MiniMaxPlayer player1 = new MiniMaxPlayer();
            //RandomPlayer player1 = new RandomPlayer();
            //MiniMaxPlayer player2 = new MiniMaxPlayer();
            RandomPlayer player2 = new RandomPlayer();

            player1.init(0, 0L, rnd);
            player2.init(1, 0L, rnd);

            Move m1 = null;
            Move m2 = null;

           System.out.println("These are the placeable locations: ");
           System.out.println(player1.board.getPlaceableLocations(player1.myColor, player1.oppColor));
           player1.board.showPlaceableLocations(player1.board.getPlaceableLocations(player1.myColor, player1.oppColor));
           System.out.println();

           m1 = player1.nextMove((Move) null, 0L, 0L);

            while(true) {
                do {
                    if (m1 == null && m2 == null) {
                        return;
                    }

                    System.out.println("These are the placeable locations: ");
                    System.out.println(player1.board.getPlaceableLocations(player1.oppColor, player1.myColor));
                    player1.board.showPlaceableLocations(player1.board.getPlaceableLocations(player1.oppColor, player1.myColor));

                    player1.board.countScore();
                    System.out.println();
                    player1.board.announceWinner(player1.board.getPlaceableLocations(player1.myColor, player1.oppColor), player1.board.getPlaceableLocations(player1.oppColor, player1.myColor));
                    System.out.println();
                    if (player1.board.isTerminal(player1.board.getPlaceableLocations(player1.myColor, player1.oppColor), player1.board.getPlaceableLocations(player1.oppColor, player1.myColor)) == true){
                        System.exit(0);
                    }

                    //System.out.println("Player 2 is now playing!");
                    m2 = player2.nextMove(m1, 0L, 0L);

                } while(m1 == null && m2 == null);
                System.out.println("These are the placeable locations: ");
                System.out.println(player2.board.getPlaceableLocations(player2.oppColor, player2.myColor));
                player2.board.showPlaceableLocations(player2.board.getPlaceableLocations(player2.oppColor, player2.myColor));
                player2.board.countScore();

                System.out.println();
                player2.board.announceWinner(player2.board.getPlaceableLocations(player2.myColor, player2.oppColor), player2.board.getPlaceableLocations(player2.oppColor, player2.myColor));
                System.out.println();

                if (player2.board.isTerminal(player2.board.getPlaceableLocations(player2.myColor, player2.oppColor), player2.board.getPlaceableLocations(player2.oppColor, player2.myColor)) == true){
                    System.exit(0);
                }

                m1 = player1.nextMove(m2, 0L, 0L);
            }

        }

    }
