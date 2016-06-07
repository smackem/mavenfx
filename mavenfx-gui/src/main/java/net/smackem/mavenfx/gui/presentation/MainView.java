package net.smackem.mavenfx.gui.presentation;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.smackem.mavenfx.gui.application.MainViewModel;
import net.smackem.mavenfx.gui.util.Views;

/**
 * @author pbo
 */
public class MainView extends BorderPane {

    private static Logger log = LoggerFactory.getLogger(MainView.class);
    private final MainViewModel model;
    private final Stage mainStage;

    @FXML
    private Pane centerPane;

    /**
     * Initializes a new instance of {@link MainView}.
     */
    public MainView(Stage mainStage) {
        Views.loadFxml(this, "fxml/MainView.fxml");

        this.mainStage = mainStage;
        this.model = new MainViewModel();

        final BoardView boardView = new BoardView(this.model.getBoardViewModel());
        this.centerPane.getChildren().add(boardView);
    }

    /////////////////////////////////////////////////////////////////

    @FXML
    private void onOpenAction(ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        final File file = fileChooser.showOpenDialog(this.mainStage);

        if (file != null) {
            try {
                this.model.loadBoardFromImage(file.getAbsolutePath());
            } catch (IOException e) {
                log.error("Error opening " + file.getPath(), e);
                new Alert(AlertType.ERROR, e.getMessage(), ButtonType.CLOSE).showAndWait();
            }
        }
    }

    @FXML
    private void onCreateAction(ActionEvent event) {
        this.model.getBoardViewModel().createNewBoard(400, 400);
    }
}
