package ch.guitarpracticebuddy.javafx;

import ch.guitarpracticebuddy.domain.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
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
    @FXML
    private FlowPane ratingFilterPane;
    private ExerciseDefinitionFormController formController;
    private PracticeBuddyBean practiceBuddyBean;
    private List<Tag> selectedTags = new ArrayList<>();
    private PracticeWeek selectedPracticeWeek;
    private List<Rating> selectedRatings = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.practiceBuddyBean = PracticeBuddyBean.getInstance();
        initForm();
        updateTagPanel();
        initPlanningTree();
        initRatingFilterPane();
        setSelectedExerciseDefinition(null);

    }

    private void initRatingFilterPane() {
        for (Rating rating : Rating.values()) {
            ratingFilterPane.getChildren().add(createRatingFilterButton(rating));
        }
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
        refreshWeekOverviewTable();

    }

    private void refreshWeekOverviewTable() {
        weekOverviewTable.setVisible(planningTree.getSelectedPracticeWeek() != null);
    }

    private void updatePracticeWeekOverview() {
        weekOverviewTable.getColumns().clear();

        if (planningTree.getSelectedPracticeWeek() != null) {
            selectedPracticeWeek = planningTree.getSelectedPracticeWeek();
            weekOverviewTable.setItems(FXCollections.observableArrayList(selectedPracticeWeek.getExerciseDefinitions()));
            initWeekOverviewTable();
        }
        refreshWeekOverviewTable();

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
            column.setCellFactory(new Callback<TableColumn, TableCell>() {

                @Override
                public TableCell call(TableColumn tableColumn) {
                    return new StatusTableCell();
                }
            });
            column.setPrefWidth(50);

            weekOverviewTable.getColumns().add(column);
        }

    }

    private void updateTagPanel() {
        tagPanel.getChildren().clear();
        for (final Tag tag : practiceBuddyBean.getTags()) {
            final ToggleButton tagButton = new TagButton(tag);
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

    private void toggleRatingSelected(Rating rating) {
        if (this.selectedRatings.contains(rating)) {
            this.selectedRatings.remove(rating);
        } else {
            this.selectedRatings.add(rating);
        }
        filterExercises();
    }

    private void filterExercises() {

        planningTree.getSelectionModel().clearSelection();
        setSelectedExerciseDefinition(null);
        planningTree.setFilter(selectedTags, selectedRatings);
    }

    private void initForm() {
        FxmlLoader fxmlLoader = new FxmlLoader();
        Parent root = fxmlLoader.load("exerciseDefinitionForm.fxml");
        formController = fxmlLoader.getFxmlLoader().getController();
        formPanel.getChildren().add(root);

    }

    public void refresh() {
    }

    private Node createRatingFilterButton(final Rating rating) {
        final RatingToggleButton ratingToggleButton = new RatingToggleButton(rating);
        ratingToggleButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                toggleRatingSelected(rating);
            }

        });
        return ratingToggleButton;
    }

    private static class RatingToggleButton extends ToggleButton {

        private RatingToggleButton(Rating rating) {
            setText(Texts.getText(rating));
            setId("ratingButton");
        }

    }

    public static class TagButton extends ToggleButton {

        private final Tag tag;

        private TagButton(Tag tag) {
            this.tag = tag;
            setText(tag.getName());
            setId("tagButton");
            getStyleClass().add("tag");

            setOnDragDetected(new EventHandler<MouseEvent>() {
                public void handle(MouseEvent event) {

                    Dragboard db = startDragAndDrop(TransferMode.COPY);

                    ClipboardContent content = new ClipboardContent();
                    content.putString(getText());
                    db.setContent(content);

                    event.consume();
                }
            });
        }

        public Tag getTag() {
            return tag;
        }
    }

    private class StatusTableCell extends TableCell<Object, ExerciseStatus> {

        @Override
        protected void updateItem(ExerciseStatus o, boolean b) {
            super.updateItem(o, b);
            String iconName = null;
            if (o == ExerciseStatus.DONE) {
                iconName = "yes.png";
            } else if (o == ExerciseStatus.SKIPPED) {
                iconName = "skip.png";
            }
            if (iconName != null) {
                setGraphic(new ImageView(new Image(getClass().getResourceAsStream(iconName))));
            }
            setAlignment(Pos.CENTER);
        }
    }
}
