package bot;

import org.jetbrains.annotations.NotNull;
import chars.Chars;
import player.PlayerChars;
import player.Obstacle;

import java.util.ArrayList;
import java.util.List;

public abstract class BotChars extends Chars {

    /**
     * This method is call every game update
     *
     * @return The action to do at the next move
     */
    @NotNull
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

}
