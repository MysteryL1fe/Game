package rules;

import menu.Menu;
import utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Rules extends JFrame {
    private final Menu menu;

    public Rules(Menu menu) {
        this.menu = menu;
        setUndecorated(true);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.LIGHT_GRAY);
        setSize(1000, 800);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setFocusable(false);
        setFocusTraversalKeysEnabled(false);

        Font font = new Font(null, Font.PLAIN, 24);
        JTextArea rulesTextArea = new JTextArea(Utils
                .stringArrToLine(Utils.readLinesFromFile("src/rules/rules.txt")));
        rulesTextArea.setEditable(false);
        rulesTextArea.setFont(font);
        this.add(rulesTextArea, BorderLayout.CENTER);

        JButton menuBtn = new JButton("Главное меню");
        menuBtn.addActionListener(new MenuBtnListener());
        this.add(menuBtn, BorderLayout.SOUTH);
    }

    private class MenuBtnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Rules.this.setVisible(false);
            menu.setVisible(true);
        }
    }
}
