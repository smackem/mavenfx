package net.smackem.mavenfx.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.smackem.mavenfx.gui.presentation.MainView;

/**
 * @author pbo
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        final MainView root = new MainView(stage);
        stage.setScene(new Scene(root, 800, 600));
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
