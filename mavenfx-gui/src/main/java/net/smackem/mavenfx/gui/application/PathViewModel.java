package net.smackem.mavenfx.gui.application;

import javafx.scene.paint.Paint;
import net.smackem.mavenfx.model.Cell;
import net.smackem.mavenfx.model.Path;

import java.util.Collection;
import java.util.Objects;

/**
 * @author pbo
 */
public final class PathViewModel {
    private final Path<Cell> path;
    private final Collection<Cell> cells;
    private final Paint stroke;

    public PathViewModel(Path<Cell> path, Paint stroke) {
        Objects.requireNonNull(path);
        this.path = path;
        this.cells = path.getNodes();
        this.stroke = stroke;
    }

    public Collection<Cell> getCells() {
        return this.cells;
    }

    public double getTotalCost() {
        return this.path.getTotalCost();
    }

    public Paint getStroke() {
        return this.stroke;
    }
}
