package net.smackem.mavenfx.gui.presentation;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import net.smackem.mavenfx.gui.application.BoardViewModel;
import net.smackem.mavenfx.gui.application.PathViewModel;
import net.smackem.mavenfx.gui.util.Views;
import net.smackem.mavenfx.model.Board;
import net.smackem.mavenfx.model.Cell;

/**
 * @author pbo
 */
public class BoardView extends ScrollPane {

    private static final Logger log = LoggerFactory.getLogger(BoardView.class);
    private static final int CELL_LENGTH = 6;
    private final BoardViewModel model;
    private DragState dragState;

    @FXML
    private Canvas canvas;

    @FXML
    private Line dragLine;

    @FXML
    private ImageView imageView;

    public BoardView(BoardViewModel model) {
        Views.loadFxml(this, "fxml/BoardView.fxml");

        this.model = model;
        this.model.boardProperty().addListener((prop, oldVal, newVal) -> {
            resizeCanvas();
            redrawBoard();
        });
        this.model.getPaths().addListener((ListChangeListener<PathViewModel>) ignored -> {
            redrawBoard();
        });

        this.imageView.imageProperty().bind(this.model.imageProperty());
        this.imageView.fitWidthProperty().bind(this.canvas.widthProperty());
        this.imageView.fitHeightProperty().bind(this.canvas.heightProperty());

        this.canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, this::onMousePressed);
        this.canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::onMouseDragged);
        this.canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, this::onMouseReleased);
    }

    /////////////////////////////////////////////////////////////////

    private Board getBoard() {
        return this.model.boardProperty().get();
    }

    private void onMousePressed(MouseEvent event) {
        this.dragState = event.isShiftDown()
            ? new RoutingDragState(event.getX(), event.getY())
            : new DrawingDragState(event.getX(), event.getY());

        event.consume();
    }

    private void onMouseDragged(MouseEvent event) {
        assert this.dragState != null;
        this.dragState.drag(event.getX(), event.getY());
        event.consume();
    }

    private void onMouseReleased(MouseEvent event) {
        assert this.dragState != null;
        this.dragState.finish(event.getX(), event.getY());
        this.dragState = null;
        event.consume();
    }

    private void resizeCanvas() {
        final int boardWidth;
        final int boardHeight;
        final Board board = getBoard();

        if (board == null) {
            boardWidth = 0;
            boardHeight = 0;
        } else {
            boardWidth = board.getWidth();
            boardHeight = board.getHeight();
        }

        final double canvasWidth = boardWidth * CELL_LENGTH;
        final double canvasHeight = boardHeight * CELL_LENGTH;

        canvas.setWidth(canvasWidth + 0.5);
        canvas.setHeight(canvasHeight + 0.5);
    }

    private void redrawBoard() {
        final Board board = getBoard();
        final GraphicsContext dc = this.canvas.getGraphicsContext2D();

        dc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        if (board == null) {
          return;
        }

        final int boardWidth = board.getWidth();
        final int boardHeight = board.getHeight();
        final double canvasWidth = boardWidth * CELL_LENGTH;
        final double canvasHeight = boardHeight * CELL_LENGTH;

        // draw cell weights
        for (int row = 0; row < boardHeight; row++) {
            for (int col = 0; col < boardWidth; col++) {
                final Cell cell = board.getCell(col, row);

                if (cell != null && cell.getWeight() > 0) {
                    dc.setFill(getCellColor(cell));
                    dc.fillRect(col * CELL_LENGTH, row * CELL_LENGTH, CELL_LENGTH, CELL_LENGTH);
                }
            }
        }

        // draw path - best path last, in red
        final List<PathViewModel> paths = this.model.getPaths();

        for (int index = paths.size() - 1; index >= 0; index--) {
            final PathViewModel path = paths.get(index);
            dc.setFill(path.getStroke());

            for (final Cell cell : path.getCells()) {
                dc.fillRect(cell.getX() * CELL_LENGTH, cell.getY() * CELL_LENGTH, CELL_LENGTH, CELL_LENGTH);
            }
        }

        // draw grid
        dc.setStroke(Color.GRAY);
        dc.setLineWidth(1.0);

        for (int row = 0; row <= boardHeight; row++) {
            final double y = row * CELL_LENGTH + 0.5;
            dc.strokeLine(0, y, canvasWidth, y);
        }

        for (int col = 0; col <= boardWidth; col++) {
            final double x = col * CELL_LENGTH + 0.5;
            dc.strokeLine(x, 0, x, canvasHeight);
        }
    }

    private static Color getCellColor(Cell cell) {
        final double ratio = (double) cell.getWeight() / BoardViewModel.BLACK_WEIGHT;
        final int channelValue = 255 - (int) (ratio * 255);
        return Color.rgb(channelValue, channelValue, channelValue);
    }

    private Cell getCellAt(double x, double y) {
        final Board board = getBoard();

        if (board != null) {
            final int col = (int)(x / CELL_LENGTH);
            final int row = (int)(y / CELL_LENGTH);
            return board.getCell(col, row);
        }

        return null;
    }

    private interface DragState {
        void drag(double x, double y);
        void finish(double x, double y);
    }

    private class DrawingDragState implements DragState {
        final int weightToSet;

        DrawingDragState(double x, double y) {
            int weightToSet = 0;

            final Cell cell = getCellAt(x, y);

            if (cell != null) {
                weightToSet = cell.getWeight() == 0 ? BoardViewModel.BLACK_WEIGHT : 0;
                log.debug("Pressed {}/{}: {}", cell.getX(), cell.getY(), cell.getWeight());
            }

            this.weightToSet = weightToSet;
            drag(x, y);
        }

        @Override
        public void drag(double x, double y) {
            final Cell cell = getCellAt(x, y);

            if (cell != null) {
                cell.setWeight(this.weightToSet);
            }

            redrawBoard();
        }

        @Override
        public void finish(double x, double y) { }
    }

    private class RoutingDragState implements DragState {
        final Cell originCell;

        RoutingDragState(double x, double y) {
            this.originCell = getCellAt(x, y);

            if (this.originCell != null) {
                final Point2D point = normalizePosition(x, y);
                dragLine.setStartX(point.getX());
                dragLine.setStartY(point.getY());
                dragLine.setVisible(true);
                drag(x, y);
            }
        }

        @Override
        public void drag(double x, double y) {
            if (this.originCell != null) {
                final Point2D point = normalizePosition(x, y);
                dragLine.setEndX(point.getX());
                dragLine.setEndY(point.getY());
            }
        }

        @Override
        public void finish(double x, double y) {
            if (this.originCell != null) {
                final Cell destCell = getCellAt(x, y);

                if (destCell != null) {
                    model.setOriginAndDestination(originCell, destCell);
                }
            }
            dragLine.setVisible(false);
        }

        Point2D normalizePosition(double x, double y) {
            return new Point2D(
                    x - x % CELL_LENGTH + CELL_LENGTH / 2.0 + 0.5,
                    y - y % CELL_LENGTH  + CELL_LENGTH / 2.0 + 0.5);
        }
    }
}
