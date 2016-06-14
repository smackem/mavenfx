package net.smackem.mavenfx.gui.application;

import java.io.IOException;
import java.util.stream.Collectors;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import net.smackem.mavenfx.model.Board;
import net.smackem.mavenfx.model.Cell;

public final class BoardViewModel {
    public static final int BLACK_WEIGHT = 1000;
    private final ReadOnlyObjectWrapper<Board> board = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<Image> image = new ReadOnlyObjectWrapper<>();
    private final ObservableList<PathViewModel> paths = FXCollections.observableArrayList();
    private final ObservableList<PathViewModel> immutablePaths;
    private final ReadOnlyObjectWrapper<Cell> originCell = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<Cell> destinationCell = new ReadOnlyObjectWrapper<>();
    private final IntegerProperty pathCount = new SimpleIntegerProperty(3);

    public BoardViewModel() {
        this.immutablePaths = FXCollections.unmodifiableObservableList(this.paths);

        this.pathCount.addListener((prop, oldVal, newVal) -> {
            findPaths();
        });
    }

    public ReadOnlyObjectProperty<Board> boardProperty() {
        return this.board.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<Image> imageProperty() {
        return this.image.getReadOnlyProperty();
    }

    public ObservableList<PathViewModel> getPaths() {
        return this.immutablePaths;
    }

    public ReadOnlyObjectProperty<Cell> originCellProperty() {
        return this.originCell.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<Cell> destinationCellProperty() {
        return this.destinationCell.getReadOnlyProperty();
    }

    public IntegerProperty pathCountProperty() {
        return this.pathCount;
    }

    public void createNewBoard(int width, int height) {
        this.board.set(new Board(width, height));
        this.paths.clear();
        this.image.set(null);
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
            return BLACK_WEIGHT - (int)(color.getBrightness() * BLACK_WEIGHT);
        });

        this.board.set(board);
        this.paths.clear();
        this.image.set(image);
    }

    public void setOriginAndDestination(Cell origin, Cell destination) {
        this.originCell.set(origin);
        this.destinationCell.set(destination);

        findPaths();
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

    private void findPaths() {
        final Board board = this.board.get();
        final Cell origin = this.originCell.get();
        final Cell destination = this.destinationCell.get();
        final PathPaintGenerator paintGenerator = new PathPaintGenerator();

        if (board != null && origin != null && destination != null) {
            this.paths.setAll(
                board.findPaths(origin, destination, this.pathCount.get())
                    .stream()
                    .map(path -> new PathViewModel(path, paintGenerator.next()))
                    .collect(Collectors.toList()));
        }
    }

    private static class PathPaintGenerator {
        final Color firstColor = Color.RED;
        double hue;

        Paint next() {
            final double saturation = firstColor.getSaturation();
            final double brightness = firstColor.getBrightness();
            final Color result = Color.hsb(this.hue, saturation, brightness);
            this.hue = (this.hue + 60.0) % 360.0;
            return result;
        }
    }
}
