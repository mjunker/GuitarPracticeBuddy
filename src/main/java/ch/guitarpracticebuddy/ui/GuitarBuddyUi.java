package ch.guitarpracticebuddy.ui;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GuitarBuddyUi {
    private JPanel mainPanel;
    private PlanningForm planningForm;
    private JTabbedPane tabbedPane;
    private PracticeForm practiceForm;

    public GuitarBuddyUi() {
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                practiceForm.save();
                planningForm.save();
                planningForm.refresh();
                practiceForm.refresh();
            }
        });

    }

    public PlanningForm getPlanningForm() {
        return planningForm;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public PracticeForm getPracticeForm() {
        return practiceForm;
    }
}
