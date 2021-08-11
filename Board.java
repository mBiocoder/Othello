import szte.mi.Move;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

public class Board {
    //Attributes
    //Define various seed types
    final public static char BLACK = 'B';
    final public static char EMPTY = '_';
    final public static char WHITE = 'W';

    //Declare player and opponent
    private char player;
    private char opponent;

    //Getter and Setter for player and opponent
    public void setPlayer(char player) {
        this.player = player;
    }

    public char getPlayer(int i, int j, char [][] board){
        char player = board[i][j];
        return player;
    }

    //Variables related to moves
    private int x;
    private int y;
    private Move prevMove;
    public void setPrevMove(Move prevMove) {
        this.prevMove = prevMove;
    }
    private Boolean pass;
    Scanner scan = new Scanner(System.in);
    private String input1;
    private String input2;

    //Define board with getter
    private char[][] board;
    private int boardSize = 8;
    public char[][] getBoard() {
        return board;
    }
    private final int boardX[] = new int[]{0, 1, 2, 3, 4, 5, 6, 7};

    //Variables related to scoring
    private int WScore;
    private int BScore;
    private int remaining;
    public int getWScore() {
        return WScore;
    }
    public int getBScore() {
        return BScore;
    }

    //Constructor which resets board
    public Board(){
        this.resetGame();
    }

    //Initialize the game = reset the game
    public void init(){
        this.resetGame();
    }

    //Reset the game
    public void resetGame(){
        player = 'B'; //Black begins the game
        opponent = 'W';

        //Define the board
        board = new char[][]{
                {'_','_','_','_','_','_','_','_',},
                {'_','_','_','_','_','_','_','_',},
                {'_','_','_','_','_','_','_','_',},
                {'_','_','_','W','B','_','_','_',},
                {'_','_','_','B','W','_','_','_',},
                {'_','_','_','_','_','_','_','_',},
                {'_','_','_','_','_','_','_','_',},
                {'_','_','_','_','_','_','_','_',},
        };

    }

    //If we print an object the toString method needs to be overriden
    @Override
    public String toString(){
        return "["+x+", "+y+"]";
    }
    @Override
    public int hashCode() {
        return Integer.parseInt(x+""+y);
    }
    @Override
    public boolean equals(Object o){
        return o.hashCode()==this.hashCode();
    }

    //Methods

    //Check if move is a legal move
    public boolean isLegal(int x, int y, char currentPlayer) {
        boolean isLegalMove = false;
        char same = currentPlayer;
        //Derive currentopponent from currentPlayer
        char other = currentPlayer == WHITE ? BLACK : WHITE;

        if (x >= 0 && x < boardSize && y >= 0 && y < boardSize && (currentPlayer == WHITE || currentPlayer == BLACK)) {
            //If board at given position is already filled, then return false
            if (this.board[x][y] != EMPTY) {
                isLegalMove = false;
                return isLegalMove;
            } else {
                for(int diffy = -1; diffy < 2; ++diffy) {
                    for(int diffx = -1; diffx < 2; ++diffx) {
                        //diifx and diffy should not be 0
                        if (diffx != 0 || diffy != 0) {
                            int i = x + diffx;
                            int j = y + diffy;

                            int length;

                            for(length = 0; i >= 0 && i < boardSize && j >= 0 && j < boardSize && this.board[i][j] == other; ++length) {
                                j += diffy;
                                i += diffx;
                            }

                            if (length > 0 && i >= 0 && i < boardSize && j >= 0 && j < boardSize && this.board[i][j] == same) {
                                isLegalMove = true;
                                return isLegalMove;
                            }
                        }
                    }
                }
                isLegalMove = false;
                return isLegalMove;
            }
        } else {
            throw new IllegalArgumentException(x + " " + y + " " + currentPlayer);
        }
    }

    //Play the game othello
    public Move playGame(){
        while (true) {
            pass = false;
            //Get placeable locations and print them out and show on board
            HashSet<PlayerMove> blackPlaceableLocations = getPlaceableLocations('B', 'W');
            HashSet<PlayerMove> whitePlaceableLocations = getPlaceableLocations('W', 'B');
            System.out.println("Black can place here: " + blackPlaceableLocations);
            showPlaceableLocations(blackPlaceableLocations);

            //Check for win
            announceWinner(whitePlaceableLocations, blackPlaceableLocations);

            //Check for pass
            if (blackPlaceableLocations.isEmpty() && announceWinner(whitePlaceableLocations, blackPlaceableLocations) != 0 && announceWinner(whitePlaceableLocations, blackPlaceableLocations) != 1 && announceWinner(whitePlaceableLocations, blackPlaceableLocations) != -1) {
                System.out.println("Black needs to pass to white");
                pass = true;
                return null;
            }

            //Get move if we are not passing for black
            if (!pass) {
                System.out.println("Black place your move for x-axis: ");
                input1 = scan.next();
                System.out.println("Black place your move for y-axis: ");
                input2 = scan.next();
                y = Integer.parseInt(input1);
                x = Integer.parseInt(input2);

                //set move
                PlayerMove move = new PlayerMove(x, y, player);

                //Check for invalid input
                while (!blackPlaceableLocations.contains(move)) {
                    System.out.println("Invalid move!\n\nBlack place move for x-axis: ");
                    input1 = scan.next();
                    System.out.println("Black place your move for y-axis: ");
                    input2 = scan.next();
                    y = Integer.parseInt(input1);
                    x = Integer.parseInt(input2);
                    //Break loop only after valid input is entered
                    if (blackPlaceableLocations.contains(new PlayerMove(x,y,player))){
                        break;
                    }

                }
                //Set move, updatre board and print gameState
                setMove(x, y, 'B', 'W',getBoard());
                updateFunction();
                setPrevMove(move.getMove());
                System.out.println("Gamestate: \nBlack: " + BScore + " White: " + WScore);
            }

            //Switch player and opponent
            if (player == 'B') {
                setPlayer('W');
            } else if (player == 'W') {
                setPlayer('B');
            }

            pass = false;

            //Print placeable locations and show them
            whitePlaceableLocations = getPlaceableLocations('W', 'B');
            blackPlaceableLocations = getPlaceableLocations('B', 'W');
            System.out.println("White can place here: " + whitePlaceableLocations);
            showPlaceableLocations(whitePlaceableLocations);

            //check for pass
            if (whitePlaceableLocations.isEmpty() && announceWinner(whitePlaceableLocations, blackPlaceableLocations) != 0 && announceWinner(whitePlaceableLocations, blackPlaceableLocations) != 1 && announceWinner(whitePlaceableLocations, blackPlaceableLocations) != -1) {
                System.out.println("White needs to pass to Black");
                pass = true;
                return null;
            }

            //Get input if not pass and set move
            if (!pass) {
                System.out.println("White place your move for x-axis: ");
                input1 = scan.next();
                System.out.println("White place your move for y-axis: ");
                input2 = scan.next();
                y = Integer.parseInt(input1);
                x = Integer.parseInt(input2);

                PlayerMove move3 = new PlayerMove(x, y, player);

                //Check for invalid input
                while (!whitePlaceableLocations.contains(move3.getMove())) {
                    System.out.println("Invalid move!\n\nWhite place your move for x-axis: ");
                    input1 = scan.next();
                    System.out.println("White place your move for y-axis: ");
                    input2 = scan.next();
                    y = Integer.parseInt(input1);
                    x = Integer.parseInt(input2);
                    //Only exit when valid input is entered
                    if (whitePlaceableLocations.contains(new PlayerMove(x,y,player))){
                        break;
                    }

                }
                //Set move, update board and print the score
                setMove(x, y, 'W', 'B',getBoard());
                updateFunction();
                setPrevMove(move3.getMove());
                System.out.println("Gamestate: \nWhite: " + WScore + " Black: " + BScore);

                //Switch player and opponent
                if (player == 'B') {
                    setPlayer('W');
                } else if (player == 'W') {
                    setPlayer('B');
                }
            }
        }
    }

    //Count the score of black and white seeds using method count
    public char countScore() {
        remaining = this.count(EMPTY);
        WScore = this.count(WHITE);
        BScore = this.count(BLACK);
        if (WScore > BScore) {
            return BLACK;
        } else {
            return WScore < BScore ? EMPTY : WHITE;
        }
    }

    //Given a seedtype it counts its occurences on board
    public int count(char seedtype) {
        int k = 0;

        for(int j = 0; j < boardSize; ++j) {
            for(int i = 0; i < boardSize; ++i) {
                if (this.board[i][j] == seedtype) {
                    ++k;
                }
            }
        }

        return k;
    }

    //Set a move on the board manually (I have removed the alternate way of looking in directions to set the move from my code as this (more detailled) method made debugging easier)
    public void setMove(int p, int q, char player, char opponent, char[][] gamefield){
        gamefield[p][q] = player;
        int I = p, J = q;

        if(p-1>=0 && q-1>=0 && gamefield[p-1][q-1] == opponent){
            p = p-1; q = q-1;
            while(p>0 && q>0 && gamefield[p][q] == opponent){
                p--;q--;
            }
            if(p>=0 && q>=0 && gamefield[p][q] == player) {
                while(p!=I-1 && q!=J-1){
                    gamefield[++p][++q]=player;
                }
            }
        }
        p=I;q=J;
        if(p-1>=0 && gamefield[p-1][q] == opponent){
            p = p-1;
            while(p>0 && gamefield[p][q] == opponent){
                p--;
            }
            if(p>=0 && gamefield[p][q] == player) {
                while(p!=I-1)gamefield[++p][q]=player;}
        }
        p=I;
        if(p-1>=0 && q+1<=7 && gamefield[p-1][q+1] == opponent){
            p = p-1; q = q+1;
            while(p>0 && q<7 && gamefield[p][q] == opponent){p--;q++;}
            if(p>=0 && q<=7 && gamefield[p][q] == player) {while(p!=I-1 && q!=J+1)gamefield[++p][--q] = player;}
        }
        p=I;q=J;
        if(q-1>=0 && gamefield[p][q-1] == opponent){
            q = q-1;
            while(q>0 && gamefield[p][q] == opponent)q--;
            if(q>=0 && gamefield[p][q] == player) {while(q!=J-1)gamefield[p][++q] = player;}
        }
        q=J;
        if(q+1<=7 && gamefield[p][q+1] == opponent){
            q=q+1;
            while(q<7 && gamefield[p][q] == opponent)q++;
            if(q<=7 && gamefield[p][q] == player) {while(q!=J+1)gamefield[p][--q] = player;}
        }
        q=J;
        if(p+1<=7 && q-1>=0 && gamefield[p+1][q-1] == opponent){
            p=p+1;q=q-1;
            while(p<7 && q>0 && gamefield[p][q] == opponent){p++;q--;}
            if(p<=7 && q>=0 && gamefield[p][q] == player) {while(p!=I+1 && q!=J-1)gamefield[--p][++q] = player;}
        }
        p=I;q=J;
        if(p+1 <= 7 && gamefield[p+1][q] == opponent){
            p=p+1;
            while(p<7 && gamefield[p][q] == opponent) p++;
            if(p<=7 && gamefield[p][q] == player) {while(p!=I+1)gamefield[--p][q] = player;}
        }
        p=I;

        if(p+1 <= 7 && q+1 <=7 && gamefield[p+1][q+1] == opponent){
            p=p+1;q=q+1;
            while(p<7 && q<7 && gamefield[p][q] == opponent){p++;q++;}
            if(p<=7 && q<=7 && gamefield[p][q] == player)while(p!=I+1 && q!=J+1)gamefield[--p][--q] = player;}
    }

    //Display the board in a nice way
    public void displayBoard(char [][] gameboard){
        System.out.print("\n  ");
        for(int i=0;i<boardSize;++i)System.out.print(boardX[i]+" ");
        System.out.println();
        for(int i=0;i<boardSize;++i){
            System.out.print((i)+" ");
            for(int j=0;j<boardSize;++j)
                System.out.print(gameboard[i][j]+" ");
            System.out.println();
        }
        System.out.println();
    }

    //Find all placeable Locations and add them to the hashset of placeable locations
    public void findPlaceableLocations(char player, char opponent, HashSet<PlayerMove> placeablePositions, char [][] gameboard){
        for(int i=0;i<boardSize;++i){
            for(int j=0;j<boardSize;++j) {
                if(gameboard[i][j] == opponent){
                    int I = i, J = j;
                    if(i-1>=0 && j-1>=0 && gameboard[i-1][j-1] == '_'){
                        i = i+1; j = j+1;
                        while(i<7 && j<7 && gameboard[i][j] == opponent){i++;j++;}
                        if(i<=7 && j<=7 && gameboard[i][j] == player) placeablePositions.add(new PlayerMove(I-1, J-1, player));
                    }
                    i=I;j=J;
                    if(i-1>=0 && gameboard[i-1][j] == '_'){
                        i = i+1;
                        while(i<7 && gameboard[i][j] == opponent) i++;
                        if(i<=7 && gameboard[i][j] == player) placeablePositions.add(new PlayerMove(I-1, J, player));
                    }
                    i=I;
                    if(i-1>=0 && j+1<=7 && gameboard[i-1][j+1] == '_'){
                        i = i+1; j = j-1;
                        while(i<7 && j>0 && gameboard[i][j] == opponent){i++;j--;}
                        if(i<=7 && j>=0 && gameboard[i][j] == player) placeablePositions.add(new PlayerMove(I-1, J+1, player));
                    }
                    i=I;j=J;
                    if(j-1>=0 && gameboard[i][j-1] == '_'){
                        j = j+1;
                        while(j<7 && gameboard[i][j] == opponent)j++;
                        if(j<=7 && gameboard[i][j] == player) placeablePositions.add(new PlayerMove(I, J-1, player));
                    }
                    j=J;
                    if(j+1<=7 && gameboard[i][j+1] == '_'){
                        j=j-1;
                        while(j>0 && gameboard[i][j] == opponent)j--;
                        if(j>=0 && gameboard[i][j] == player) placeablePositions.add(new PlayerMove(I, J+1, player));
                    }
                    j=J;
                    if(i+1<=7 && j-1>=0 && gameboard[i+1][j-1] == '_'){
                        i=i-1;j=j+1;
                        while(i>0 && j<7 && gameboard[i][j] == opponent){i--;j++;}
                        if(i>=0 && j<=7 && gameboard[i][j] == player) placeablePositions.add(new PlayerMove(I+1, J-1, player));
                    }
                    i=I;j=J;
                    if(i+1 <= 7 && gameboard[i+1][j] == '_'){
                        i=i-1;
                        while(i>0 && gameboard[i][j] == opponent) i--;
                        if(i>=0 && gameboard[i][j] == player) placeablePositions.add(new PlayerMove(I+1, J, player));
                    }
                    i=I;
                    if(i+1 <= 7 && j+1 <=7 && gameboard[i+1][j+1] == '_'){
                        i=i-1;j=j-1;
                        while(i>0 && j>0 && gameboard[i][j] == opponent){i--;j--;}
                        if(i>=0 && j>=0 && gameboard[i][j] == player)placeablePositions.add(new PlayerMove(I+1, J+1, player));
                    }
                    i=I;j=J;
                }
            }
        }
    }

    //Check if we have a winner
    //Return 0 id draw, return 1 if white has won and 1 if black has won and -2 for everything else
    public int checkForWin(HashSet<PlayerMove> whitePlaceableLocations, HashSet<PlayerMove> blackPlaceableLocations){
        updateFunction();
        if(remaining == 0){
            if(WScore > BScore) return 1;
            else if(BScore > WScore) return -1;
            else return 0; //Draw
        }
        if(WScore==0 || BScore == 0){
            if(WScore > 0) return 1;
            else if(BScore > 0) return -1;
        }
        if(whitePlaceableLocations.isEmpty() && blackPlaceableLocations.isEmpty()){
            if(WScore > BScore) return 1;
            else if(BScore > WScore) return -1;
            else return 0; //Draw
        }
        return -2; //for every other value
    }

    //Is game over? Check
    public boolean isTerminal(HashSet<PlayerMove> whitePlaceableLocations, HashSet<PlayerMove> blackPlaceableLocations){
        boolean isTerminal = false;
        if (checkForWin(whitePlaceableLocations, blackPlaceableLocations) == 0 || checkForWin(whitePlaceableLocations, blackPlaceableLocations) == 1 || checkForWin(whitePlaceableLocations, blackPlaceableLocations) == -1){
            isTerminal = true;
        }
        return isTerminal;
    }

    //Announce the winner if game over
    public int announceWinner(HashSet<PlayerMove> whitePlaceableLocations, HashSet<PlayerMove> blackPlaceableLocations){
        int result = checkForWin(whitePlaceableLocations, blackPlaceableLocations);
        if(result == 0){
            System.out.println("Game over.\nIt is a draw.");
            //System.exit(0);
        }
        else if(result==1){
            System.out.println("Game over.Congrats!\nWhite wins: "+WScore+":"+BScore);
            //System.exit(0);
        }
        else if(result==-1){
            System.out.println("Game over.Congrats!\nBlack wins: "+BScore+":"+WScore);
            //System.exit(0);
        }
        return result;
    }

    //Returns hashset of placeableLocations
    public HashSet<PlayerMove> getPlaceableLocations(char player, char opponent){
        HashSet<PlayerMove> placeablePositions = new HashSet<>();
        findPlaceableLocations(player, opponent, placeablePositions,getBoard());
        return placeablePositions;
    }

    //Show placeable locations on the board
    public void showPlaceableLocations(HashSet<PlayerMove> locations){
        for(Move p:locations)
            board[p.x][p.y]='*';
        displayBoard(getBoard());
        for(Move p:locations)
            board[p.x][p.y]='_';
    }

    //Update the score
    public void updateFunction(){
        WScore = 0;
        BScore = 0;
        remaining = 0;
        for(int i=0;i<8;++i){
            for(int j=0;j<8;++j){
                if(board[i][j]=='W')WScore++;
                else if(board[i][j]=='B')BScore++;
                else remaining++;
            }
        }
    }
}
