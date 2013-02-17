package ch.guitarpracticebuddy.javafx;

import ch.guitarpracticebuddy.domain.PracticeBuddyBean;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TableView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: mjunker
 * Date: 2/17/13
 * Time: 12:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class PlanningController implements Initializable {

    @FXML
    private Pane formPanel;

    @FXML
    private TableView weekOverviewTable;

    @FXML
    private FlowPane tagPanel;

    @FXML
    private PlanningTree planningTree;

    private ExerciseDefinitionFormController formController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initForm();
        initPlanningTree();
        planningTree.setPracticeBuddyBean(PracticeBuddyBean.getInstance());

    }

    private void initPlanningTree() {

    }

    private void initForm() {
        FxmlLoader fxmlLoader = new FxmlLoader();
        Parent root = fxmlLoader.load("exerciseDefinitionForm.fxml");
        formController = fxmlLoader.getFxmlLoader().getController();
        formPanel.getChildren().add(root);

    }

}
