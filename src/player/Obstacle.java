package player;

import java.awt.*;

public interface Obstacle {
    Polygon getHitBox();
    void paint(Graphics g);
}
