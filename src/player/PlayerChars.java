package player;

import chars.Chars;
import gui.OptionsMenu;
import org.jetbrains.annotations.NotNull;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static chars.Chars.Action.*;

public class PlayerChars extends Chars implements KeyListener {

    public final int up, down, right, left, shoot;
    private int input;

    public PlayerChars(@NotNull OptionsMenu.PlayerMove moove){
        this(moove.up, moove.down, moove.right, moove.left, moove.shoot);
        System.out.println("new char(" + moove.toString() + ")");
    }

    public PlayerChars(int up, int down, int right, int left, int shoot){
        this.up = up;
        this.down = down;
        this.right = right;
        this.left = left;
        this.shoot = shoot;
        this.input = KeyEvent.VK_UNDEFINED;
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

    @NotNull
    @Override
    public Action nextAction() {
        if(input == up){
            return Forward;
        }
        else if(input == down){
            return Backward;
        }
        else if(input == right){
            return TurnRight;
        }
        else if(input == left){
            return TurnLeft;
        }
        else if(input == shoot){
            return Shoot;
        }
        return None;
    }

    @Override
    protected void blocked() {}
}
