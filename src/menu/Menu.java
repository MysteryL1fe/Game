package menu;

import game.Game;
import rules.Rules;
import settings.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu extends JFrame {
    private int maxX = 10, maxY = 8, colorCnt = 3, hardness = 0;

    public Menu() {
        setUndecorated(true);
        setLayout(new GridLayout(4, 1));
        getContentPane().setBackground(Color.LIGHT_GRAY);
        setSize(500, 400);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setFocusable(false);
        setFocusTraversalKeysEnabled(false);

        JButton playBtn = new JButton("Играть");
        playBtn.addActionListener(new PlayBtnListener());
        this.add(playBtn);

        JButton settingsBtn = new JButton("Настройки");
        settingsBtn.addActionListener(new SettingsBtnListener());
        this.add(settingsBtn);

        JButton rulesBtn = new JButton("Правила");
        rulesBtn.addActionListener(new RulesBtnListener());
        this.add(rulesBtn);

        JButton exitBtn = new JButton("Выйти");
        exitBtn.addActionListener(e -> System.exit(0));
        this.add(exitBtn);
    }

    public int getMaxX() {
        return maxX;
    }

    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }

    public int getColorCnt() {
        return colorCnt;
    }

    public void setColorCnt(int colorCnt) {
        this.colorCnt = colorCnt;
    }

    public int getHardness() {
        return hardness;
    }

    public void setHardness(int hardness) {
        this.hardness = hardness;
    }

    private class PlayBtnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Menu.this.setVisible(false);
            Game game = new Game(Menu.this, maxX, maxY, colorCnt, hardness);
            game.setLocation(0, 0);
            game.setVisible(true);
        }
    }

    private class SettingsBtnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Menu.this.setVisible(false);
            Settings settings = new Settings(Menu.this);
            settings.setLocationRelativeTo(null);
            settings.setVisible(true);
        }
    }

    private class RulesBtnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Menu.this.setVisible(false);
            Rules rules = new Rules(Menu.this);
            rules.setLocationRelativeTo(null);
            rules.setVisible(true);
        }
    }
}
