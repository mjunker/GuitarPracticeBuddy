package ch.guitarpracticebuddy.javafx;

import ch.guitarpracticebuddy.domain.ExerciseDefinition;
import ch.guitarpracticebuddy.domain.PracticeWeek;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.EventHandler;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.input.*;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

public class ExerciseDefinitionTreeCell extends TreeCell {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendDayOfWeekShortText()
            .appendLiteral(", ")
            .appendDayOfMonth(1)
            .appendLiteral(".")
            .appendMonthOfYear(1)
            .appendLiteral(".")
            .appendTwoDigitYear(2000)
            .toFormatter();
    public static final DateTimeFormatter DAY_ONLY_FORMATTER = new DateTimeFormatterBuilder()
            .appendDayOfWeekShortText()
            .toFormatter();
    private ExerciseDefinition exerciseDefinition;
    private PracticeWeek practiceWeek;
    private final TreeItem rootNode;
    private final ContextMenuFactory contextMenuFactory;

    public ExerciseDefinitionTreeCell(TreeItem root, ContextMenuFactory contextMenuFactory) {
        this.rootNode = root;
        this.contextMenuFactory = contextMenuFactory;
    }

    @Override
    protected void updateItem(Object object, boolean b) {
        super.updateItem(object, b);
        this.textProperty().unbind();

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

        }
        setContextMenu(contextMenuFactory.createPracticePlanRootMenu());

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
                + " " + practiceWeek.getPracticeTimeAsString() + "";
    }

    public ExerciseDefinition getExerciseDefinition() {
        return exerciseDefinition;
    }
}
