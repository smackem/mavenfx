package net.smackem.mavenfx.gui.application;

import java.io.IOException;

import com.sun.javafx.collections.ImmutableObservableList;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.paint.Color;
import net.smackem.mavenfx.model.Board;
import net.smackem.mavenfx.model.Cell;
import net.smackem.mavenfx.model.Path;

public final class BoardViewModel {
    private final ReadOnlyObjectWrapper<Board> board = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<Image> image = new ReadOnlyObjectWrapper<>();
    private final ObservableList<Path<Cell>> paths = FXCollections.observableArrayList();

    public ReadOnlyObjectProperty<Board> boardProperty() {
        return this.board.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<Image> getImage() {
        return this.image;
    }

    public ObservableList<Path<Cell>> getPaths() {
        return FXCollections.unmodifiableObservableList(this.paths);
    }

    public void createNewBoard(int width, int height) {
        this.board.set(new Board(width, height));
        this.paths.clear();
    }

    public void importFromImage(Image image) throws IOException {
        final Image scaledImage = scaleImage(image);
        final int width = (int) scaledImage.getWidth();
        final int height = (int) scaledImage.getHeight();
        final PixelReader reader = scaledImage.getPixelReader();
        final int[] buffer = new int[width * height];

        reader.getPixels(0, 0, width, height, WritablePixelFormat.getIntArgbInstance(), buffer, 0, width);

        final Board board = Board.fromBuffer(buffer, width, height, pixel -> {
            final Color color = Color.rgb((pixel >> 16) & 0xff, (pixel >> 8) & 0xff, (pixel >> 0) & 0xff);
            return Integer.MAX_VALUE - (int)(color.getBrightness() * Integer.MAX_VALUE);
        });

        this.board.set(board);
        this.paths.clear();
        this.image.set(image);
    }

    public void findPath(Cell origin, Cell destination) {
        final Board board = this.board.get();

        if (board != null) {
            this.paths.clear();
            this.paths.addAll(board.findPaths(origin, destination, 3));
        }
    }

    /////////////////////////////////////////////////////////////////

    private static Image scaleImage(Image image) {
        final ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(image.getWidth() / 8);
        imageView.setFitHeight(image.getHeight() / 8);
        imageView.setSmooth(true);
        return imageView.snapshot(null, null);
    }
}
