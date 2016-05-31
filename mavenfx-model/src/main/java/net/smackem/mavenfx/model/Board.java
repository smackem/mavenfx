package net.smackem.mavenfx.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * @author pbo
 */
public final class Board {
    private final Cell[] cells;
    private final int width;
    private final int height;

    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = new Cell[width * height];

        int index = 0;
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                this.cells[index] = new Cell(col, row);
                index++;
            }
        }
    }

    public Cell getCell(int col, int row) {
        if (col >= 0 && col < this.width && row >= 0 && row < this.height)
            return this.cells[row * this.width + col];

        return null;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public Path<Cell> findPath(Cell origin, Cell destination) {
        Objects.requireNonNull(origin);
        Objects.requireNonNull(destination);

        return Path.findPath(origin, destination,
                Board::calculateEdgeWeight,
                cell -> calculateDistance(cell, destination),
                this::collectNeighbours);
    }

    /////////////////////////////////////////////////////////////////

    private static double calculateDistance(Cell node1, Cell node2) {
        final double dx = (double)(node1.getX() - node2.getX());
        final double dy = (double)(node1.getY() - node2.getY());

        return Math.sqrt(dx * dx + dy * dy);
    }

    private static double calculateEdgeWeight(Path<Cell> originPath, Cell destination) {
        final Cell origin = originPath.getHead();
        double distance = calculateDistance(origin, destination);

        // punish diagonals
        if (destination.getX() != origin.getX() && destination.getY() != origin.getY()) {
            distance += 0.7;
        }

        // punish changes of direction
        if (originPath.getTail() != null) {
            final Cell previous = originPath.getTail().getHead();
            final double dx1 = previous.getX() - origin.getX();
            final double dy1 = previous.getY() - origin.getY();
            final double dx2 = origin.getX() - destination.getX();
            final double dy2 = origin.getY() - destination.getY();

            if (dx1 != dx2 || dy1 != dy2) {
                distance += 0.2;
            }
        }

        return distance + destination.getWeight();
    }

    Collection<Cell> collectNeighbours(Cell cell) {
        final int firstX = Math.max(cell.getX() - 1, 0);
        final int lastX = Math.min(cell.getX() + 1, this.width - 1);

        final int firstY = Math.max(cell.getY() - 1, 0);
        final int lastY = Math.min(cell.getY() + 1, this.height - 1);

        final Collection<Cell> neighbours = new ArrayList<>();

        int lineBase = firstY * this.width;

        for (int y = firstY; y <= lastY; y++) {
            for (int x = firstX; x <= lastX; x++) {
                neighbours.add(this.cells[lineBase + x]);
            }

            lineBase += this.width;
        }

        return neighbours;
    }
}
