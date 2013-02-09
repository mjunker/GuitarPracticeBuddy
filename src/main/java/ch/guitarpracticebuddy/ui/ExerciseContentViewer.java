package ch.guitarpracticebuddy.ui;

import ch.guitarpracticebuddy.domain.ExerciseAttachment;
import ch.guitarpracticebuddy.domain.ExerciseDefinition;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExerciseContentViewer {
    private JPanel rootPanel;
    private JButton previousButton;
    private JButton nextButton;
    private JLabel contentLabel;
    private ExerciseDefinition exerciseDefinition;
    private ExerciseAttachment currentAttachment;

    public ExerciseContentViewer() {
        addListeners();
    }

    private void addListeners() {
        previousButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                showPrevious();
            }
        });

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                showNext();
            }
        });
    }

    private void showPrevious() {
        currentAttachment = getAttachments().get(Math.max(0, getAttachments().indexOf(currentAttachment) - 1));
        display();

    }

    private void showNext() {
        currentAttachment = getAttachments().get(Math.min(getAttachments().size() - 1, getAttachments().indexOf(currentAttachment) + 1));
        display();
    }

    public void display(ExerciseDefinition exerciseDefinition) {
        this.exerciseDefinition = exerciseDefinition;
        if (exerciseDefinition != null && !exerciseDefinition.getAttachments().isEmpty()) {
            currentAttachment = getAttachments().get(0);
            enable(true);
        } else {
            currentAttachment = null;
            enable(false);

        }
        display();
    }

    private void enable(boolean enabled) {
        nextButton.setEnabled(enabled);
        previousButton.setEnabled(enabled);
    }

    private void display() {
        if (currentAttachment != null) {
            try {
                BufferedImage image = ImageIO.read(FileUtil.toFiles(exerciseDefinition, currentAttachment));
                ImageIcon imageIcon = new ImageIcon(scaleImage(image));
                contentLabel.setIcon(imageIcon);
            } catch (IOException e) {
            }
        } else {
            contentLabel.setIcon(null);
        }

    }

    private Image scaleImage(BufferedImage image) {
        int actualWidth = Math.min(contentLabel.getWidth(), image.getWidth());
        float scale = ((float) actualWidth) / image.getWidth();

        return image.getScaledInstance(actualWidth,
                (int) (image.getHeight() * scale), Image.SCALE_SMOOTH);
    }

    private List<ExerciseAttachment> getAttachments() {
        if (exerciseDefinition == null) {
            return new ArrayList<ExerciseAttachment>();
        }
        return exerciseDefinition.getAttachments();
    }
}
