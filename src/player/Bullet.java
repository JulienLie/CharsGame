package player;

import helper.PlanHelper;

import java.awt.*;
import java.awt.geom.Area;
import java.util.List;

public class Bullet implements Obstacle{

    private static final int height = 2, width = 6;
    private static final double bulletSpeed = 2;

    private double x, y;
    private double rota;
    private Color color;

    Bullet(int x, int y, double rota, Color color){
        this.x = x;
        this.y = y;
        this.rota = rota;
        this.color = color;
    }

    public boolean doAction(List<Obstacle> obstacles, List<Chars> chars){
        double rad = rota*Math.PI/180.;
        x+= bulletSpeed*Math.cos(rad);
        y+= bulletSpeed*Math.sin(rad);
        Area area = new Area(this.getHitBox());
        for(Obstacle o : obstacles){
            Area other = new Area(o.getHitBox());
            other.intersect(area);
            if(!other.isEmpty()){
                return true;
            }
        }
        for(Chars c : chars){
            Area other = new Area(c.getHitBox());
            other.intersect(area);
            if(!other.isEmpty() && this.color != c.getColor()){
                c.hit();
                return true;
            }
        }
        return false;
    }

    @Override
    public Polygon getHitBox() {
        return PlanHelper.rotatedRectangle(x, y, height, width, rota);
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(color);
        g.fillPolygon(getHitBox());
    }
}
