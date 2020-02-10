package bot;

import chars.Chars;

import java.awt.*;

import static chars.Chars.Action.*;

public class ShootBot extends BotChars {

    @Override
    protected Action nextAction() {
        Chars closest = getClosest();
        if(closest == null) return None;
        double closX = closest.getX();
        double closY = closest.getY();
        int aim = aim(closX, closY, 1);
        if(aim > 0) return TurnLeft;
        else if (aim < 0) return TurnRight;
        else return Shoot;
    }

    /**
     * Aim at the @x, @y location with a the given @precision
     *
     * @param x the x coordinate to aim
     * @param y the y coordinate to aim
     * @param precision the precision
     * @return -1 if you need to turn right; 1 if tou need to turn left and 0 if te target is aim
     */
    protected int aim(double x, double y, double precision){

        double x1 = this.getX() + Math.cos(Math.toRadians(this.getRota()));
        double y1 = this.getY() + Math.sin(Math.toRadians(this.getRota()));
        double ang1 = Math.atan2((y1 - this.getY()), (x1 - this.getX()));
        double ang2 = Math.atan2((y - this.getY()), (x - this.getX()));
        double angle = Math.toDegrees(Math.abs(ang1 - ang2));

        double m = Math.tan(Math.toRadians(getRota()));
        double p = this.getY() - m*this.getX();
        double pos = y - (m*x+p);
        if(angle > precision) {
            if (getRota() <= 90 || getRota() > 270) {
                if (pos > 0) return -1;
                else return 1;
            } else {
                if (pos > 0) return 1;
                else return -1;
            }
        }
        return 0;
    }

    @Override
    protected void blocked() {}
}
