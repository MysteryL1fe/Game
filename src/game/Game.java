package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.util.*;
import java.util.List;
import menu.Menu;
import utils.TileColor;

public class Game extends JFrame implements MouseListener {
    private static final int TILE_WIDTH = 40, TILE_HEIGHT = 40;
    private static final int SOUTH_WIDTH = 200, SOUTH_HEIGHT = 75;
    private final int WIDTH, HEIGHT;
    private final List<Tile> checkedTiles = new ArrayList<>();
    private final List<Tile> tilesForCheck = new ArrayList<>();
    private final List<Tile> suitableTiles = new ArrayList<>();
    private final Set<Tile> tilesForRepaint = new HashSet<>();
    private final Menu menu;
    private final int maxX, maxY, colorCnt, hardness;
    private final Tile[][] tiles;
    private TimerToEndThread timerToEnd;
    private SpawnNewTilesThread spawnNewTiles;
    private final JTextArea timerTextArea, scoreTextArea;
    private int score = 0;

    public Game(Menu menu, int maxX, int maxY, int colorCnt, int hardness) {
        this.menu = menu;
        this.maxX = maxX;
        this.maxY = maxY;
        this.colorCnt = colorCnt;
        this.hardness = hardness;

        WIDTH = TILE_WIDTH * maxX;
        HEIGHT = TILE_HEIGHT * maxY + SOUTH_HEIGHT;
        setSize(WIDTH, HEIGHT);
        setUndecorated(true);
        setLayout(new BorderLayout());
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setFocusable(false);
        setFocusTraversalKeysEnabled(false);
        addMouseListener(this);

        JPanel panel = new JPanel(new GridLayout(2, 2));

        timerTextArea = new JTextArea();
        timerTextArea.setEditable(false);
        panel.add(timerTextArea);

        scoreTextArea = new JTextArea("Счёт: 0");
        scoreTextArea.setEditable(false);
        panel.add("score", scoreTextArea);

        JButton menuBtn = new JButton("Главное меню");
        menuBtn.setSize(SOUTH_WIDTH, SOUTH_HEIGHT);
        menuBtn.addActionListener(new MenuBtnListener());
        panel.add("menu", menuBtn);

        this.add(panel, BorderLayout.SOUTH);

        tiles = new Tile[maxX][maxY];
        createTiles();

        timerToEnd = new TimerToEndThread();
        timerToEnd.start();

        spawnNewTiles = new SpawnNewTilesThread();
        spawnNewTiles.start();
    }

    private void createTiles() {
        Random random = new Random();
        for (int i = 0; i < maxX; i++) {
            for (int j = 0; j < maxY; j++) {
                tiles[i][j] = new Tile(i, j,
                        TILE_WIDTH, TILE_HEIGHT,
                        TileColor.values()[random.nextInt(colorCnt)], this);
                tilesForRepaint.add(tiles[i][j]);
            }
        }
    }

    public void tilePressed(Tile tile) {
        if (tile.getTileColor() == null) return;

        tilesForCheck.add(tile);
        suitableTiles.add(tile);
        while (!tilesForCheck.isEmpty()) {
            List<Tile> checkingTiles = new ArrayList<>(tilesForCheck);
            for (Tile tileForCheck : checkingTiles) {
                checkNeighbours(tileForCheck);
                tilesForCheck.remove(tileForCheck);
                checkedTiles.add(tileForCheck);
            }
        }
        checkedTiles.clear();

        if (suitableTiles.size() < 2) {
            suitableTiles.clear();
            return;
        }
        for (Tile suitableTile : suitableTiles) {
            suitableTile.setTileColor(null);
            tilesForRepaint.add(suitableTile);
        }

        moveTiles();
        repaint();

        score += suitableTiles.size();
        suitableTiles.clear();

        checkWinning(false);
    }

    private void moveTiles() {
        for (int y = maxY - 1; y >= 0; y--) {
            for (int x = 0; x < maxX; x++) {
                int yOffset = 0;
                while (y + yOffset < maxY - 1 && tiles[x][y + yOffset + 1].getTileColor() == null) {
                    yOffset++;
                }
                if (yOffset > 0) {
                    tiles[x][y + yOffset].setTileColor(tiles[x][y].getTileColor());
                    tiles[x][y].setTileColor(null);
                    tilesForRepaint.add(tiles[x][y]);
                }
            }
        }

        for (int x = 0; x < maxX; x++) {
            if (tiles[x][maxY - 1].getTileColor() == null) {
                int xOffset = 0;
                while (x + xOffset < maxX - 1 && tiles[x + xOffset][maxY - 1].getTileColor() == null) {
                    xOffset++;
                }
                if (tiles[x + xOffset][maxY - 1].getTileColor() != null) {
                    for (int y = 0; y < maxY; y++) {
                        tiles[x][y].setTileColor(tiles[x + xOffset][y].getTileColor());
                        tiles[x + xOffset][y].setTileColor(null);
                        tilesForRepaint.add(tiles[x][y]);
                        tilesForRepaint.add(tiles[x + xOffset][y]);
                    }
                } else {
                    break;
                }
            }
        }
    }

    private void checkNeighbours(Tile tile) {
        if (tile.getX() > 0 && checkTile(tile, tiles[tile.getX() - 1][tile.getY()])) {
            suitableTiles.add(tiles[tile.getX() - 1][tile.getY()]);
            tilesForCheck.add(tiles[tile.getX() - 1][tile.getY()]);
        }
        if (tile.getY() > 0 && checkTile(tile, tiles[tile.getX()][tile.getY() - 1])) {
            suitableTiles.add(tiles[tile.getX()][tile.getY() - 1]);
            tilesForCheck.add(tiles[tile.getX()][tile.getY() - 1]);
        }
        if (tile.getY() < maxY - 1 && checkTile(tile, tiles[tile.getX()][tile.getY() + 1])) {
            suitableTiles.add(tiles[tile.getX()][tile.getY() + 1]);
            tilesForCheck.add(tiles[tile.getX()][tile.getY() + 1]);
        }
        if (tile.getX() < maxX - 1 && checkTile(tile, tiles[tile.getX() + 1][tile.getY()])) {
            suitableTiles.add(tiles[tile.getX() + 1][tile.getY()]);
            tilesForCheck.add(tiles[tile.getX() + 1][tile.getY()]);
        }
    }

    private boolean checkTile(Tile tile, Tile checkingTile) {
        return checkingTile != null && checkingTile.getTileColor() == tile.getTileColor() &&
                !tilesForCheck.contains(checkingTile) && !checkedTiles.contains(checkingTile);
    }

    public void paint(Graphics g) {
        super.paint(g);
        scoreTextArea.setText(String.format("Счёт: %s", score));

        for (Tile tile : tilesForRepaint) tile.paintItself(g);
    }

    private void checkWinning(boolean timerExpired) {
        if (tiles[0][maxY - 1].getTileColor() == null) gameOver(true);
        else if (timerExpired) gameOver(isWon());
    }

    private boolean isWon() {
        double scorePercent = ((double) score) / (maxX * maxY);
        return (hardness == 0 && colorCnt > 4 && scorePercent > 0.425)
                || (hardness == 0 && scorePercent > 0.85)
                || (hardness == 1 && colorCnt > 4 && scorePercent > 0.5)
                || (hardness == 1 && scorePercent > 1)
                || (hardness == 2 && colorCnt > 4 && scorePercent > 0.575)
                || (hardness == 2 && scorePercent > 1.15);
    }

    private void gameOver(boolean isWon) {
        try {
            timerToEnd.stopTimer();
        } catch (Exception ignored) {}
        try {
            spawnNewTiles.stopTimer();
        } catch (Exception ignored) {}
        removeMouseListener(this);

        if (isWon) writeEndText("Ура!", "Победа");
        else writeEndText("Анлаки", "...");
    }

    public void writeEndText(String text1, String text2) {
        Graphics2D g2 = (Graphics2D) getGraphics();
        Font font = new Font(null, Font.BOLD, 50);
        FontRenderContext context = g2.getFontRenderContext();
        int textWidth = (int) font.getStringBounds(text1, context).getWidth();
        LineMetrics ln = font.getLineMetrics(text1, context);
        int textHeight = (int) (ln.getAscent() + ln.getDescent());
        int x1 = (WIDTH - textWidth) / 2;
        int y1 = (int) ((HEIGHT + textHeight) / 2 - ln.getDescent()) - textHeight / 2;
        textWidth = (int) font.getStringBounds(text2, context).getWidth();
        ln = font.getLineMetrics(text2, context);
        textHeight = (int) (ln.getAscent() + ln.getDescent());
        int x2 = (WIDTH - textWidth) / 2;
        int y2 = (int) ((HEIGHT + textHeight) / 2 - ln.getDescent()) + textHeight / 2;
        g2.setColor(Color.BLACK);
        g2.setFont(font);
        g2.drawString(text1, x1, y1);
        g2.drawString(text2, x2, y2);
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getX() < TILE_WIDTH * maxX && e.getY() < TILE_HEIGHT * maxY) {
            tilePressed(tiles[e.getX() / TILE_WIDTH][e.getY() / TILE_HEIGHT]);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    private class MenuBtnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                timerToEnd.stopTimer();
            } catch (Exception ignored) {}
            try {
                spawnNewTiles.stopTimer();
            } catch (Exception ignored) {}
            Game.this.setVisible(false);
            Game.this.menu.setVisible(true);
        }
    }

    private class TimerToEndThread extends Thread implements Runnable {

        @Override
        public void run() {
            super.run();

            Thread thisThread = Thread.currentThread();
            int time = calcTime();
            while (timerToEnd == thisThread && time >= 0) {
                try {
                    timerTextArea.setText(String.format("Время: %s", time));
                    sleep(1000);
                    time--;
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                    break;
                }
            }
            checkWinning(true);
        }

        private int calcTime() {
            return maxX >= 40 && maxY >= 15 && colorCnt > 4 ? 180
                    : maxX >= 40 && maxY >= 15 ? 150
                    : maxX >= 40 && colorCnt > 4 ? 150
                    : maxX >= 40 ? 120
                    : maxX >= 20 && maxY >= 15 && colorCnt > 4 ? 150
                    : maxX >= 20 && maxY >= 15 ? 120
                    : maxX >= 20 && colorCnt > 4 ? 120
                    : colorCnt > 4 ? 90 : 30;
        }

        public void stopTimer() {
            timerToEnd = null;
        }
    }

    private class SpawnNewTilesThread extends Thread implements Runnable {
        @Override
        public void run() {
            super.run();

            Thread thisThread = Thread.currentThread();
            int time = calcTime();
            int spawnCnt = (int) Math.ceil((double) maxX * maxY / 10);

            while (true) {
                try {
                    sleep(time * 1000);
                    if (spawnNewTiles != thisThread) break;
                    try {
                        Game.this.removeMouseListener(Game.this);
                    } catch (Exception ignored) {}
                    sleep(100);
                    if (spawnNewTiles != thisThread) break;

                    List<Tile> emptyTilesList = new ArrayList<>();
                    for (Tile[] tiles1 : tiles) {
                        for (Tile tile : tiles1) {
                            if (tile.getTileColor() == null) {
                                emptyTilesList.add(tile);
                            }
                        }
                    }
                    Random random = new Random();
                    if (emptyTilesList.size() <= spawnCnt) {
                        for (Tile tile : emptyTilesList) {
                            tile.setTileColor(TileColor.values()[random.nextInt(colorCnt)]);
                        }
                    } else {
                        for (int i = 0; i < spawnCnt; i++) {
                            Tile tile = emptyTilesList.get(random.nextInt(emptyTilesList.size()));
                            tile.setTileColor(TileColor.values()[random.nextInt(colorCnt)]);
                            emptyTilesList.remove(tile);
                        }
                    }

                    moveTiles();
                    repaint();

                    Game.this.addMouseListener(Game.this);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                    break;
                }
            }
        }

        private int calcTime() {
            return maxX >= 40 && maxY >= 15 && colorCnt > 4 ? 60
                    : maxX >= 40 && maxY >= 15 ? 50
                    : maxX >= 40 && colorCnt > 4 ? 50
                    : maxX >= 40 ? 40
                    : maxX >= 20 && maxY >= 15 && colorCnt > 4 ? 50
                    : maxX >= 20 && maxY >= 15 ? 40
                    : maxX >= 20 && colorCnt > 4 ? 40
                    : colorCnt > 4 ? 30 : 10;
        }

        public void stopTimer() {
            spawnNewTiles = null;
        }
    }
}
