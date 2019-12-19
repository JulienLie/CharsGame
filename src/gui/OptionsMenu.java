package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.security.Key;

public class OptionsMenu extends JPanel {

    public static final File OPTIONSFILE = getOptFile();

    public static File getOptFile(){
        File f = new File("otpions.ini");
        if(!f.exists()){
            try {
                if(f.createNewFile()){
                    save();
                }
                else{
                    System.err.println("Couldn't create options file");
                    System.exit(-1);
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Couldn't create options file");
                System.exit(-1);
            }
        }
        try {
            load(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }

    private static void load(File f) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(f));
        String line;
        while((line = reader.readLine()) != null){
            String[] s = line.split("\\(.*\\)| = ");
            for(PlayerMove pm : PlayerMove.values()){
                if(pm.name().equals(s[0])){
                    switch (s[1]){
                        case "Up":
                            pm.up = Integer.parseInt(s[2]);
                            break;
                        case "Down":
                            pm.down = Integer.parseInt(s[2]);
                            break;
                        case "Left":
                            pm.left = Integer.parseInt(s[2]);
                            break;
                        case "Right":
                            pm.right = Integer.parseInt(s[2]);
                            break;
                        case "Shoot":
                            pm.shoot = Integer.parseInt(s[2]);
                            break;
                        default:
                            System.err.println("QUOI?");
                            break;
                    }
                }
            }
        }
    }

    public static void save() throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(OPTIONSFILE);
        for(PlayerMove pm : PlayerMove.values()){
            writer.write(String.format("%sUp = %d\n", pm.toString(), pm.up));
            writer.write(String.format("%sDown = %d\n", pm.toString(), pm.down));
            writer.write(String.format("%sLeft = %d\n", pm.toString(), pm.left));
            writer.write(String.format("%sRight = %d\n", pm.toString(), pm.right));
            writer.write(String.format("%sShoot = %d\n", pm.toString(), pm.shoot));
        }
        writer.flush();
    }

    public enum PlayerMove{
        Player1(KeyEvent.VK_Z, KeyEvent.VK_S, KeyEvent.VK_Q, KeyEvent.VK_D, KeyEvent.VK_SPACE),
        Player2(KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_ENTER),
        Player3(0, 0, 0, 0, 0),
        Player4(0, 0, 0, 0, 0);

        public int up, down, left, right, shoot;

        PlayerMove(int up, int down, int left, int right, int shoot){
            this.up = up;
            this.down = down;
            this.left = left;
            this.right = right;
            this.shoot = shoot;
        }

        @Override
        public String toString() {
            return super.toString()+"(" + up + "," + down +","+left+","+right+","+shoot+")";
        }
    }

    private Game parent;
    private JButton save;
    private JButton cancel;
    private JTabbedPane pane;

    public OptionsMenu(Game parent){
        super();
        this.parent = parent;
        this.save = new JButton("Save Changes");
        this.cancel = new JButton("Cancel Changes");
        this.save.addActionListener(this::saveButton);
        this.cancel.addActionListener(this::cancelButton);

        pane = new JTabbedPane();
        for(PlayerMove pm : PlayerMove.values()){
            pane.addTab(pm.name(), new KeyPanel(pm));
        }

        this.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.gridx = 0;
        c.fill = GridBagConstraints.BOTH;
        c.ipady = 40;
        c.ipadx = 40;
        c.insets = new Insets(5, 5, 5, 5);
        c.gridwidth = 2;
        this.add(pane, c);
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 1;
        c.ipady = 0;
        c.ipadx = 0;
        this.add(save, c);
        c.gridy = 1;
        c.gridx = 1;
        c.ipady = 0;
        c.ipadx = 0;
        this.add(cancel, c);
    }

    private void cancelButton(ActionEvent event) {
        parent.changeMenu(new Menu(parent));
    }

    private void saveButton(ActionEvent event) {
        try {
            save();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("Could not save option file");
        }
        parent.changeMenu(new Menu(parent));
    }

    private class KeyPanel extends JPanel{

        PlayerMove pm;
        JButton[] fields;

        KeyPanel(PlayerMove pm) {
            this.pm = pm;
            this.fields = new JButton[5];
            this.setLayout(new GridLayout(5, 2, 5, 5));
            this.add(new JLabel("Up"));
            String up = KeyEvent.getKeyText(pm.up);
            fields[0] = new JButton(up);
            fields[0].setName("up");
            this.add(fields[0]);
            this.add(new JLabel("Down"));
            String down = KeyEvent.getKeyText(pm.down);
            fields[1] = new JButton(down);
            fields[1].setName("down");
            this.add(fields[1]);
            this.add(new JLabel("Left"));
            String left = KeyEvent.getKeyText(pm.left);
            fields[2] = new JButton(left);
            fields[2].setName("left");
            this.add(fields[2]);
            this.add(new JLabel("Right"));
            String right = KeyEvent.getKeyText(pm.right);
            fields[3] = new JButton(right);
            fields[3].setName("right");
            this.add(fields[3]);
            this.add(new JLabel("Shoot"));
            String shoot = KeyEvent.getKeyText(pm.shoot);
            fields[4] = new JButton(shoot);
            fields[4].setName("shoot");
            this.add(fields[4]);
            for (final JButton b : fields) {
                b.addActionListener(this::onClick);
            }
        }

        private void onClick(ActionEvent event) {
            Object source = event.getSource();
            if(source instanceof JButton){
                final JButton button = (JButton) source;
                Color c = button.getForeground();
                button.setForeground(Color.red);
                button.addKeyListener(new KeyListener() {
                    @Override
                    public void keyTyped(KeyEvent keyEvent) {}
                    @Override
                    public void keyPressed(KeyEvent keyEvent) {
                        switch (button.getName()){
                            case "up":
                                KeyPanel.this.pm.up = keyEvent.getKeyCode();
                                break;
                            case "down":
                                KeyPanel.this.pm.down = keyEvent.getKeyCode();
                                break;
                            case "left":
                                KeyPanel.this.pm.down = keyEvent.getKeyCode();
                                break;
                            case "right":
                                KeyPanel.this.pm.down = keyEvent.getKeyCode();
                                break;
                            case "shoot":
                                KeyPanel.this.pm.down = keyEvent.getKeyCode();
                                break;
                            default:
                                System.out.println("QUOI?");
                                break;
                        }
                        button.setForeground(c);
                        button.setText(KeyEvent.getKeyText(keyEvent.getKeyCode()));
                    }
                    @Override
                    public void keyReleased(KeyEvent keyEvent) {}
                });
            }
        }
    }
}
