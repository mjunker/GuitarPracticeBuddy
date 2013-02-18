package ch.guitarpracticebuddy.javafx;

import ch.guitarpracticebuddy.domain.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import org.joda.time.LocalDate;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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
    private PracticeBuddyBean practiceBuddyBean;
    private List<Tag> selectedTags = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.practiceBuddyBean = PracticeBuddyBean.getInstance();
        initForm();
        updateTagPanel();
        initPlanningTree();

    }

    private LocalDateRange getColumnRange() {
        return new LocalDateRange(getSelectedPracticeWeek().getInterval());
    }

    private PracticeWeek getSelectedPracticeWeek() {
        return this.planningTree.getSelectedPracticeWeek();
    }

    private void initPlanningTree() {
        planningTree.setPracticeBuddyBean(PracticeBuddyBean.getInstance());
        planningTree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem>() {

            @Override
            public void changed(ObservableValue<? extends TreeItem> observableValue, TreeItem treeItem, TreeItem treeItem2) {
                if (treeItem2 != null && treeItem2.getValue() instanceof ExerciseDefinition) {
                    setSelectedExerciseDefinition((ExerciseDefinition) treeItem2.getValue());
                } else {
                    setSelectedExerciseDefinition(null);
                }
                updatePracticeWeekOverview();

            }
        });
    }

    private void setSelectedExerciseDefinition(ExerciseDefinition exerciseDefinition) {
        formController.setExerciseDefinition(exerciseDefinition);
    }

    private void updatePracticeWeekOverview() {
        weekOverviewTable.getColumns().clear();

        if (planningTree.getSelectedPracticeWeek() != null) {
            weekOverviewTable.setItems(FXCollections.observableArrayList(planningTree.getSelectedPracticeWeek().getExerciseDefinitions()));
            initWeekOverviewTable();
        }
    }

    private void initWeekOverviewTable() {

        TableColumn title = new TableColumn("Title");
        title.setMinWidth(150);
        title.setEditable(false);
        weekOverviewTable.getColumns().add(title);
        title.setCellValueFactory(new PropertyValueFactory("title"));

        for (final LocalDate localDate : getColumnRange()) {
            TableColumn column = new TableColumn(ExerciseDefinitionTreeCell.DAY_ONLY_FORMATTER.print(localDate));
            column.setEditable(true);
            column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ExerciseDefinition, String>, ObservableValue<ExerciseStatus>>() {
                public ObservableValue<ExerciseStatus> call(TableColumn.CellDataFeatures<ExerciseDefinition, String> p) {
                    return p.getValue().getExerciseForDay(localDate).statusProperty();
                }
            });
            weekOverviewTable.getColumns().add(column);
        }

    }

    private void updateTagPanel() {
        tagPanel.getChildren().clear();
        for (final Tag tag : practiceBuddyBean.getTags()) {
            final ToggleButton tagButton = new ToggleButton();
            tagButton.setText(tag.getName());
            tagButton.setId("tagButton");
            tagButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    toggleTagSelected(tag);
                }
            });

            tagPanel.getChildren().add(tagButton);

        }
    }

    private void toggleTagSelected(Tag tag) {
        if (this.selectedTags.contains(tag)) {
            this.selectedTags.remove(tag);
        } else {
            this.selectedTags.add(tag);
        }
        filterExercises();
    }

    private void filterExercises() {
        planningTree.setTagFilter(selectedTags);
    }

    private void initForm() {
        FxmlLoader fxmlLoader = new FxmlLoader();
        Parent root = fxmlLoader.load("exerciseDefinitionForm.fxml");
        formController = fxmlLoader.getFxmlLoader().getController();
        formPanel.getChildren().add(root);

    }

    public void refresh() {
        //To change body of created methods use File | Settings | File Templates.
    }
}
