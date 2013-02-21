package ch.guitarpracticebuddy.javafx;

import ch.guitarpracticebuddy.domain.PracticeBuddyBean;
import ch.guitarpracticebuddy.util.PersistenceUtil;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.persistence.EntityManager;

public class GuitarPracticeBuddyApplication extends Application {

    public static void main(String[] args) {
        GuitarPracticeBuddyApplication.launch(args);

    }

    @Override
    public void start(Stage stage) throws Exception {

        final EntityManager em = PersistenceUtil.init();
        PracticeBuddyBean.init(em);

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {

                em.getTransaction().commit();
            }
        });

        FxmlLoader fxmlLoader = new FxmlLoader();
        Parent root = fxmlLoader.load("guitarpracticebuddy.fxml");
        final GuitarBuddyController controller = fxmlLoader.getFxmlLoader().getController();


        stage.setTitle("Guitar Practice Buddy");

        Scene scene = new Scene(root, 1070, Screen.getPrimary().getBounds().getHeight());

        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();

    }

}
