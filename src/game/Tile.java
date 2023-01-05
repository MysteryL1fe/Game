package game;

import utils.TileColor;

import java.awt.*;

public class Tile {
    private final int WIDTH, HEIGHT;
    private final int x, y;
    private TileColor tileColor;
    private final Game game;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public TileColor getTileColor() {
        return tileColor;
    }

    public void setTileColor(TileColor tileColor) {
        this.tileColor = tileColor;
    }

    public Tile(int x, int y, int width, int height, TileColor tileColor, Game game) {
        this.x = x;
        this.y = y;
        this.WIDTH = width;
        this.HEIGHT = height;
        this.tileColor = tileColor;
        this.game = game;
    }

    public void paintItself(Graphics g) {
        g.setColor(getColor());
        g.fillOval(x * WIDTH, y * HEIGHT, WIDTH, HEIGHT);
    }

    private Color getColor() {
        if (this.tileColor == null) {
            return new Color(0xeeeeee);
        }
        return switch (this.tileColor) {
            case RED -> Color.red;
            case BLUE -> Color.blue;
            case Green -> Color.green;
            case PURPLE -> Color.magenta;
            case ORANGE -> Color.orange;
            case YELLOW -> Color.yellow;
            case BLACK -> Color.gray;
            case WHITE -> Color.white;
        };
    }
}
