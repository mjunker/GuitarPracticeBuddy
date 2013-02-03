package ch.guitarpracticebuddy.ui;

import ch.guitarpracticebuddy.domain.ExerciseInstance;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableCellEditor;
import java.awt.*;


public class ExerciseInstanceCellEditor extends AbstractCellEditor implements TableCellEditor {


    private JCheckBox checkBox;
    private Boolean selected;
    private ExerciseInstance exerciseInstance;

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

        if (value instanceof ExerciseInstance) {
            this.exerciseInstance = (ExerciseInstance) value;
            checkBox = new JCheckBox();
            checkBox.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    exerciseInstance.setDone(checkBox.isSelected());
                }
            });
            return checkBox;
        }
        return null;


    }


    @Override
    public Object getCellEditorValue() {
        return exerciseInstance;
    }

}
