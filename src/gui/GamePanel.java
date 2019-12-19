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

    GamePanel(Map map){
        this.chars = new ArrayList<>(2);
        this.bullets = new LinkedList<>();
        this.map = map;
    }

    @Override
    public void paint(Graphics g){
        super.paint(g);

        for(Obstacle c : map.getObstacles()){
            c.paint(g);
        }

        for(Chars c : chars){
            c.paint(g);
        }

        for(Bullet b : bullets){
            b.paint(g);
        }
    }

    boolean doGameUpdate(double delta){
        List<Obstacle> all = new ArrayList<>(map.getObstacles());
        all.addAll(chars);
        int nbrAlive = 0;
        for(Chars c : chars){
            Bullet b = (Bullet) c.doAction(all);
            if(b != null) bullets.add(b);
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
        this.repaint();
        return true;
    }

    public Map getMap(){
        return map;
    }
}
