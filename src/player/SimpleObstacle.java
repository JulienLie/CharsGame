package player;

import java.awt.*;

public final class SimpleObstacle implements Obstacle{

    private final Polygon hitBox;

    public SimpleObstacle(Polygon hitBox){
        this.hitBox = hitBox;
    }

    @Override
    public Polygon getHitBox() {
        return hitBox;
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.gray);
        g.fillPolygon(hitBox);
    }
}
