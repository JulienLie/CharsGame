package bot;

import player.Chars;
import player.Obstacle;

import java.util.ArrayList;
import java.util.List;

public abstract class BotChars extends Chars {

    /**
     * Default constructor
     * All value set to -1 for key listening
     */
    public BotChars() {
        super(-1, -1, -1, -1, -1);
    }

    @Override
    public final Obstacle doAction(List<Obstacle> obstacles){
        this.obstacles = obstacles;
        switch (nextAction(obstacles)){
            case Forward:
                forward();
                return null;
            case Backward:
                backward();
                return null;
            case TurnLeft:
                turnLeft();
                return null;
            case TurnRight:
                turnRight();
                return null;
            case Shoot:
                return shoot();
            case None:
            default:
                return null;
        }
    }

    /**
     * This method is call every game update
     *
     * @param obstacles a list of all the obstacle on the map (can be a wall, a living Chars or a dead Chars)
     *
     * @return The action to do at the next move
     */
    protected abstract Action nextAction(List<Obstacle> obstacles);

    /**
     * Extract all Chars from the obstacle list
     *
     * @return a list with all the Chars alive
     */
    protected final List<Chars> getChars(){
        List<Chars> chars = new ArrayList<>();
        for(Obstacle o : obstacles){
            if(o instanceof Chars && !((Chars) o).isDead()) chars.add((Chars) o);
        }
        return chars;
    }

    protected enum Action{
        Forward,
        Backward,
        TurnLeft,
        TurnRight,
        Shoot,
        None
    }

}
