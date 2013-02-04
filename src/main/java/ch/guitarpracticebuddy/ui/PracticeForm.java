package ch.guitarpracticebuddy.ui;

import ch.guitarpracticebuddy.domain.ExerciseDefinition;
import ch.guitarpracticebuddy.domain.PracticeBuddyBean;
import ch.guitarpracticebuddy.util.KeyEventDispatcherUtil;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class PracticeForm {
    private JList exerciseList;
    private JPanel practicePanel;
    private JProgressBar progressBar;
    private JButton playButton;
    private JPanel exerciseDetails;
    private JButton skipButton;
    private ExerciseDefinition selectedExercise;
    private List<ExerciseDefinition> excercises = new ArrayList<ExerciseDefinition>();
    private ExerciseTimer timer;
    private PracticeBuddyBean practiceBuddyBean;

    public PracticeForm() {
        addListeners();
    }

    public void setData(PracticeBuddyBean practiceBuddyBean) {
        this.practiceBuddyBean = practiceBuddyBean;
        initExercises();
    }

    private void initExercises() {
        DefaultListModel model = new DefaultListModel() {

        };
        for (ExerciseDefinition exerciseDefinition : practiceBuddyBean.getExcercisesForToday()) {
            model.addElement(exerciseDefinition);

        }
        this.exerciseList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel component = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                ExerciseDefinition exerciseDefinition = (ExerciseDefinition) value;
                if (exerciseDefinition.getTodaysExercises().isDone()) {
                    component.setBackground(Color.GREEN);
                    component.setForeground(Color.BLACK);

                }
                component.setText(exerciseDefinition.getTitle());
                return component;
            }
        });
        this.exerciseList.setModel(model);
    }

    private void addListeners() {
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleTimer();
            }
        });
        exerciseList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {

                resetTimer();
            }
        });

        KeyEventDispatcherUtil.addKeyListener(new KeyEventDispatcherUtil.KeyEventListener() {

            @Override
            public void onKeyReleased(KeyEvent e) {
                if (e.getKeyChar() == ' ') {
                    toggleTimer();
                }
            }

            @Override
            public void onKeyPressed(KeyEvent e) {

            }
        });

    }

    private void toggleTimer() {

        if (selectedExercise == null) {
            return;
        }
        if (timer != null && timer.isRunning()) {
            stopTimer();
        } else {
            startTimer();
        }

    }

    private void startTimer() {
        initTimerIfNecessary();
        timer.start();
        playButton.setText("Pause");
    }

    private void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
        playButton.setText("Play");
    }

    private void initTimerIfNecessary() {
        if (timer == null || !timer.getExerciseDefinition().equals(selectedExercise)) {
            timer = new ExerciseTimer(this, selectedExercise);
            initProgressBar(selectedExercise);
        }
    }

    public void resetTimer() {
        stopTimer();
        initCurrentExcercise();
        resetProgressBar();

    }

    private void resetProgressBar() {
        progressBar.setValue(0);
        progressBar.setMaximum(1);
    }

    private void initProgressBar(ExerciseDefinition exerciseDefinition) {
        progressBar.setValue(0);
        progressBar.setMaximum(exerciseDefinition.getMiliSeconds());
    }

    private void initCurrentExcercise() {
        this.selectedExercise = (ExerciseDefinition) this.exerciseList.getSelectedValue();
        if (this.selectedExercise == null && !excercises.isEmpty()) {
            // automatically select first element
            this.selectedExercise = excercises.get(0);
        }
    }

    private void createUIComponents() {
        this.exerciseList = new JList();
        this.exerciseList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                ExerciseDefinition exerciseDefinition = (ExerciseDefinition) value;
                if (exerciseDefinition.getTodaysExercises().isDone()) {
                    component.setBackground(Color.GREEN);
                }
                return component;
            }
        });
    }

    public void selectNextExcercise() {
        if (hasMoreExercises()) {
            exerciseList.setSelectedIndex(excercises.indexOf(selectedExercise) + 1);
        } else {
            exerciseList.setSelectedIndex(0);

        }
    }

    private boolean hasMoreExercises() {
        return excercises.indexOf(selectedExercise) < excercises.size() - 1;
    }

    public ExerciseDefinition getSelectedExercise() {
        return selectedExercise;
    }

    public void updateProgressBar(int time) {
        this.progressBar.setValue(time);
    }

    public void refresh() {
        initExercises();
        this.timer = null;
    }
}
