package net.smackem.mavenfx.gui.application;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javafx.scene.image.Image;

public final class MainViewModel {
    private final BoardViewModel boardViewModel = new BoardViewModel();

    public BoardViewModel getBoardViewModel() {
        return this.boardViewModel;
    }

    public void loadBoardFromImage(String imagePath) throws IOException {
        final Image image = loadImage(imagePath);
        this.boardViewModel.importFromImage(image);
    }

    /////////////////////////////////////////////////////////////////

    private static Image loadImage(String imagePath) throws IOException {
        try (final InputStream is = new FileInputStream(imagePath)) {
            return new Image(is);
        }
    }
}
