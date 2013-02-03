package ch.guitarpracticebuddy;

import ch.guitarpracticebuddy.domain.ExerciseDefinition;
import ch.guitarpracticebuddy.domain.PracticeBuddyBean;
import ch.guitarpracticebuddy.domain.PracticeWeek;
import ch.guitarpracticebuddy.ui.GuitarBuddyUi;
import ch.guitarpracticebuddy.util.KeyEventDispatcherUtil;
import org.joda.time.DateTime;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class Main {

    public static void main(String[] args) {

        System.setProperty("objectdb.home", "/Users/mjunker/test");

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("$objectdb/db/guitarpracticebuddy.odb");
        final EntityManager em = emf.createEntityManager();


        createDummyData(em);
        em.getTransaction().begin();
        GuitarBuddyUi guitarBuddyUi = createMainModel(em);
        openUi(em, guitarBuddyUi);

    }


    private static GuitarBuddyUi createMainModel(EntityManager em) {
        GuitarBuddyUi guitarBuddyUi = new GuitarBuddyUi();
        PracticeBuddyBean practiceBuddy = new PracticeBuddyBean(em);
        guitarBuddyUi.getPlanningForm().setData(practiceBuddy);
        guitarBuddyUi.getPracticeForm().setData(practiceBuddy);
        return guitarBuddyUi;
    }

    private static void openUi(final EntityManager em, GuitarBuddyUi guitarBuddyUi) {
        JFrame frame = new JFrame("GuitarBuddy");
        frame.setContentPane(guitarBuddyUi.getMainPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(1024, 768));

        frame.pack();
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                em.getTransaction().commit();
            }
        });

        KeyEventDispatcherUtil.addKeyListener(new KeyEventDispatcherUtil.KeyEventListener() {
            @Override
            public void onKeyPressed(KeyEvent e) {
                if (e.isControlDown() && e.getKeyChar() == 's') {
                    em.getTransaction().commit();
                    em.getTransaction().begin();
                }
            }
        });


    }


    private static void createDummyData(EntityManager em) {

        em.getTransaction().begin();

        PracticeWeek practiceWeek = new PracticeWeek(DateTime.now().toLocalDate(), DateTime.now().plusDays(6).toLocalDate());

        em.persist(practiceWeek);

        ExerciseDefinition exerciseDefinition = new ExerciseDefinition();
        exerciseDefinition.setTitle("Test 1");
        exerciseDefinition.setMinutes(1);
        exerciseDefinition.setBpm(100);
        exerciseDefinition.tag("Technique");
        exerciseDefinition.createInstancesForEntireWeek();


        ExerciseDefinition exerciseDefinition2 = new ExerciseDefinition();
        exerciseDefinition2.setTitle("Test 2");
        exerciseDefinition2.setMinutes(2);
        exerciseDefinition2.setBpm(100);

        exerciseDefinition2.createInstancesForEntireWeek();

        practiceWeek.activate(exerciseDefinition);
        practiceWeek.activate(exerciseDefinition2);

        em.getTransaction().commit();

    }
}

