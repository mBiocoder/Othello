import szte.mi.Move;
import szte.mi.Player;

public class GUIPlayerClass implements Player {
    //Attributes
    private Move extMove;
    private boolean input = false;
    protected Board board;
    protected char myColor;
    protected char oppColor;

    //Constructor
    public GUIPlayerClass() {
        super();
    }

    //Methods

    //Initialize
    @Override
    public void init( int order, long t, java.util.Random rnd ){
        this.board.init();
    }

    //NextMove
    @Override
    public Move nextMove(Move prevMove, long tOpponent, long t ){
        extMove = null;
        input = true;

        System.out.println("Enter input: " + input);

        while(true) {
            if (extMove == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                board.setMove(prevMove.x, prevMove.y, myColor, oppColor, board.getBoard());
                continue;
            }

            Move currentMove = extMove;

            try {
                if (this.board.isLegal(currentMove.x, currentMove.y, myColor)) {
                    input = false;
                    this.board.setMove(currentMove.x,currentMove.y, myColor, oppColor, board.getBoard());
                    return currentMove;
                } else {
                    extMove = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("Enter input: " + extMove);
        }
    }

    //SetMove
    public void setMove(int x, int y)
    {
        if (input) {
            extMove = new Move(x,y);
        } else {
            System.out.println("Can't accept");
        }
    }

}
