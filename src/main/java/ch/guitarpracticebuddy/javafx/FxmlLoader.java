package ch.guitarpracticebuddy.javafx;

import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: mjunker
 * Date: 2/16/13
 * Time: 1:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class FxmlLoader {

    private FXMLLoader fxmlLoader;

    public Parent load(String name) {

        URL location = FxmlLoader.class.getResource(name);
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(location);
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());

        try {
            return (Parent) fxmlLoader.load(location.openStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FXMLLoader getFxmlLoader() {
        return fxmlLoader;
    }
}
