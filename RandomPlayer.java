import szte.mi.Move;
import szte.mi.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

public class RandomPlayer implements Player {
    protected Board board;
    protected char myColor;
    protected char oppColor;
    protected ArrayList<PlayerMove> moves = new ArrayList(64);
    protected Random rnd;

    public RandomPlayer() {
    }

    public void init(int order, long t, Random rnd) {
        this.board = new Board();
        if (order == 0) {
            this.myColor = Board.BLACK;
            this.oppColor = Board.WHITE;
        } else {
            if (order != 1) {
                throw new IllegalArgumentException();
            }

            this.myColor = Board.WHITE;
            this.oppColor = Board.BLACK;
        }

        this.moves.clear();

        for(int i = 0; i < 8; ++i) {
            for(int j = 0; j < 8; ++j) {
                this.moves.add(new PlayerMove(i, j, myColor));
            }
        }

        Collections.shuffle(this.moves, rnd);
        this.rnd = rnd;
    }

    public Move nextMove(Move prevMove, long tOpponent, long t) {
        if (prevMove != null) {
            this.board.setMove(prevMove.x, prevMove.y, this.oppColor, myColor, board.getBoard());
        }
        Collections.shuffle(this.moves, this.rnd);
        Move nextMove = null;
        Iterator itr = this.moves.iterator();
        while(itr.hasNext()) {
            Move m = (Move)itr.next();
            try {
                if (this.board.isLegal(m.x, m.y, this.myColor)) {
                    this.board.setMove(m.x, m.y, this.myColor, oppColor, board.getBoard());
                    //board.changedCoinsLocations.clear();
                    nextMove = m;
                    break;
                }
            }catch (IllegalArgumentException e){
                e.getCause();
            }
        }
        return nextMove;


    }

    @Override
    public boolean equals(Object o){
        return o.hashCode()==this.hashCode();
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Welcome to a game of Othello!");
        System.out.println("Gamestate: \nBlack: 2"+" White: 2");
        System.out.println();

        Random rnd = new Random();
        RandomPlayer player1 = new RandomPlayer();
        RandomPlayer player2 = new RandomPlayer();

        player1.init(0, 0L, rnd);
        player2.init(1, 0L, rnd);

        Move m1 = null;
        Move m2 = null;

        System.out.println("These are the placeable locations: ");
        System.out.println(player1.board.getPlaceableLocations(player1.myColor, player1.oppColor));
        player1.board.showPlaceableLocations(player1.board.getPlaceableLocations(player1.myColor, player1.oppColor));
        System.out.println();

        m1 = player1.nextMove((Move)null, 0L, 0L);
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
                m2 = player2.nextMove(m1, 0L, 0L);

            } while(m1 == null && m2 == null);
            System.out.println("These are the placeable locations: ");
            System.out.println(player2.board.getPlaceableLocations(player2.myColor, player2.oppColor));
            player2.board.showPlaceableLocations(player2.board.getPlaceableLocations(player2.myColor, player2.oppColor));
            player2.board.countScore();
            System.out.println();
            player2.board.announceWinner(player2.board.getPlaceableLocations(player2.myColor, player2.oppColor), player2.board.getPlaceableLocations(player2.oppColor, player2.myColor));
            System.out.println();
            m1 = player1.nextMove(m2, 0L, 0L);
        }
    }
}

