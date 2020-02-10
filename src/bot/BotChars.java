package bot;

import chars.Chars;
import player.PlayerChars;
import player.Obstacle;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class BotChars extends Chars {

    /**
     * This method is call every game update
     *
     * @return The action to do at the next move
     */
    protected abstract Action nextAction();

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

    /**
     * Find the closest chars
     *
     * @return the closest chars
     */
    protected Chars getClosest(){
        Chars closest = null;
        double closDist = Double.MAX_VALUE;
        for(Chars c : getChars()){
            if(c != null && c != this){
                Rectangle r = c.getHitBox().getBounds();
                double x = r.x+(r.width/2.);
                double y = r.y+(r.height/2.);
                double dist =  Math.sqrt(Math.pow(this.getX() - x, 2) + Math.pow(this.getY() - y, 2));
                if(dist < closDist){
                    closDist = dist;
                    closest = c;
                }
            }
        }
        return closest;
    }

}
