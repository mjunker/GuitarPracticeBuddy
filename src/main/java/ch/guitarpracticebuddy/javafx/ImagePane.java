package ch.guitarpracticebuddy.javafx;

import ch.guitarpracticebuddy.domain.ExerciseAttachment;
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
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

public class ImagePane extends ScrollPane {
    public static final int IMAGE_WIDTH = 1100;
    private final ExerciseDefinition exerciseDefinition;
    private final List<ExerciseAttachment> attachments;
    private List<ImageView> images = new ArrayList<>();
    private Pagination pagination;

    public ImagePane(ExerciseDefinition def) {

        this.exerciseDefinition = def;
        this.attachments = filterImages(this.exerciseDefinition);
        setFitToHeight(true);
        setFitToWidth(true);
        initContent();
        setVisible(!attachments.isEmpty());

    }

    private void initContent() {
        if (!attachments.isEmpty()) {
            initPagination();
        }
    }

    private void initPagination() {
        pagination = PaginationBuilder.create().pageCount(attachments.size()).pageFactory(new Callback<Integer, Node>() {
            @Override
            public Node call(Integer pageIndex) {

                VBox box = new VBox();
                Image image = FileUtil.loadImage(exerciseDefinition, attachments.get(pageIndex));
                ImageView iv = new ImageView(image);
                iv.setPreserveRatio(true);
                iv.setFitHeight(IMAGE_WIDTH);
                images.add(iv);
                box.setAlignment(Pos.CENTER);
                box.getChildren().add(iv);
                return box;
            }
        }).build();
        pagination.getStyleClass().add(Pagination.STYLE_CLASS_BULLET);
        VBox.setVgrow(this, Priority.ALWAYS);
        setContent(pagination);

        initSwipe();
        initZoom();
    }

    private void initSwipe() {
        setOnSwipeLeft(new EventHandler<SwipeEvent>() {
            @Override
            public void handle(SwipeEvent event) {
                pagination.setCurrentPageIndex(pagination.getCurrentPageIndex() - 1);
                event.consume();
            }
        });
        setOnSwipeRight(new EventHandler<SwipeEvent>() {
            @Override
            public void handle(SwipeEvent event) {
                pagination.setCurrentPageIndex(pagination.getCurrentPageIndex() + 1);
                event.consume();
            }
        });

    }

    private void initZoom() {
        setOnZoom(new EventHandler<ZoomEvent>() {
            @Override
            public void handle(ZoomEvent event) {
                ImageView imageView = images.get(pagination.getCurrentPageIndex());
                // TODO zoom

                event.consume();
            }
        });
    }

    private List<ExerciseAttachment> filterImages(ExerciseDefinition exerciseDefinition) {
        List<ExerciseAttachment> imageAttachments = new ArrayList<>();
        for (ExerciseAttachment exerciseAttachment : exerciseDefinition.getAttachments()) {
            Image image = FileUtil.loadImage(exerciseDefinition, exerciseAttachment);
            if (!image.isError()) {
                imageAttachments.add(exerciseAttachment);
            }

        }
        return imageAttachments;
    }
}
