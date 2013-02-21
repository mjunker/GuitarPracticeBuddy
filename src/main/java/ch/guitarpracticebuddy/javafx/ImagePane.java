package ch.guitarpracticebuddy.javafx;

import ch.guitarpracticebuddy.domain.ExerciseDefinition;
import ch.guitarpracticebuddy.util.FileUtil;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Pagination;
import javafx.scene.control.PaginationBuilder;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.SwipeEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;


public class ImagePane extends ScrollPane {
    public static final int IMAGE_WIDTH = 1100;
    private final ExerciseDefinition exerciseDefinition;


    public ImagePane(ExerciseDefinition def) {

        this.exerciseDefinition = def;
        setFitToHeight(true);
        setFitToWidth(true);
        final Pagination pagination = PaginationBuilder.create().pageCount(exerciseDefinition.getAttachments().size()).pageFactory(new Callback<Integer, Node>() {
            @Override
            public Node call(Integer pageIndex) {

                VBox box = new VBox();
                Image image = FileUtil.loadImage(exerciseDefinition, exerciseDefinition.getAttachments().get(pageIndex));
                ImageView iv = new ImageView(image);
                iv.setPreserveRatio(true);
                iv.setFitHeight(IMAGE_WIDTH);
                box.setAlignment(Pos.CENTER);
                box.getChildren().add(iv);
                return box;
            }
        }).build();
        pagination.getStyleClass().add(Pagination.STYLE_CLASS_BULLET);
        VBox.setVgrow(this, Priority.ALWAYS);
        setContent(pagination);

        setOnSwipeRight(new EventHandler<SwipeEvent>() {
            @Override
            public void handle(SwipeEvent event) {
                pagination.setCurrentPageIndex(pagination.getCurrentPageIndex() + 1);
                event.consume();
            }
        });

        setOnSwipeLeft(new EventHandler<SwipeEvent>() {
            @Override
            public void handle(SwipeEvent event) {
                pagination.setCurrentPageIndex(pagination.getCurrentPageIndex() - 1);
                event.consume();
            }
        });
    }
}
