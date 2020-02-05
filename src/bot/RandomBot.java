package bot;

import player.Obstacle;

import java.util.List;
import java.util.Random;

public class RandomBot extends BotChars {

    private Random random;

    public RandomBot(){
        this.random = new Random();
    }

    @Override
    protected Action nextAction(List<Obstacle> obstacles) {
        int nbr = random.nextInt(Action.values().length-1);
        return Action.values()[nbr];
    }
}
