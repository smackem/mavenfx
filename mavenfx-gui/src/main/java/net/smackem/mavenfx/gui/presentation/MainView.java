package net.smackem.mavenfx.gui.presentation;

import javafx.fxml.FXML;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import net.smackem.mavenfx.gui.util.Views;
import net.smackem.mavenfx.model.Board;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author pbo
 */
public class MainView extends BorderPane {

    private final Logger log = LoggerFactory.getLogger(MainView.class);

    @FXML
    @SuppressWarnings("unused")
    private BoardView boardControl;

    /**
     * Initializes a new instance of {@link MainView}.
     */
    public MainView() {
        Views.loadFxml(this, "fxml/MainControl.fxml");

        boardControl.boardProperty().set(new Board(100, 100));
        setBackground(new Background(new BackgroundFill(Color.AQUA, null, null)));
    }
}