package bot;

import org.jetbrains.annotations.NotNull;
import player.Obstacle;

import java.util.List;
import java.util.Random;

public class RandomBot extends BotChars {

    private final Random random;

    public RandomBot(){
        this.random = new Random();
    }

    @NotNull
    @Override
    protected Action nextAction() {
        int nbr = random.nextInt(Action.values().length-1);
        return Action.values()[nbr];
    }

    @Override
    protected void blocked() {}
}
