package net.smackem.mavenfx.gui.presentation;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import net.smackem.mavenfx.gui.util.Views;
import net.smackem.mavenfx.model.Board;
import net.smackem.mavenfx.model.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author pbo
 */
public class BoardView extends ScrollPane {

    private static final Logger log = LoggerFactory.getLogger(BoardView.class);
    private static final int CELL_LENGTH = 8;
    private final ObjectProperty<Board> boardProperty = new SimpleObjectProperty<>(null);
    private int weightToSet;

    @FXML
    private Canvas canvas;

    @FXML
    private Line dragLine;

    public BoardView() {
        Views.loadFxml(this, "fxml/BoardControl.fxml");

        boardProperty.addListener((prop, oldVal, newVal) -> {
          resizeCanvas();
          redrawBoard();
        });

        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton() == MouseButton.PRIMARY)
                onMousePressed(event);
        });
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if (event.getButton() == MouseButton.PRIMARY)
                onMouseDragged(event);
        });
    }

    public ObjectProperty<Board> boardProperty() {
        return this.boardProperty;
    }

    /////////////////////////////////////////////////////////////////

    private void onMousePressed(MouseEvent event) {
        final Board board = this.boardProperty.get();

        if (board == null) {
            return;
        }

        final int col = (int)(event.getX() / CELL_LENGTH);
        final int row = (int)(event.getY() / CELL_LENGTH);

        final Cell cell = board.getCell(col, row);

        if (cell != null) {
            log.debug("Pressed {}/{}: {}", col, row, cell.getWeight());
            this.weightToSet = cell.getWeight() == 0 ? 1 : 0;
            cell.setWeight(this.weightToSet);
        }

        redrawBoard();
        event.consume();
    }

    private void onMouseDragged(MouseEvent event) {
      final Board board = this.boardProperty.get();

      if (board == null) {
          return;
      }

      final int col = (int)(event.getX() / CELL_LENGTH);
      final int row = (int)(event.getY() / CELL_LENGTH);

      final Cell cell = board.getCell(col, row);

      if (cell != null) {
          cell.setWeight(this.weightToSet);
      }

      redrawBoard();
      event.consume();
    }

    private void resizeCanvas() {
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
    }

    private void redrawBoard() {
        final Board board = this.boardProperty.get();
        final GraphicsContext dc = this.canvas.getGraphicsContext2D();

        dc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        if (board == null) {
          return;
        }

        final int boardWidth = board.getWidth();
        final int boardHeight = board.getHeight();
        final double canvasWidth = boardWidth * CELL_LENGTH;
        final double canvasHeight = boardHeight * CELL_LENGTH;

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
