package ch.guitarpracticebuddy.ui;

import ch.guitarpracticebuddy.domain.*;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlanningForm {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendDayOfWeekShortText()
            .appendLiteral(", ")
            .appendDayOfMonth(2)
            .appendLiteral(".")
            .appendMonthOfYear(2)
            .appendLiteral(".")
            .appendYear(2, 2)
            .toFormatter();

    private JTextArea descriptionField;
    private JTextField titleField;
    private JTextField durationField;
    private JTextField bpmField;
    private JTree practicePlanTree;
    private JPanel planningPanel;
    private JList tagList;
    private JTable exerciseOverviewTable;

    private ExerciseDefinition selectedExerciseDef;
    private PracticeWeek selectedPracticeWeek;
    private PracticeBuddyBean practiceBuddyBean;
    private DefaultMutableTreeNode practicePlansNode;
    private DefaultMutableTreeNode allExercisesNode;

    public PlanningForm() {

        addListeners();
        configure();
        setupTreeContextMenu();


    }

    private void setupTreeContextMenu() {
        final JPopupMenu popup = new JPopupMenu();
        JMenuItem mi = new JMenuItem("Add Exercise");
        mi.setActionCommand("insert");
        popup.add(mi);
        mi.addActionListener(new PracticePlanTreeActionListener());
        mi = new JMenuItem("Delete");
        mi.setActionCommand("remove");
        popup.add(mi);
        mi.addActionListener(new PracticePlanTreeActionListener());
        popup.setOpaque(true);
        popup.setLightWeightPopupEnabled(true);

        practicePlanTree.addMouseListener(
                new MouseAdapter() {
                    public void mouseReleased(MouseEvent e) {
                        practicePlanTree.setSelectionPath(practicePlanTree.getClosestPathForLocation(e.getX(), e.getY()));
                        if (e.getButton() == 3) {
                            popup.show((JComponent) e.getSource(), e.getX(), e.getY());
                        }
                    }
                }
        );
    }


    private void configure() {
        this.practicePlanTree.setRootVisible(false);

        practicePlanTree.setDragEnabled(true);
        practicePlanTree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);
        practicePlanTree.setDropMode(DropMode.USE_SELECTION);


    }


    private void addListeners() {
        addSelectionListener();
        addKeyListener();
        initDropTarget();
        addFocusListener(titleField, descriptionField, durationField, bpmField);
    }

    private void addSelectionListener() {
        practicePlanTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                PathExtractor pathExtractor = new PathExtractor(e.getPath()).invoke();
                updateModelFromView();

                selectedPracticeWeek = null;
                selectedExerciseDef = null;

                if (pathExtractor.isUserModelIsExcerciseDef()) {
                    setSelectedExerciseDef(pathExtractor.getExerciseDefinition());

                    if (pathExtractor.isExerciseDefInPracticeWeek()) {
                        setSelectedPracticeWeek(pathExtractor.getPracticeWeek());
                    }

                } else if (pathExtractor.isUserModelIsPracticeWeek()) {
                    setSelectedPracticeWeek(pathExtractor.getPracticeWeek());
                    setSelectedExerciseDef(null);
                }
                updateExerciseOverview();
                enableExcerciseDefPanel(pathExtractor.isUserModelIsExcerciseDef());
            }
        });
    }

    private void addKeyListener() {
        practicePlanTree.addKeyListener(new KeyListener() {


            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == '\b') {
                    deleteFromModel();
                }

            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
    }


    private void initDropTarget() {
        practicePlanTree.setDropTarget(new DropTarget(practicePlanTree, TransferHandler.COPY,
                new DropTargetAdapter() {
                    @Override
                    public void drop(DropTargetDropEvent event) {

                        TreePath selectionPath = practicePlanTree.getSelectionPath();
                        Point dropLocation = event.getLocation();
                        TreePath targetPath = practicePlanTree.getClosestPathForLocation(
                                dropLocation.x, dropLocation.y);

                        if (isDropAllowed(selectionPath, targetPath)) {
                            PathExtractor targetPathExtractor = new PathExtractor(targetPath).invoke();
                            PathExtractor sourcePathExtractor = new PathExtractor(selectionPath).invoke();
                            DefaultMutableTreeNode selectedNode = getSelectedNode();
                            boolean activeSuccesful = targetPathExtractor.getPracticeWeek().activate(sourcePathExtractor.getExerciseDefinition());

                            if (activeSuccesful) {
                                targetPathExtractor.getPracticeWeekNode().add((MutableTreeNode) selectedNode.clone());
                                event.dropComplete(true);
                                practicePlanTree.updateUI();
                            }

                        } else {
                            event.rejectDrop();
                            event.dropComplete(false);
                        }
                    }

                    private boolean isDropAllowed(TreePath sourcePath, TreePath targetPath) {
                        return isPracticeWeekNode(targetPath) && isExerciseDefinitionPath(sourcePath);
                    }

                }));
    }

    private boolean isExerciseDefinitionPath(TreePath sourcePath) {
        PathExtractor pathExtractor = new PathExtractor(sourcePath).invoke();
        return pathExtractor.getExerciseDefinition() != null;
    }

    private boolean isPracticeWeekNode(TreePath targetPath) {
        PathExtractor pathExtractor = new PathExtractor(targetPath).invoke();
        return pathExtractor.getPracticeWeek() != null;
    }

    private boolean isExerciseDefOfPracticeWeekSelected() {
        return selectedExerciseDef != null && selectedPracticeWeek != null;
    }

    private boolean isStandaloneExerciseDefSelected() {
        return selectedExerciseDef != null && selectedPracticeWeek == null;
    }

    private boolean isPracticeWeekSelected() {
        return selectedExerciseDef == null && selectedPracticeWeek != null;
    }

    private void updateExerciseOverview() {
        if (selectedExerciseDef != null && selectedPracticeWeek != null) {
            setExerciseOverviewData(Arrays.asList(selectedExerciseDef));
        } else if (selectedPracticeWeek != null) {
            setExerciseOverviewData(selectedPracticeWeek.getExerciseDefinitions());
        } else {
            dontShowExerciseOverview();
        }

    }

    private void dontShowExerciseOverview() {
        exerciseOverviewTable.setModel(new DefaultTableModel());
    }

    private void setSelectedPracticeWeek(PracticeWeek practiceWeek) {
        this.selectedPracticeWeek = practiceWeek;
    }

    private void updateModelFromView() {
        if (selectedExerciseDef != null) {
            getData(selectedExerciseDef);
        }
    }

    private void addFocusListener(JComponent... titleFields) {
        for (JComponent field : titleFields) {
            field.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    if (selectedExerciseDef != null) {

                        getData(selectedExerciseDef);
                    }
                }
            });
        }

    }

    private void enableExcerciseDefPanel(boolean enabled) {
        descriptionField.setEnabled(enabled);
        durationField.setEnabled(enabled);
        bpmField.setEnabled(enabled);
        titleField.setEnabled(enabled);
    }


    private void setExerciseOverviewData(List<ExerciseDefinition> exerciseDefinitionList) {
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
                        checkBox.setSelected(((ExerciseInstance) valueFromModel).isDone());
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
            columns.add(DATE_TIME_FORMATTER.print(localDate));
        }
        return columns.toArray(new String[columns.size()]);
    }


    private LocalDateRange getColumnRange() {
        return new LocalDateRange(selectedPracticeWeek.getInterval());
    }


    private void showPracticeInstancesOfWeek(DefaultTableModel dataModel, int row, ExerciseDefinition exerciseDefinition) {
        int column = 1;
        for (ExerciseInstance exerciseInstance : selectedPracticeWeek.getExerciseInstances(exerciseDefinition)) {
            dataModel.setValueAt(exerciseInstance, row, column);
            column++;
        }
    }

    public void setSelectedExerciseDef(ExerciseDefinition data) {
        selectedExerciseDef = data;

        if (data != null) {
            descriptionField.setText(data.getDescription());
            titleField.setText(data.getTitle());
            durationField.setText(Integer.valueOf(data.getMinutes()).toString());
            bpmField.setText(Integer.valueOf(data.getBpm()).toString());
            // TODO MJU select tags
            // tagList.setSelectedIndices(data.get);
        } else {
            descriptionField.setText(null);
            titleField.setText(null);
            durationField.setText(null);
            bpmField.setText(null);
            tagList.clearSelection();
            tagList.setListData(new Object[]{});
        }

    }

    public void getData(ExerciseDefinition data) {

        data.setDescription(descriptionField.getText());
        data.setTitle(titleField.getText());
        data.setMinutes(Integer.parseInt(durationField.getText()));
        data.setBpm(Integer.parseInt(bpmField.getText()));
        data.setTags(tagList.getSelectedValuesList());

    }


    public void setData(PracticeBuddyBean data) {

        this.practiceBuddyBean = data;
        buildTree();

    }

    private void buildTree() {
        practicePlansNode = new DefaultMutableTreeNode("Practice Plans");
        allExercisesNode = new DefaultMutableTreeNode("All Exercises");
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();

        root.insert(practicePlansNode, 0);
        root.insert(allExercisesNode, 1);


        DefaultTreeModel treeRoot = new DefaultTreeModel(root, true);
        addPracticePlanModels(practicePlansNode);
        addAllExcercisesModels(allExercisesNode);
        practicePlanTree.setModel(treeRoot);
    }

    private void addAllExcercisesModels(DefaultMutableTreeNode rootNode) {
        for (ExerciseDefinition exerciseDefinition : practiceBuddyBean.getExerciseDefinitions()) {
            DefaultMutableTreeNode node = createExcerciseDefNode(exerciseDefinition);
            rootNode.insert(node, 0);
        }
    }

    private void addPracticePlanModels(DefaultMutableTreeNode root) {
        for (final PracticeWeek practiceWeek : practiceBuddyBean.getPracticeWeeks()) {
            DefaultMutableTreeNode node = createPracticeWeekNode(practiceWeek);
            root.insert(node, 0);
            addExerciseDefNodes(practiceWeek, node);

        }
    }

    private DefaultMutableTreeNode createPracticeWeekNode(final PracticeWeek practiceWeek) {
        return new DefaultMutableTreeNode(practiceWeek) {

            @Override
            public String toString() {
                return DATE_TIME_FORMATTER.print(practiceWeek.getDateFrom())
                        + "-" + DATE_TIME_FORMATTER.print(practiceWeek.getDateTo());
            }
        };
    }

    private void addExerciseDefNodes(PracticeWeek practiceWeek, DefaultMutableTreeNode practicePlanNode) {
        for (final ExerciseDefinition exerciseDefinition : practiceWeek.getExerciseDefinitions()) {
            DefaultMutableTreeNode node = createExcerciseDefNode(exerciseDefinition);
            practicePlanNode.insert(node, 0);

        }
    }

    private DefaultMutableTreeNode createExcerciseDefNode(final ExerciseDefinition exerciseDefinition) {
        return new DefaultMutableTreeNode(exerciseDefinition, false) {

            @Override
            public String toString() {
                return exerciseDefinition.getTitle();
            }
        };
    }

    private void createUIComponents() {
        exerciseOverviewTable = new JTable() {


        };
        exerciseOverviewTable.getTableHeader().setEnabled(true);

        exerciseOverviewTable.setShowVerticalLines(false);
        exerciseOverviewTable.setShowHorizontalLines(true);
    }

    public void refresh() {
        buildTree();
    }

    private class PracticePlanTreeActionListener implements ActionListener {
        public void actionPerformed(ActionEvent ae) {

            if (ae.getActionCommand().equals("insert")) {
                insertInModel();
            }
            if (ae.getActionCommand().equals("remove")) {
                deleteFromModel();

            }
        }
    }

    private void insertInModel() {
        DefaultMutableTreeNode node;

        if (getSelectedNode().equals(practicePlansNode)) {
            PracticeWeek newPracticePlan = practiceBuddyBean.createNewPracticePlan(DateTime.now().toLocalDate(), DateTime.now().plusDays(6).toLocalDate());
            node = createPracticeWeekNode(newPracticePlan);
        } else {
            ExerciseDefinition newExerciseDefinition = practiceBuddyBean.createNewExerciseDefinition();
            node = createExcerciseDefNode(newExerciseDefinition);

            if (selectedPracticeWeek != null) {
                selectedPracticeWeek.activate(newExerciseDefinition);
                addNewNode(node, allExercisesNode);

            }
        }
        addNewNode(node, getSelectedNode());


    }

    private void deleteFromModel() {
        if (isExerciseDefOfPracticeWeekSelected()) {
            selectedPracticeWeek.deleteExerciseDef(selectedExerciseDef);
            removeSelectedNodeFromTree();

        } else if (isStandaloneExerciseDefSelected()) {
            practiceBuddyBean.deleteExerciseDefinition(selectedExerciseDef);
            removeSelectedNodesWithSameUserObjectFromTree();

        } else if (isPracticeWeekSelected()) {
            practiceBuddyBean.deletePracticeWeek(selectedPracticeWeek);
            removeSelectedNodeFromTree();

        }

    }

    private void removeSelectedNodesWithSameUserObjectFromTree() {
        DefaultMutableTreeNode lastPathComponent = getSelectedNode();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) practicePlanTree.getModel().getRoot();

        removeAllNodesWithSameModel(root, lastPathComponent.getUserObject());

    }

    private void removeAllNodesWithSameModel(DefaultMutableTreeNode root, Object userObject) {
        if (root.getUserObject() != null && root.getUserObject().equals(userObject)) {
            removeNode(root);
        } else {
            for (int i = 0; i < root.getChildCount(); i++) {
                removeAllNodesWithSameModel((DefaultMutableTreeNode) root.getChildAt(i), userObject);
            }
        }

    }


    private void removeSelectedNodeFromTree() {
        DefaultMutableTreeNode lastPathComponent = getSelectedNode();
        removeNode(lastPathComponent);
    }

    private void removeNode(DefaultMutableTreeNode nodeToRemove) {
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) nodeToRemove.getParent();
        int nodeIndex = parent.getIndex(nodeToRemove);
        nodeToRemove.removeAllChildren();
        parent.remove(nodeIndex);
        refreshTree(parent);
    }

    private DefaultMutableTreeNode getSelectedNode() {
        TreePath selectionPath = practicePlanTree.getSelectionPath();
        return (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
    }

    private void addNewNode(DefaultMutableTreeNode node, DefaultMutableTreeNode parent) {

        parent.add(node);
        refreshTree(parent);
    }

    private void refreshTree(DefaultMutableTreeNode lastPathComponent) {
        ((DefaultTreeModel) practicePlanTree.getModel()).nodeStructureChanged(lastPathComponent);
    }


}
