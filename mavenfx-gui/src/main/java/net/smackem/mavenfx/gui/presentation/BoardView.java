package net.smackem.mavenfx.gui.presentation;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import net.smackem.mavenfx.gui.util.Views;
import net.smackem.mavenfx.model.Board;
import net.smackem.mavenfx.model.Cell;

/**
 * @author pbo
 */
public class BoardView extends ScrollPane {

    private static final int CELL_LENGTH = 8;
    private final ObjectProperty<Board> boardProperty = new SimpleObjectProperty<>(null);

    @FXML
    @SuppressWarnings("unused")
    private Canvas canvas;

    public BoardView() {
        Views.loadFxml(this, "fxml/BoardControl.fxml");

        boardProperty.addListener((prop, oldVal, newVal) -> redrawBoard());
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onMouseClicked);
    }

    public ObjectProperty<Board> boardProperty() {
        return this.boardProperty;
    }

    /////////////////////////////////////////////////////////////////

    private void onMouseClicked(MouseEvent event) {
        final Board board = this.boardProperty.get();

        if (board == null) {
            return;
        }

        final int col = (int)(event.getX() / CELL_LENGTH);
        final int row = (int)(event.getY() / CELL_LENGTH);

        final Cell cell = board.getCell(col, row);

        if (cell != null) {
            cell.setWeight(cell.getWeight() == 0 ? 1 : 0);
        }

        redrawBoard();
        event.consume();
    }

    private void redrawBoard() {
        final int boardWidth;
        final int boardHeight;
        final Board board = this.boardProperty.get();

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

        if (board != null) {
            final GraphicsContext dc = this.canvas.getGraphicsContext2D();

            dc.clearRect(0, 0, canvasWidth, canvasHeight);

            dc.setFill(Color.CORNFLOWERBLUE);

            for (int row = 0; row < boardHeight; row++) {
                for (int col = 0; col < boardWidth; col++) {
                    final Cell cell = board.getCell(col, row);

                    if (cell != null && cell.getWeight() > 0) {
                        dc.fillRect(col * CELL_LENGTH, row * CELL_LENGTH, CELL_LENGTH, CELL_LENGTH);
                    }
                }
            }

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
    }
}
