package net.smackem.mavenfx.gui.presentation;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import net.smackem.mavenfx.gui.application.MainViewModel;
import net.smackem.mavenfx.gui.util.Views;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author pbo
 */
public class MainView extends BorderPane {

    private static Logger log = LoggerFactory.getLogger(MainView.class);
    private final MainViewModel model;

    @FXML
    private BoardView boardView;

    /**
     * Initializes a new instance of {@link MainView}.
     */
    public MainView() {
        Views.loadFxml(this, "fxml/MainView.fxml");

        this.model = new MainViewModel();
        boardView.boardProperty().bind(this.model.boardProperty());
        setBackground(new Background(new BackgroundFill(Color.AQUA, null, null)));
    }

    /////////////////////////////////////////////////////////////////

    @FXML
    private void onOpenAction(ActionEvent event) {
        log.debug("open");
    }

    @FXML
    private void onCreateAction(ActionEvent event) {
        this.model.createNewBoard(400, 400);
    }
}
