package net.smackem.mavenfx.gui.util;

import com.google.common.base.Strings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * Non-instantiable class that provides utility methods for controls.
 *
 * @author pbo
 */
public final class Views {

    /**
     * Loads the FXML description for the specified control and associates
     * the control with the loaded FXML root.
     *
     * @param view
     *      The control for which to load FXML.
     *
     * @param fxmlResourcePath
     *      The resource path to the FXML resource, to be loaded by the
     *      {@link ClassLoader} that loaded the class of {@code control}.
     */
    public static void loadFxml(Parent view, String fxmlResourcePath) {
        Objects.requireNonNull(view);
        if (Strings.isNullOrEmpty(fxmlResourcePath))
            throw new IllegalArgumentException();

        final URL url = view.getClass().getClassLoader().getResource(fxmlResourcePath);
        final FXMLLoader loader = new FXMLLoader();
        loader.setLocation(url);
        loader.setRoot(view);
        loader.setController(view);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /////////////////////////////////////////////////////////////////

    private Views() {
        throw new RuntimeException("Illegal access");
    }
}
