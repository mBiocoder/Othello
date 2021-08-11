import szte.mi.Move;
import szte.mi.Player;

public class DummyPlayer implements Player {
    //Constructor
    public DummyPlayer() {
    }

    @Override
    public void init(int order, long t, java.util.Random rnd){

    }

    public Move nextMove(Move prevMove, long tOpponent, long t ){
        return null;
    }

}
