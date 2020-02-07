package gui;

import bot.PathFindingBot;
import bot.ShootBot;
import player.PlayerChars;
import player.Obstacle;
import player.SimpleObstacle;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;

public enum Map {

    Map1(new Polygon(new int[]{0, 500, 500, 0}, new int[]{0, 0, 500, 500}, 4),
            new Polygon[]{
                    new Polygon(new int[]{245, 255, 255, 245}, new int[]{100, 100, 400, 400}, 4)
            },
            new int[]{100, 400},
            new int[]{250, 250},
            new double[]{0, 180},
            new Color[]{Color.red, Color.blue}),
    Map2(new Polygon(new int[]{0, 600, 600, 0}, new int[]{0, 0, 600, 600}, 4),
            new Polygon[]{
                    new Polygon(new int[]{295, 305, 295, 305}, new int[]{75, 75, 525, 525}, 4),
                    new Polygon(new int[]{75, 525, 525, 75}, new int[]{295, 305, 295, 305}, 4)
            },
            new int[]{150, 450, 150, 450},
            new int[]{150, 450, 450, 150},
            new double[]{45, 225, 315, 135},
            new Color[]{Color.red, Color.blue, Color.green, Color.yellow});
    /*Map3(new Polygon(new int[]{0, 200, 400, 600, 600, 400, 200, 0}, new int[]{200, 0, 0, 200, 400, 600, 600, 400, 200}, 8),
            new Polygon[]{

            },
            new int[]{100, 200, 300, 400, 500, 400, 300, 200},
            new int[]{300, 200, 100, 200, 300, 400, 500, 400},
            new double[]{0, 45, 90, 135, 180, 225, 270, 315},
            new Color[]{Color.red, Color.blue, Color.green, Color.yellow, Color.CYAN, Color.orange, Color.pink, Color.BLACK});*/

    private final List<Obstacle> obstacles;
    private final List<Spawn> spawns;

    Map(Polygon bound, Polygon[] obstacle, int[] xs, int[] ys, double[] rots, Color[] colors) {
        this.obstacles = new ArrayList<>(obstacle.length);
        this.spawns = new ArrayList<>(xs.length);
        for (int i = 0; i < bound.npoints; i++) {
            int j = (i + 1) % bound.npoints;
            int x0 = bound.xpoints[i];
            int y0 = bound.ypoints[i];
            int x1 = bound.xpoints[j];
            int y1 = bound.ypoints[j];
            Polygon p = new Polygon(new int[]{x0, x1, x1 + 1, x0 + 1}, new int[]{y0, y1, y1 + 1, y0 + 1}, 4);
            Area check = new Area(p);
            if(check.isEmpty()){
                p = new Polygon(new int[]{x0, x1, x1 + 1, x0 + 1}, new int[]{y0, y1, y1, y0}, 4);
            }
            obstacles.add(new SimpleObstacle(p));
        }
        for (Polygon p : obstacle) {
            obstacles.add(new SimpleObstacle(p));
        }
        for (int i = 0; i < xs.length; i++) {
            spawns.add(new Spawn(xs[i], ys[i], rots[i], colors[i]));
        }
    }

    public int nbrPlayers(){
        return spawns.size();
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    public List<Spawn> getSpawns() {
        return spawns;
    }

    public static class Spawn {
        public final Point pos;
        public final double dir;
        public final Color color;

        Spawn(int x, int y, double dir, Color color) {
            this.pos = new Point(x, y);
            this.dir = dir;
            this.color = color;
        }
    }

    static class MapMenu extends JPanel {

        final JButton[] maps;
        final Game parent;
        final JSpinner spinner;
        final JButton back;
        final JSpinner bots;

        MapMenu(Game parent){
            this.parent = parent;
            this.back = new JButton("Back");
            this.back.addActionListener(this::back);
            this.setLayout(new GridBagLayout());
            SpinnerModel model = new SpinnerNumberModel(2, 0, 4, 1);
            spinner = new JSpinner(model);
            SpinnerModel modelBots = new SpinnerNumberModel(0, 0, 4, 1);
            bots = new JSpinner(modelBots);
            maps = new JButton[Map.values().length];
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 0;
            c.insets = new Insets(10, 10, 10, 10);
            this.add(new JLabel("Players:"), c);
            c.gridx = 1;
            this.add(spinner, c);
            c.gridx = 0;
            c.gridy = 1;
            this.add(new JLabel("Bots:"), c);
            c.gridx = 1;
            this.add(bots, c);
            for(int i = 0; i < Map.values().length; i++){
                maps[i] = new JButton(Map.values()[i].toString());
                maps[i].addActionListener(this::mapChoose);
                c.gridy = i+2;
                this.add(maps[i], c);
            }
            ChangeListener listener = changeEvent -> {
                int tot = ((int) spinner.getValue() + (int) bots.getValue());
                if(tot < 2){
                    if(changeEvent.getSource() instanceof JSpinner){
                        JSpinner src = (JSpinner) changeEvent.getSource();
                        src.setValue(src.getNextValue());
                    }
                }
                for(int i = 0; i < Map.values().length; i++){
                    if(Map.values()[i].nbrPlayers() < tot) maps[i].setEnabled(false);
                    else maps[i].setEnabled(true);
                }
            };
            spinner.addChangeListener(listener);
            bots.addChangeListener(listener);
            c.gridx = 2;
            c.gridy = GridBagConstraints.RELATIVE;
            c.anchor = GridBagConstraints.LAST_LINE_END;
            this.add(back);
        }

        private void back(ActionEvent event) {
            parent.changeMenu(new Menu(parent));
        }

        private void mapChoose(ActionEvent event){
            if(event.getSource() instanceof  JButton){
                JButton button = (JButton) event.getSource();
                for(Map m : Map.values()){
                    if(button.getText().equals(m.toString())){
                        parent.changeMenu(new GamePanel(m));
                        for(int i = 0; i < (int) spinner.getValue(); i++){
                            parent.addChar(new PlayerChars(OptionsMenu.PlayerMove.values()[i]));
                        }
                        for(int i = 0; i < (int) bots.getValue(); i++){
                            parent.addChar(new PathFindingBot());
                        }
                        Thread t = new Thread(parent);
                        t.start();
                    }
                }
            }
        }
    }
}

