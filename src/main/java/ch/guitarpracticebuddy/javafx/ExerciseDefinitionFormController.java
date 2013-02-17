package ch.guitarpracticebuddy.javafx;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class ExerciseDefinitionFormController implements Initializable {

    @FXML
    private TextField titleField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private TextField minutesField;

    @FXML
    private TextField bpmField;

    @FXML
    private TextArea filesField;

    @FXML
    private ListView tagField;

    @FXML
    private ChoiceBox ratingField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
