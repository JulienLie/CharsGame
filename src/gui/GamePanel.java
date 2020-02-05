package gui;

import player.Bullet;
import player.Chars;
import player.Obstacle;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GamePanel extends JPanel {

    private List<Chars> chars;
    private List<Bullet> bullets;
    private Map map;
    private List<Obstacle> obstacles;

    GamePanel(Map map){
        this.chars = new ArrayList<>(2);
        this.bullets = new LinkedList<>();
        this.map = map;
        this.obstacles = new ArrayList<>();
        this.obstacles.addAll(map.getObstacles());
    }

    @Override
    public void paint(Graphics g){
        super.paint(g);

        for(Obstacle c :obstacles){
            c.paint(g);
        }

        for(Bullet b : bullets){
            b.paint(g);
        }
    }

    boolean doGameUpdate(double delta){
        int nbrAlive = 0;
        for(Chars c : chars){
            Bullet b = (Bullet) c.doAction(obstacles);
            if(b != null){
                bullets.add(b);
            }
            nbrAlive += c.isDead() ? 0 : 1;
        }

        bullets.removeIf(bullet -> bullet.doAction(map.getObstacles(), chars));

        return nbrAlive > 1;
    }

    public List<Chars> getChars(){
        return chars;
    }

    public boolean addChar(Chars c){
        if(map.getSpawns().size() <= chars.size()) return false;
        c.spawn(map.getSpawns().get(chars.size()));
        this.chars.add(c);
        this.obstacles.add(c);
        this.repaint();
        return true;
    }

    public Map getMap(){
        return map;
    }
}
