package ch.guitarpracticebuddy.ui;

import ch.guitarpracticebuddy.domain.*;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import org.joda.time.LocalDate;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.collect;
import static ch.lambdaj.Lambda.on;

public class PlanningForm {

    private JTextArea descriptionField;
    private JTextField titleField;
    private JTextField durationField;
    private JTextField bpmField;
    private JList tagList;
    private JTable exerciseOverviewTable;
    private JPanel rootPanel;
    private PracticePlanManagerTree practicePlanOverview;
    private JTextArea filesTextBox;
    private JButton selectFilesButton;
    private ExerciseDefinition selectedExerciseDef;

    public PlanningForm() {

        addListeners();
        configure();
    }

    private void configure() {
        exerciseOverviewTable.getTableHeader().setEnabled(true);
        exerciseOverviewTable.setShowVerticalLines(false);
        exerciseOverviewTable.setShowHorizontalLines(true);
    }

    private void addListeners() {
        addFocusListener(titleField, descriptionField, durationField, bpmField);
        selectFilesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser fileChooser = new JFileChooser();

                fileChooser.setMultiSelectionEnabled(true);
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.showOpenDialog(rootPanel);
                appendFiles(fileChooser.getSelectedFiles(), PlanningForm.this.selectedExerciseDef);
            }
        });
    }

    private void appendFiles(File[] selectedFiles, ExerciseDefinition exerciseDefinition) {
        List<String> relativePaths = FileUtil.copyFilesToApplicationHome(selectedFiles, exerciseDefinition);
        filesTextBox.setText(createAttachmentText(relativePaths));

    }

    public void dontShowExerciseOverview() {
        exerciseOverviewTable.setModel(new DefaultTableModel());
    }

    private void addFocusListener(JComponent... titleFields) {
        for (JComponent field : titleFields) {
            field.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    updateModelFromForm();
                    practicePlanOverview.updateLabels();

                }
            });
        }

    }

    private void updateModelFromForm() {
        getData(selectedExerciseDef);
    }

    public void enableExcerciseDefPanel(boolean enabled) {
        descriptionField.setEnabled(enabled);
        durationField.setEnabled(enabled);
        bpmField.setEnabled(enabled);
        titleField.setEnabled(enabled);
        filesTextBox.setEnabled(enabled);
    }

    public void setExerciseOverviewData(List<ExerciseDefinition> exerciseDefinitionList) {
        final DefaultTableModel dataModel = new DefaultTableModel(getColumnNames(), exerciseDefinitionList.size()) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column > 0;
            }
        };

        for (int row = 0; row < exerciseDefinitionList.size(); row++) {
            ExerciseDefinition exerciseDefinition = exerciseDefinitionList.get(row);
            dataModel.setValueAt(exerciseDefinition.getTitle(), row, 0);
            showPracticeInstancesOfWeek(dataModel, row, exerciseDefinition);

        }
        exerciseOverviewTable.setModel(dataModel);
        exerciseOverviewTable.createDefaultColumnsFromModel();
        exerciseOverviewTable.getColumnModel().getColumn(0).setPreferredWidth(200);

        setCellRendererAndEditor(dataModel);
    }

    private void setCellRendererAndEditor(final DefaultTableModel dataModel) {
        for (int i = 1; i < 8; i++) {

            exerciseOverviewTable.getColumnModel().getColumn(i).setCellEditor(new ExerciseInstanceCellEditor());
            exerciseOverviewTable.getColumnModel().getColumn(i).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

                    Object valueFromModel = dataModel.getValueAt(row, column);
                    if (valueFromModel instanceof ExerciseInstance) {
                        JCheckBox checkBox = new JCheckBox();
                        checkBox.setBackground(Color.WHITE);
                        checkBox.setSelected(((ExerciseInstance) valueFromModel).isDone());
                        checkBox.setVerticalAlignment(SwingConstants.CENTER);
                        return checkBox;
                    } else {
                        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    }
                }

            });

        }
    }

    private String[] getColumnNames() {

        List<String> columns = new ArrayList<String>();
        columns.add("Title");
        for (LocalDate localDate : getColumnRange()) {
            columns.add(PracticePlanManagerTree.DAY_ONLY_FORMATTER.print(localDate));
        }
        return columns.toArray(new String[columns.size()]);
    }

    private LocalDateRange getColumnRange() {
        return new LocalDateRange(getSelectedPracticeWeek().getInterval());
    }

    private PracticeWeek getSelectedPracticeWeek() {
        return this.practicePlanOverview.getSelectedPracticeWeek();
    }

    private void showPracticeInstancesOfWeek(DefaultTableModel dataModel, int row, ExerciseDefinition exerciseDefinition) {
        int column = 1;
        for (ExerciseInstance exerciseInstance : getSelectedPracticeWeek().getExerciseInstances(exerciseDefinition)) {
            dataModel.setValueAt(exerciseInstance, row, column);
            column++;
        }
    }

    public void setSelectedExerciseDef(ExerciseDefinition data) {
        this.selectedExerciseDef = data;
        if (data != null) {
            descriptionField.setText(data.getDescription());
            titleField.setText(data.getTitle());
            durationField.setText(Integer.valueOf(data.getMinutes()).toString());
            bpmField.setText(Integer.valueOf(data.getBpm()).toString());
            // TODO MJU select tags
            // tagList.setSelectedIndices(data.get);
            filesTextBox.setText(createAttachmentText(collect(data.getAttachments(), on(ExerciseAttachment.class).getFilePath())));
        } else {
            descriptionField.setText(null);
            titleField.setText(null);
            durationField.setText(null);
            bpmField.setText(null);
            tagList.clearSelection();
            tagList.setListData(new Object[]{});
            filesTextBox.setText(null);
        }

    }

    private String createAttachmentText(List<String> filePaths) {
        return Joiner.on("\n")
                .skipNulls()
                .join(filePaths);
    }

    public void getData(ExerciseDefinition data) {

        if (data != null) {
            data.setDescription(descriptionField.getText());
            data.setTitle(titleField.getText());
            if (!Strings.isNullOrEmpty(durationField.getText())) {
                data.setMinutes(Integer.parseInt(durationField.getText()));
            }

            if (!Strings.isNullOrEmpty(bpmField.getText())) {
                data.setBpm(Integer.parseInt(bpmField.getText()));
            }
            data.setTags(convertTags(tagList.getSelectedValues()));
            data.setAttachments(createAttachments());
        }

    }

    private List<ExerciseAttachment> createAttachments() {
        List<ExerciseAttachment> attachments = new ArrayList<ExerciseAttachment>();

        for (String filePath : Splitter.on("\n").omitEmptyStrings().split(filesTextBox.getText())) {
            attachments.add(new ExerciseAttachment(filePath));
        }
        return attachments;
    }

    private List<String> convertTags(Object[] selectedValues) {

        List<String> result = new ArrayList<String>();
        for (Object selectedValue : selectedValues) {
            result.add((String) selectedValue);
        }
        return result;
    }

    public void setData(PracticeBuddyBean data) {
        practicePlanOverview.setPracticeBuddyBean(data);
    }

    public void refresh() {
        this.practicePlanOverview.refresh();
    }

    private void createUIComponents() {
        this.practicePlanOverview = new PracticePlanManagerTree(this);
    }

    public void save() {
        updateModelFromForm();
    }
}
