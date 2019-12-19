package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Menu extends JPanel {

    private JButton play;
    private JButton multiplayer;
    private JButton options;
    private Game parent;

    public Menu(Game parent){
        super();
        this.parent = parent;
        this.play = new JButton("Play");
        this.multiplayer = new JButton("Multiplayer");
        this.options = new JButton("Options");
        this.play.addActionListener(this::playButton);
        this.multiplayer.addActionListener(this::multiplayerButton);
        this.options.addActionListener(this::optionsButton);
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(10, 10, 10, 10);
        this.add(play, c);
        c.gridy = 1;
        this.add(multiplayer, c);
        c.gridy = 2;
        this.add(options, c);
    }

    private void playButton(ActionEvent event){
        parent.changeMenu(new Map.MapMenu(parent));
    }

    private void multiplayerButton(ActionEvent event){

    }

    private void optionsButton(ActionEvent event){
        parent.changeMenu(new OptionsMenu(parent));
    }
}
