package ch.guitarpracticebuddy.javafx;

import javafx.event.Event;
import javafx.event.EventHandler;
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

    private PlanningController planningController;
    private PracticeController practiceFormController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        FxmlLoader practicePaneLoader = new FxmlLoader();
        Parent root = practicePaneLoader.load("practicePanel.fxml");
        practicingContent.setContent(root);
        practiceFormController = practicePaneLoader.getFxmlLoader().getController();

        FxmlLoader planningPaneLoader = new FxmlLoader();
        Parent planningRoot = planningPaneLoader.load("planningForm.fxml");
        planningController = planningPaneLoader.getFxmlLoader().getController();
        planningContent.setContent(planningRoot);

        practicingContent.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                planningController.refresh();
                practiceFormController.refresh();
            }
        });

    }
}
