package ch.guitarpracticebuddy.ui;

import ch.guitarpracticebuddy.domain.ExerciseDefinition;
import ch.guitarpracticebuddy.domain.PracticeBuddyBean;
import ch.guitarpracticebuddy.domain.PracticeWeek;
import ch.guitarpracticebuddy.util.Constants;
import com.google.common.base.Joiner;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.*;
import java.util.Arrays;

public class PracticePlanManagerTree extends JTree {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendDayOfWeekShortText()
            .appendLiteral(", ")
            .appendDayOfMonth(1)
            .appendLiteral(".")
            .appendMonthOfYear(1)
            .appendLiteral(".")
            .appendYear(2, 2)
            .toFormatter();

    public static final DateTimeFormatter DAY_ONLY_FORMATTER = new DateTimeFormatterBuilder()
            .appendDayOfWeekShortText()
            .toFormatter();

    private DefaultMutableTreeNode practicePlansNode;
    private DefaultMutableTreeNode allExercisesNode;

    private ExerciseDefinition selectedExerciseDefinition;
    private PracticeWeek selectedPracticeWeek;
    private PlanningForm planningForm;
    private PracticeBuddyBean practiceBuddyBean;

    public PracticePlanManagerTree(PlanningForm planningForm) {
        this.planningForm = planningForm;
        addSelectionListener();
        addKeyListener();
        configure();
        setupTreeContextMenu();
        initDropTarget();
    }

    private void configure() {
        setRootVisible(false);

        setDragEnabled(true);
        getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);
        setDropMode(DropMode.USE_SELECTION);

    }

    private void addKeyListener() {
        addKeyListener(new KeyListener() {

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

    private void addSelectionListener() {
        addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {

                PathExtractor pathExtractor = new PathExtractor(e.getPath()).invoke();
                if (!pathExtractor.isRootNodeSelected()) {
                    planningForm.getData(selectedExerciseDefinition);
                    updateLabels();

                    selectedPracticeWeek = null;
                    selectedExerciseDefinition = null;

                    if (pathExtractor.isUserModelIsExcerciseDef()) {
                        selectedExerciseDefinition = pathExtractor.getExerciseDefinition();
                        planningForm.setSelectedExerciseDef(selectedExerciseDefinition);

                        if (pathExtractor.isExerciseDefInPracticeWeek()) {
                            setSelectedPracticeWeek(pathExtractor.getPracticeWeek());
                        }

                    } else if (pathExtractor.isUserModelIsPracticeWeek()) {
                        setSelectedPracticeWeek(pathExtractor.getPracticeWeek());
                        planningForm.setSelectedExerciseDef(null);
                    }
                    updateExerciseOverview();
                    planningForm.enableExcerciseDefPanel(pathExtractor.isUserModelIsExcerciseDef());
                } else {
                    planningForm.enableExcerciseDefPanel(false);
                }

            }
        });
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

        addMouseListener(
                new MouseAdapter() {
                    public void mouseReleased(MouseEvent e) {
                        setSelectionPath(getClosestPathForLocation(e.getX(), e.getY()));
                        if (e.getButton() == Constants.RIGHT_MOUSE) {
                            popup.show((JComponent) e.getSource(), e.getX(), e.getY());
                        }
                    }
                }
        );
    }

    private void initDropTarget() {
        setDropTarget(new DropTarget(this, TransferHandler.COPY,
                new DropTargetAdapter() {
                    @Override
                    public void drop(DropTargetDropEvent event) {

                        TreePath selectionPath = getSelectionPath();
                        Point dropLocation = event.getLocation();
                        TreePath targetPath = getClosestPathForLocation(
                                dropLocation.x, dropLocation.y);

                        if (isDropAllowed(selectionPath, targetPath)) {
                            PathExtractor targetPathExtractor = new PathExtractor(targetPath).invoke();
                            PathExtractor sourcePathExtractor = new PathExtractor(selectionPath).invoke();
                            DefaultMutableTreeNode selectedNode = getSelectedNode();
                            boolean activeSuccesful = targetPathExtractor.getPracticeWeek().activate(sourcePathExtractor.getExerciseDefinition());

                            if (activeSuccesful) {
                                targetPathExtractor.getPracticeWeekNode().add((MutableTreeNode) selectedNode.clone());
                                event.dropComplete(true);
                                updateUI();
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
        return selectedExerciseDefinition != null && selectedPracticeWeek != null;
    }

    private boolean isStandaloneExerciseDefSelected() {
        return selectedExerciseDefinition != null && selectedPracticeWeek == null;
    }

    private boolean isPracticeWeekSelected() {
        return selectedExerciseDefinition == null && selectedPracticeWeek != null;
    }

    public void updateExerciseOverview() {
        if (selectedExerciseDefinition != null && selectedPracticeWeek != null) {
            planningForm.setExerciseOverviewData(Arrays.asList(selectedExerciseDefinition));
        } else if (selectedPracticeWeek != null) {
            planningForm.setExerciseOverviewData(selectedPracticeWeek.getExerciseDefinitions());
        } else {
            planningForm.dontShowExerciseOverview();
        }

    }

    private void setSelectedPracticeWeek(PracticeWeek practiceWeek) {
        this.selectedPracticeWeek = practiceWeek;
    }

    public PracticeWeek getSelectedPracticeWeek() {
        return selectedPracticeWeek;
    }

    public void updateLabels() {

        if (getSelectionPath() != null) {
            // hack, so labels don't get abbreviated
            getModel().valueForPathChanged(getSelectionPath(),
                    ((DefaultMutableTreeNode) getSelectionPath().getLastPathComponent()).getUserObject());
        }

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

    public void refresh() {
        buildTree();
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
            selectedPracticeWeek.deleteExerciseDef(selectedExerciseDefinition);
            removeSelectedNodeFromTree();

        } else if (isStandaloneExerciseDefSelected()) {
            practiceBuddyBean.deleteExerciseDefinition(selectedExerciseDefinition);
            removeSelectedNodesWithSameUserObjectFromTree();

        } else if (isPracticeWeekSelected()) {
            practiceBuddyBean.deletePracticeWeek(selectedPracticeWeek);
            removeSelectedNodeFromTree();

        }

    }

    private void removeSelectedNodesWithSameUserObjectFromTree() {
        DefaultMutableTreeNode lastPathComponent = getSelectedNode();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) getModel().getRoot();

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
        TreePath selectionPath = getSelectionPath();
        return (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
    }

    private void addNewNode(DefaultMutableTreeNode node, DefaultMutableTreeNode parent) {

        parent.insert(node, 0);
        refreshTree(parent);
    }

    private void refreshTree(DefaultMutableTreeNode lastPathComponent) {
        DefaultTreeModel model = (DefaultTreeModel) getModel();
        model.nodeStructureChanged(lastPathComponent);
        repaint();
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
        setModel(treeRoot);
        expandAllRows();
    }

    private void expandAllRows() {
        for (int i = 0; i < getRowCount(); i++) {
            expandRow(i);
        }
    }

    private void addAllExcercisesModels(DefaultMutableTreeNode rootNode) {
        for (ExerciseDefinition exerciseDefinition : practiceBuddyBean.getExerciseDefinitions()) {
            DefaultMutableTreeNode node = createExcerciseDefNode(exerciseDefinition);
            rootNode.add(node);
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
                        + "-" + DATE_TIME_FORMATTER.print(practiceWeek.getDateTo())
                        + " " + createTimeString(practiceWeek) + "";
            }
        };
    }

    private String createTimeString(PracticeWeek practiceWeek) {
        int timeInMinutes = practiceWeek.calculateTotalMinutes();
        int hours = timeInMinutes / 60;
        int minutes = timeInMinutes % 60;

        String hourString = null;
        String minuteString = null;

        if (minutes > 0) {
            minuteString = minutes + "m";
        }

        if (hours > 0) {
            hourString = hours + "h";
        }

        return Joiner.on(" ").skipNulls().join(hourString, minuteString);
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

    public void setPracticeBuddyBean(PracticeBuddyBean practiceBuddyBean) {
        this.practiceBuddyBean = practiceBuddyBean;
        refresh();
    }
}
