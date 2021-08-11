
import szte.mi.Move;

public class PlayerMove extends Move {
    private char playerType;

    //COnstructor
    public PlayerMove(int x, int y, char playerType) {
        super(x, y);
        this.playerType = playerType;
    }

    //Get move
    public Move getMove() {
        return this;
    }

    @Override
    public String toString(){
        return "["+x+", "+y+"]";
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 17 + x;
        hash = hash * 31 + y;
        hash = hash * 13 + playerType;

        return hash;
    }

    @Override
    public boolean equals(Object o) {
        PlayerMove move = (PlayerMove) o;
        if (move.x == this.x && move.y == this.y
                && move.playerType == this.playerType)
            return true;
        else
            return false;
    }
}