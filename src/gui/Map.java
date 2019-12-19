package gui;

import player.Chars;
import player.Obstacle;
import player.SimpleObstacle;

import javax.swing.*;
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
            new Color[]{Color.red, Color.blue, Color.green, Color.yellow}),
    Map3(new Polygon(new int[]{0, 200, 400, 600, 600, 400, 200, 0}, new int[]{200, 0, 0, 200, 400, 600, 600, 400, 200}, 8),
            new Polygon[]{

            },
            new int[]{100, 200, 300, 400, 500, 400, 300, 200},
            new int[]{300, 200, 100, 200, 300, 400, 500, 400},
            new double[]{0, 45, 90, 135, 180, 225, 270, 315},
            new Color[]{Color.red, Color.blue, Color.green, Color.yellow, Color.CYAN, Color.orange, Color.pink, Color.BLACK});

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

    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    public List<Spawn> getSpawns() {
        return spawns;
    }

    public static class Spawn {
        public Point pos;
        public double dir;
        public Color color;

        Spawn(int x, int y, double dir, Color color) {
            this.pos = new Point(x, y);
            this.dir = dir;
            this.color = color;
        }
    }

    static class MapMenu extends JPanel {

        JButton[] maps;
        Game parent;
        JSpinner spinner;
        JButton back;

        MapMenu(Game parent){
            this.parent = parent;
            this.back = new JButton("Back");
            this.back.addActionListener(this::back);
            this.setLayout(new GridBagLayout());
            SpinnerModel model = new SpinnerNumberModel(2, 2, 4, 1);
            spinner = new JSpinner(model);
            maps = new JButton[Map.values().length];
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 1;
            c.gridy = 0;
            c.insets = new Insets(10, 10, 10, 10);
            this.add(spinner, c);
            for(int i = 0; i < Map.values().length; i++){
                maps[i] = new JButton(Map.values()[i].toString());
                maps[i].addActionListener(this::mapChoose);
                c.gridy = i+1;
                this.add(maps[i], c);
            }
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
                            parent.addChar(new Chars(OptionsMenu.PlayerMove.values()[i]));
                        }
                        Thread t = new Thread(parent);
                        t.start();
                    }
                }
            }
        }
    }
}
