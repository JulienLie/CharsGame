package bot;

import java.util.Random;

public class RandomBot extends BotChars {

    private final Random random;

    public RandomBot(){
        this.random = new Random();
    }

    @Override
    protected Action nextAction() {
        int nbr = random.nextInt(Action.values().length-1);
        return Action.values()[nbr];
    }

    @Override
    protected void blocked() {}
}
