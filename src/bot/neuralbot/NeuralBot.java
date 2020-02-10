package bot.neuralbot;

import bot.BotChars;
import chars.Chars;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import static chars.Chars.Action.*;

public class NeuralBot extends BotChars {

    private final PrintWriter writer;
    private double precX;
    private double precY;
    private double precRota;
    private Chars precClosest;

    public NeuralBot(){
        PrintWriter writer;
        try {
            writer = new PrintWriter("info.txt");
        } catch (FileNotFoundException e) {
            writer = null;
            e.printStackTrace();
        }
        this.writer = writer;
        precX = 0;
        precY = 0;
        precRota = 0;
        precClosest = null;
    }

    @Override
    protected Action nextAction() {
        if(this.writer == null) return None;

        Chars c = getClosest();
        if(c != precClosest){
            precX = 0;
            precY = 0;
            precRota = 0;
            precClosest = c;
        }

        Action action = getAction(c);
        String out = String.format("%f %f %f\t%s\n", precX, precY, precRota, action);
        writer.print(out);

        precX = c.getX();
        precY = c.getY();
        precRota = c.getRota();

        return None;
    }

    private Action getAction(Chars c){
        if((precRota+1)%360 == c.getRota()) return TurnRight;
        else if(((precRota-1) < 0 ? 359 : precRota-1) == c.getRota()) return TurnLeft;
        else{
            double ang = Math.toRadians(precRota);
            if(precX + Math.cos(ang) == c.getX() && precY + Math.sin(ang) == c.getY()) return Forward;
            else if(precX - Math.cos(ang) == c.getX() && precY - Math.sin(ang) == c.getY()) return Backward;
        }
        return None;
    }

    @Override
    protected void blocked() {

    }

    @Override
    protected void finalize() throws Throwable {
        if(writer != null) this.writer.close();
        super.finalize();
    }
}
