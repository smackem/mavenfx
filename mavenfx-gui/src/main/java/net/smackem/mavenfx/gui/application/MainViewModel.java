package net.smackem.mavenfx.gui.application;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.paint.Color;
import net.smackem.mavenfx.model.Board;

public class MainViewModel {
    private final ReadOnlyObjectWrapper<Board> board = new ReadOnlyObjectWrapper<>(null);

    public final ReadOnlyObjectProperty<Board> boardProperty() {
        return this.board.getReadOnlyProperty();
    }

    public void createNewBoard(int width, int height) {
        this.board.set(new Board(width, height));
    }

    public void loadBoardFromImage(String imagePath) throws IOException {
        final Image image = loadImage(imagePath);
        final int width = (int) image.getWidth();
        final int height = (int) image.getHeight();
        final PixelReader reader = image.getPixelReader();
        final int[] buffer = new int[width * height];

        reader.getPixels(0, 0, width, height, WritablePixelFormat.getIntArgbInstance(), buffer, 0, width);
        final Board board = Board.fromBuffer(buffer, width, height, pixel -> {
            final Color color = Color.rgb((pixel >> 16) & 0xff, (pixel >> 8) & 0xff, (pixel >> 0) & 0xff);
            return Integer.MAX_VALUE - (int)(color.getBrightness() * Integer.MAX_VALUE);
        });

        this.board.set(board);
    }

    /////////////////////////////////////////////////////////////////

    private static Image loadImage(String imagePath) throws IOException {
        try (final InputStream is = new FileInputStream(imagePath)) {
            return new Image(is);
        }
    }

    private static Image scaleImage(Image image) {
        final ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(image.getWidth() / 8);
        imageView.setFitHeight(image.getHeight() / 8);
        imageView.setSmooth(true);
        return imageView.snapshot(null, null);
    }
}
