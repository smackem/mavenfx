package net.smackem.mavenfx.model;

/**
 * @author pbo
 */
public final class Cell {
    private final int x;
    private final int y;
    private int weight;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getWeight() {
        return this.weight;
    }

    public void setWeight(int value) {
        this.weight = value;
    }
}
