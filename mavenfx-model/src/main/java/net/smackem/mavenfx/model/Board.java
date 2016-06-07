package net.smackem.mavenfx.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author pbo
 */
public final class Board {
    private static final Logger log = LoggerFactory.getLogger(Board.class);
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

    public Collection<Path<Cell>> findPaths(Cell origin, Cell destination, int maxPathCount) {
        Objects.requireNonNull(origin);
        Objects.requireNonNull(destination);

        final Set<Cell> usedCells = new HashSet<>();
        final Collection<Path<Cell>> result = new LinkedList<>();

        while (maxPathCount-- > 0) {
            final Path<Cell> path = Path.findPath(origin, destination,
                    (originPath, destCell) -> {
                        double weight = calculateEdgeWeight(originPath, destCell);
                        if (destCell != destination && usedCells.contains(destCell))
                            weight = Integer.MAX_VALUE;
                        return weight;
                    },
                    cell -> calculateDistance(cell, destination),
                    this::collectNeighbours);

            if (path == null) {
                log.info("no more paths found. count={}", result.size());
                break;
            }

            usedCells.addAll(path.getNodes());
            result.add(path);
        }

        return result;
    }

    public static Board fromBuffer(int[] buffer, int width, int height, Functions.IntegerMapper weightCalculator) {
        Objects.requireNonNull(buffer);
        Objects.requireNonNull(weightCalculator);
        if (width <= 0)
            throw new IllegalArgumentException("Invalid width");
        if (height <= 0)
            throw new IllegalArgumentException("Invalid height");
        if (buffer.length != width * height)
            throw new IllegalArgumentException("Invalid buffer size");

        final Cell[] cells = new Cell[width * height];

        int index = 0;
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                cells[index] = new Cell(col, row);
                cells[index].setWeight(weightCalculator.apply(buffer[index]));
                index++;
            }
        }

        return new Board(width, height, cells);
    }

    /////////////////////////////////////////////////////////////////

    private Board(int width, int height, Cell[] cells) {
        this.width = width;
        this.height = height;
        this.cells = cells;
    }

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

    private Collection<Cell> collectNeighbours(Cell cell) {
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
