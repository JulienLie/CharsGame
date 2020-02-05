package player;

import gui.Map;
import gui.OptionsMenu;
import helper.PlanHelper;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Chars implements KeyListener, Obstacle {

    private static final long reloadTime = 1500;
    private static final int width = 30, height = 20;
    private static int nbr = 0;
    private static final BufferedImage charImg;

    static {
        BufferedImage charImgTemp;
        try {
            charImgTemp = ImageIO.read(new File("assets/Chars.png"));
        } catch (IOException e) {
            charImgTemp = null;
            e.printStackTrace();
        }
        charImg = charImgTemp;
    }

    public final int up, down, right, left, shoot;
    private double x,y;
    private double rota; // En degré
    private long lastShoot;
    private int input;
    private Color color;
    private boolean isDead;
    private final int serial;
    private BufferedImage image;
    protected List<Obstacle> obstacles;

    public Chars(@NotNull OptionsMenu.PlayerMove moove){
        this(moove.up, moove.down, moove.right, moove.left, moove.shoot);
        System.out.println("new char(" + moove.toString() + ")");
    }

    public Chars(int up, int down, int right, int left, int shoot){
        this.up = up;
        this.down = down;
        this.right = right;
        this.left = left;
        this.shoot = shoot;
        this.x = 0;
        this.y = 0;
        this.rota = 0;
        this.lastShoot = System.currentTimeMillis();
        this.input = KeyEvent.VK_UNDEFINED;
        color = Color.gray;
        isDead = true;
        this.serial = ++nbr;
        if(charImg != null) image = PlanHelper.changeColor(charImg, color);
    }

    public final void spawn(Map.Spawn spawn){
        this.x = spawn.pos.x;
        this.y = spawn.pos.y;
        this.rota = spawn.dir;
        this.color = spawn.color;
        image = PlanHelper.changeColor(charImg, color);
        isDead = false;
    }

    @Override
    public final Polygon getHitBox(){
        return PlanHelper.rotatedRectangle(x, y, height, width, rota);
    }

    public Obstacle doAction(List<Obstacle> obstacles){
        if(isDead) return null;
        this.obstacles = obstacles;
        if(input == up){
            forward();
        }
        else if(input == down){
            backward();
        }
        else if(input == right){
            turnRight();
        }
        else if(input == left){
            turnLeft();
        }
        else if(input == shoot){
            return shoot();
        }
        return null;
    }

    protected final void forward(){
        double rad = Math.toRadians(rota);
        move(x+Math.cos(rad), y+Math.sin(rad), rota);
    }

    protected final void backward(){
        double rad = Math.toRadians(rota);
        move(x-Math.cos(rad), y-Math.sin(rad), rota);
    }

    protected final void turnLeft(){
        move(x, y, (rota-1) < 0 ? 359 : rota-1);
    }

    protected final void turnRight(){
        move(x, y, (rota+1)%360);
    }

    private void move(double x, double y, double rota){
        double lastX = this.x;
        double lastY = this.y;
        double lastRota = this.rota;
        this.x = x;
        this.y = y;
        this.rota = rota;
        Area thisA = new Area(this.getHitBox());
        for(Obstacle o : obstacles){
            Area a = new Area(o.getHitBox());
            a.intersect(thisA);
            if(!a.isEmpty() && o!=this) {
                this.x = lastX;
                this.y = lastY;
                this.rota = lastRota;
                return;
            }
        }
    }

    protected final double getX(){
        return x;
    }

    protected final double getY(){
        return y;
    }

    protected final double getRota(){
        return rota;
    }

    protected final Obstacle shoot(){
        long now = System.currentTimeMillis();
        if(lastShoot+reloadTime <= now){
            this.lastShoot = now;
            double[] pos = PlanHelper.rotate(x, y, x+width/2., y, Math.toRadians(rota));
            return new Bullet((int) Math.round(pos[0]), (int) Math.round(pos[1]), rota, color);
        }
        return null;
    }

    public final void paint(Graphics g){
        if(charImg == null) {
            g.setColor(color);
            g.fillPolygon(this.getHitBox());
        }
        else{
            BufferedImage image = PlanHelper.rotateImageByDegrees(this.image, rota);
            g.drawImage(image, (int) (x-(image.getWidth()/2)), (int) (y-(image.getHeight()/2)), null);
        }
    }

    final void hit(){
        this.isDead = true;
        this.color = Color.gray;
        this.image = PlanHelper.changeColor(charImg, color);
    }

    public final boolean isDead(){
        return isDead;
    }

    final Color getColor(){
        return color;
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        int code = keyEvent.getKeyCode();
        if(code == up || code == down || code == left || code == right || code == shoot) {
            this.input = keyEvent.getKeyCode();
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        if(this.input == keyEvent.getKeyCode()) input = KeyEvent.VK_UNDEFINED;
    }

    public final String toString(){
        return String.format("char%d(%s;%d;%d;%d;%d;%f)", serial, color, (int)x, (int)y, height, width, rota);
    }
}
