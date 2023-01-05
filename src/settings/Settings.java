package settings;

import exceptions.WrongColorCountException;
import exceptions.WrongHeightException;
import exceptions.WrongWidthException;
import menu.Menu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Settings extends JFrame {
    private final Menu menu;
    private final JTextField maxXTextField, maxYTextField, colorCntTextField;

    public Settings(Menu menu) {
        this.menu = menu;
        setUndecorated(true);
        setLayout(new GridLayout(6, 2));
        getContentPane().setBackground(Color.LIGHT_GRAY);
        setSize(500, 400);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setFocusable(false);
        setFocusTraversalKeysEnabled(false);

        JTextArea maxXTextArea = new JTextArea("Ширина поля (от 5 до 45)");
        maxXTextArea.setEditable(false);
        this.add(maxXTextArea);

        maxXTextField = new JTextField(String.format("%s", menu.getMaxX()));
        this.add(maxXTextField);

        JTextArea maxYTextArea = new JTextArea("Высота поля (от 5 до 20)");
        maxYTextArea.setEditable(false);
        this.add(maxYTextArea);

        maxYTextField = new JTextField(String.format("%s", menu.getMaxY()));
        this.add(maxYTextField);

        JTextArea colorCntTextArea = new JTextArea("Количество цветов (от 3 до 8)");
        colorCntTextArea.setEditable(false);
        this.add(colorCntTextArea);

        colorCntTextField = new JTextField(String.format("%s", menu.getColorCnt()));
        this.add(colorCntTextField);

        JTextArea hardnessTextArea = new JTextArea("Выберите сложность");
        hardnessTextArea.setEditable(false);
        this.add(hardnessTextArea);

        JRadioButton easyBtn = new JRadioButton("Легко", menu.getHardness() == 0);
        easyBtn.addActionListener(new RadioBtnListener(0));
        this.add(easyBtn);

        JRadioButton normalBtn = new JRadioButton("Средне", menu.getHardness() == 1);
        normalBtn.addActionListener(new RadioBtnListener(1));
        this.add(normalBtn);

        JRadioButton hardBtn = new JRadioButton("Сложно", menu.getHardness() == 2);
        hardBtn.addActionListener(new RadioBtnListener(2));
        this.add(hardBtn);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(easyBtn);
        buttonGroup.add(normalBtn);
        buttonGroup.add(hardBtn);

        JButton menuBtn = new JButton("Главное меню");
        menuBtn.addActionListener(new MenuBtnListener());
        this.add(menuBtn);
    }

    private class MenuBtnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                menu.setMaxX(getMaxX());
                maxXTextField.setBackground(Color.white);
                menu.setMaxY(getMaxY());
                maxYTextField.setBackground(Color.white);
                menu.setColorCnt(getColorCnt());
                colorCntTextField.setBackground(Color.white);
                Settings.this.setVisible(false);
                menu.setVisible(true);
            } catch (WrongWidthException exception) {
                maxXTextField.setBackground(Color.red);
            } catch (WrongHeightException exception) {
                maxYTextField.setBackground(Color.red);
            } catch (WrongColorCountException exception) {
                colorCntTextField.setBackground(Color.red);
            }
        }

        private int getMaxX() throws WrongWidthException {
            try {
                int maxX = Integer.parseInt(maxXTextField.getText());
                if (maxX < 5 || maxX > 45) {
                    throw new WrongWidthException("Не удовлетворяет критериям");
                }
                return maxX;
            } catch (NumberFormatException e) {
                throw new WrongWidthException("Не является числом");
            }
        }

        private int getMaxY() throws WrongHeightException {
            try {
                int maxY = Integer.parseInt(maxYTextField.getText());
                if (maxY < 5 || maxY > 20) {
                    throw new WrongHeightException("Не удовлетворяет критериям");
                }
                return maxY;
            } catch (NumberFormatException e) {
                throw new WrongHeightException("Не является числом");
            }
        }

        private int getColorCnt() throws WrongColorCountException {
            try {
                int colorCnt = Integer.parseInt(colorCntTextField.getText());
                if (colorCnt < 0 || colorCnt > 8) {
                    throw new WrongColorCountException("Не удовлетворяет критериям");
                }
                return colorCnt;
            } catch (NumberFormatException e) {
                throw new WrongColorCountException("Не является числом");
            }
        }
    }

    private class RadioBtnListener implements ActionListener {
        private final int hardness;
        public RadioBtnListener(int hardness) {
            this.hardness = hardness;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            menu.setHardness(hardness);
        }
    }
}