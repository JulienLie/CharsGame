package bot;

import player.Chars;
import player.Obstacle;

import java.awt.*;
import java.util.List;

import static bot.BotChars.Action.*;

public class ShootBot extends BotChars {
    @Override
    public Action nextAction(List<Obstacle> obstacles) {
        Chars closest = null;
        double closDist = Double.MAX_VALUE;
        double closX = 0;
        double closY = 0;
        for(Chars c : getChars()){
            if(c != null && c != this){
                Rectangle r = c.getHitBox().getBounds();
                double x = r.x+(r.width/2.);
                double y = r.y+(r.height/2.);
                double dist =  Math.sqrt(Math.pow(this.getX() - x, 2) + Math.pow(this.getY() - y, 2));
                if(dist < closDist){
                    closDist = dist;
                    closX = x;
                    closY = y;
                    closest = c;
                }
            }
        }
        if(closest == null) return null;
        double m = Math.tan(Math.toRadians(getRota()));
        double p = this.getY() - m*this.getX();
        double pos = closY - (m*closX+p);
        if(getRota() <= 90 || getRota() > 270) {
            if(pos > 10) return TurnRight;
            else if(pos < -10) return TurnLeft;
            else return Shoot;
        }
        else{
            if(pos > 10) return TurnLeft;
            else if(pos < -10) return TurnRight;
            else return Shoot;
        }
    }
}
