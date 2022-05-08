package edu.hitsz.view;

import edu.hitsz.application.Game;
import edu.hitsz.application.Main;
import edu.hitsz.application.difficulty.Easy;
import edu.hitsz.application.difficulty.Hard;
import edu.hitsz.application.difficulty.Medium;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class DifficultySelect {
    private JTextField topText;
    private JPanel buttonPanel3;
    private JButton hardButton;
    private JPanel buttonPanel1;
    private JPanel buttonPanel2;
    private JButton mediumButton;
    private JButton easyButton;
    private JPanel mainPanel;
    private JPanel soundPanel;
    private JComboBox soundComboBox;

    private Game game;

    private boolean isVisible = true;

    private boolean isSoundOpen = true;//默认开启音效
    public DifficultySelect() {

        easyButton.addActionListener(e -> {
            game = new Easy();
            isVisible = false;
            synchronized (Main.object){
                Main.object.notify();
            }
        });
        mediumButton.addActionListener(e -> {
            game = new Medium();
            isVisible = false;
            synchronized (Main.object){
                Main.object.notify();
            }
        });
        hardButton.addActionListener(e -> {
            game = new Hard();
            isVisible = false;
            synchronized (Main.object){
                Main.object.notify();
            }
        });

        //音量监听器
        soundComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED){
                if ("音效开启".equals(soundComboBox.getSelectedItem())){
                    isSoundOpen = true;
                    System.out.println("音效开启");
                } else if ("音效关闭".equals(soundComboBox.getSelectedItem())) {
                    isSoundOpen = false;
                    System.out.println("音效关闭");
                }
            }
        });
    }

    public JPanel getMainPanel(){
        return mainPanel;
    }

    public Game getGame() {
        return game;
    }

    public boolean getIsVisible(){
        return isVisible;
    }

    public boolean getIsSoundOpen() {
        return isSoundOpen;
    }

}
