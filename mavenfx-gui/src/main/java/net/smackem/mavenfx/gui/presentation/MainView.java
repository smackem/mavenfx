package net.smackem.mavenfx.gui.presentation;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.smackem.mavenfx.gui.application.BoardViewModel;
import net.smackem.mavenfx.gui.application.MainViewModel;
import net.smackem.mavenfx.gui.application.PathViewModel;
import net.smackem.mavenfx.gui.util.Views;
import net.smackem.mavenfx.model.Cell;
import net.smackem.mavenfx.model.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * @author pbo
 */
public class MainView extends BorderPane {

    private static Logger log = LoggerFactory.getLogger(MainView.class);
    private final MainViewModel model;
    private final Stage mainStage;

    @FXML
    private Pane boardPane;

    @FXML
    private Slider pathCountSlider;

    @FXML
    private ListView<PathViewModel> pathsListView;

    /**
     * Initializes a new instance of {@link MainView}.
     */
    public MainView(Stage mainStage) {
        Views.loadFxml(this, "fxml/MainView.fxml");

        this.mainStage = mainStage;
        this.model = new MainViewModel();

        final BoardViewModel boardViewModel = this.model.getBoardViewModel();
        final BoardView boardView = new BoardView(boardViewModel);
        this.boardPane.getChildren().add(boardView);

        this.pathCountSlider.valueProperty().bindBidirectional(boardViewModel.pathCountProperty());
        this.pathsListView.setCellFactory(listView -> new ColorRectCell());
        this.pathsListView.itemsProperty().setValue(boardViewModel.getPaths());
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

    private static class ColorRectCell extends ListCell<PathViewModel> {
        @Override
        public void updateItem(PathViewModel item, boolean empty) {
            super.updateItem(item, empty);

            if (item == null || empty) {
                setGraphic(null);
                setText(null);
                return;
            }

            final Rectangle rect = new Rectangle(16, 16);
            rect.setFill(Color.RED);
            setGraphic(rect);
            setText(Double.toString(item.getTotalCost()));
        }
    }
}
