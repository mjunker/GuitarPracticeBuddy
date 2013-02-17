package ch.guitarpracticebuddy.javafx; /**
 * Copyright (c) 2008, 2012 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 */

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;

import java.util.Arrays;

/**
 * An implementation of the TreeView control displaying an expandable tree root
 * node.
 *
 * @see javafx.scene.control.TreeView
 */
public class TreeViewSample extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    private void init(Stage primaryStage) {
        Group root = new Group();
        primaryStage.setScene(new Scene(root));
        final TreeItem<String> treeRoot = new TreeItem<>("Root node");
        treeRoot.getChildren().addAll(Arrays.asList(
                new TreeItem<>("Child Node 1"),
                new TreeItem<>("Child Node 2"),
                new TreeItem<>("Child Node 3")));

        treeRoot.getChildren().get(2).getChildren().addAll(Arrays.asList(
                new TreeItem<>("Child Node 4"),
                new TreeItem<>("Child Node 5"),
                new TreeItem<>("Child Node 6"),
                new TreeItem<>("Child Node 7"),
                new TreeItem<>("Child Node 8"),
                new TreeItem<>("Child Node 9"),
                new TreeItem<>("Child Node 10"),
                new TreeItem<>("Child Node 11"),
                new TreeItem<>("Child Node 12")));

        final TreeView treeView = new TreeView();
        treeView.setShowRoot(false);
        treeView.setRoot(treeRoot);
        treeRoot.setExpanded(true);

        root.getChildren().add(treeView);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        init(primaryStage);
        primaryStage.show();
    }
}
