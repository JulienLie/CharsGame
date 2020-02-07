package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Menu extends JPanel {

    private final Game parent;

    public Menu(Game parent){
        super();
        this.parent = parent;
        JButton play = new JButton("Play");
        JButton multiplayer = new JButton("Multiplayer");
        JButton options = new JButton("Options");
        play.addActionListener(this::playButton);
        multiplayer.addActionListener(this::multiplayerButton);
        options.addActionListener(this::optionsButton);
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
        System.err.println("[WIP] Not yet implemented");
    }

    private void optionsButton(ActionEvent event){
        parent.changeMenu(new OptionsMenu(parent));
    }
}
