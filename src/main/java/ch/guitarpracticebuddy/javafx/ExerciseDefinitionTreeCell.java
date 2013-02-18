package ch.guitarpracticebuddy.javafx;

import ch.guitarpracticebuddy.domain.ExerciseDefinition;
import ch.guitarpracticebuddy.domain.PracticeBuddyBean;
import ch.guitarpracticebuddy.domain.PracticeWeek;
import com.google.common.base.Joiner;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.input.*;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import static com.google.common.base.Strings.repeat;

public class ExerciseDefinitionTreeCell extends TreeCell {

    public static final TreeItem PRACTICE_PLAN_ROOT_NODE = new TreeItem("Practice Plans");
    public static final TreeItem ALL_EXERCISES_NODE = new TreeItem("All Exercises");
    public static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendDayOfWeekShortText()
            .appendLiteral(", ")
            .appendDayOfMonth(1)
            .appendLiteral(".")
            .appendMonthOfYear(1)
            .appendLiteral(".")
            .appendYear(2, 2)
            .toFormatter();
    public static final DateTimeFormatter DAY_ONLY_FORMATTER = new DateTimeFormatterBuilder()
            .appendDayOfWeekShortText()
            .toFormatter();
    private ExerciseDefinition exerciseDefinition;
    private PracticeWeek practiceWeek;

    public ExerciseDefinitionTreeCell() {
    }

    @Override
    protected void updateItem(Object object, boolean b) {
        super.updateItem(object, b);
        if (object instanceof ExerciseDefinition) {
            this.exerciseDefinition = (ExerciseDefinition) object;
            this.textProperty().bind(this.exerciseDefinition.titleProperty());
            initDragAndDropExerciseDefinition();

        } else if (object instanceof PracticeWeek) {
            this.practiceWeek = (PracticeWeek) object;
            this.textProperty().bind(new SimpleStringProperty(createPracticeWeekString(this.practiceWeek)));
            initDropListener();

        } else {
            this.textProperty().bind(new SimpleStringProperty((String) object));
            if (PRACTICE_PLAN_ROOT_NODE.equals(getTreeItem())) {
                setContextMenu(createPracticePlanRootMenu());
            } else if (ALL_EXERCISES_NODE.equals(getTreeItem())) {
                setContextMenu(createExerciseDefinitionRootMenu());
            }

        }

    }

    private ContextMenu createPracticePlanRootMenu() {
        MenuItem addItem = new MenuItem("Add practice plan");
        addItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                PracticeWeek newPracticePlan = PracticeBuddyBean.getInstance()
                        .createNewPracticePlan(DateTime.now().toLocalDate(), DateTime.now().plusDays(6).toLocalDate());
                PRACTICE_PLAN_ROOT_NODE.getChildren().add(new TreeItem<>(newPracticePlan));

            }
        });
        return new ContextMenu(addItem);
    }

    private ContextMenu createExerciseDefinitionRootMenu() {
        MenuItem addItem = new MenuItem("Add exercise");
        addItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ExerciseDefinition newExerciseDefinition = PracticeBuddyBean.getInstance().createNewExerciseDefinition();
                ALL_EXERCISES_NODE.getChildren().add(0, new TreeItem<>(newExerciseDefinition));

            }
        });
        return new ContextMenu(addItem);
    }

    private void initDragAndDropExerciseDefinition() {
        setOnDragDetected(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {

                Dragboard db = startDragAndDrop(TransferMode.ANY);

                ClipboardContent content = new ClipboardContent();
                content.putString(getText());
                db.setContent(content);

                event.consume();
            }
        });
    }

    private void initDropListener() {

        setOnDragOver(new EventHandler<DragEvent>() {
            public void handle(DragEvent dragEvent) {

                if (dragEvent.getGestureSource() instanceof ExerciseDefinitionTreeCell) {
                    ExerciseDefinitionTreeCell source = (ExerciseDefinitionTreeCell) dragEvent.getGestureSource();
                    if (!alreadyContainsNode(source)) {
                        dragEvent.acceptTransferModes(TransferMode.COPY);
                        dragEvent.consume();

                    }
                }

            }
        });

        setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                ExerciseDefinitionTreeCell source = (ExerciseDefinitionTreeCell) dragEvent.getGestureSource();
                Object value = source.getTreeItem().getValue();
                TreeItem copy = new TreeItem(value);
                getTreeItem().getChildren().add(copy);
                practiceWeek.activate(source.getExerciseDefinition());
                dragEvent.setDropCompleted(true);

            }
        });
    }

    private boolean alreadyContainsNode(ExerciseDefinitionTreeCell source) {
        for (Object o : getTreeItem().getChildren()) {
            TreeItem treeItem = (TreeItem) o;
            if (treeItem.getValue().equals(source.getTreeItem().getValue())) {
                return true;
            }
        }
        return false;
    }

    private String createPracticeWeekString(PracticeWeek practiceWeek) {
        return DATE_TIME_FORMATTER.print(practiceWeek.getDateFrom())
                + "-" + DATE_TIME_FORMATTER.print(practiceWeek.getDateTo())
                + " " + createTimeString(practiceWeek) + "";
    }

    public String createExerciseDefinitionString(ExerciseDefinition exerciseDefinition) {
        StringBuilder sb = new StringBuilder();
        sb.append(exerciseDefinition.getTitle());
        if (exerciseDefinition.getRating() != null && exerciseDefinition.getRating().getLevel() > 0) {
            sb.append(" ");
            sb.append(repeat("*", exerciseDefinition.getRating().getLevel()));
        }
        return sb.toString();
    }

    private String createTimeString(PracticeWeek practiceWeek) {
        int timeInMinutes = practiceWeek.calculateTotalMinutes();
        int hours = timeInMinutes / 60;
        int minutes = timeInMinutes % 60;

        String hourString = null;
        String minuteString = null;

        if (minutes > 0) {
            minuteString = minutes + "m";
        }

        if (hours > 0) {
            hourString = hours + "h";
        }

        return Joiner.on(" ").skipNulls().join(hourString, minuteString);
    }

    public ExerciseDefinition getExerciseDefinition() {
        return exerciseDefinition;
    }

    public PracticeWeek getPracticeWeek() {
        return practiceWeek;
    }
}
