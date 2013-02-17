package ch.guitarpracticebuddy.javafx;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Tab;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: mjunker
 * Date: 2/16/13
 * Time: 1:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class GuitarBuddyController implements Initializable {

    @FXML
    private Tab practicingContent;

    @FXML
    private Tab planningContent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Parent root = new FxmlLoader().load("practicePanel.fxml");
        practicingContent.setContent(root);

        Parent planningRoot = new FxmlLoader().load("planningForm.fxml");
        planningContent.setContent(planningRoot);

    }
}
