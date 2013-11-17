package ch.guitarpracticebuddy.javafx;

import ch.guitarpracticebuddy.domain.PracticeBuddyBean;
import ch.guitarpracticebuddy.domain.Tag;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: mjunker
 * Date: 11/17/13
 * Time: 11:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class TagAdminController implements Initializable {

    @FXML
    private ListView<Tag> tagAdminList;
    @FXML
    private Button tagAdminNewButton;
    @FXML
    private Button tagAdminEditButton;
    @FXML
    private Button tagAdminDeleteButton;

    @FXML
    private TextField tagAdminNameField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        initTagList();
        initActions();
    }

    private void initActions() {

        tagAdminList.setCellFactory(new Callback<ListView<Tag>, ListCell<Tag>>() {

            @Override
            public ListCell<Tag> call(ListView<Tag> exerciseDefinitionListView) {
                return new TagListCell();
            }
        });

        tagAdminNewButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                PracticeBuddyBean.getInstance().addTag(tagAdminNameField.getText());
            }
        });

        tagAdminDeleteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                PracticeBuddyBean.getInstance().deleteTag(tagAdminList.getSelectionModel().getSelectedItem());
            }
        });

        tagAdminList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tag>() {
            @Override
            public void changed(ObservableValue<? extends Tag> observableValue, Tag tag, Tag tag2) {
                tagAdminNameField.setText(tag2.getName());
            }
        });

        tagAdminEditButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                tagAdminList.getSelectionModel().getSelectedItem().setName(tagAdminNameField.getText());
            }
        });
    }

    private void initTagList() {
        tagAdminList.setItems(PracticeBuddyBean.getInstance().getTags());
    }

    public void refresh() {

    }

    private class TagListCell extends ListCell<Tag> {

        @Override
        protected void updateItem(final Tag tag, boolean b) {
            textProperty().unbind();
            super.updateItem(tag, b);

            if (tag != null) {
                textProperty().bind(tag.nameProperty());
            }

        }

    }
}
