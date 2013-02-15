package ch.guitarpracticebuddy.ui;

import ch.guitarpracticebuddy.domain.ExerciseAttachment;
import ch.guitarpracticebuddy.domain.ExerciseDefinition;
import com.google.common.io.Files;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    public static final String SEPARATOR = "/";
    public static final String EXERCISE_FOLDER_PREFIX = "exercise_data/exercise_";
    private static final int DEFAULT_IMAGE_WIDTH = 800;

    public static List<String> copyFilesToApplicationHome(File[] selectedFiles, ExerciseDefinition exerciseDefinition) {
        List<String> fileNames = new ArrayList<String>();
        for (File selectedFile : selectedFiles) {
            try {
                new File(getApplicationHome() + SEPARATOR + getExerciseDirectory(exerciseDefinition)).mkdirs();
                final File destination = new File(getExerciseDirectoryAbsolute(exerciseDefinition) + selectedFile.getName());
                Files.copy(selectedFile, destination);
                scaleImageInNewThread(destination);
                fileNames.add(selectedFile.getName());
            } catch (IOException e) {
                throw new IllegalArgumentException("Cannot copy file", e);
            }
        }
        return fileNames;
    }

    private static void scaleImageInNewThread(final File destination) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    BufferedImage read = ImageIO.read(destination);
                    ImageIO.write(scaleImage(read), "jpg", destination);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }

    private static String getExerciseDirectoryAbsolute(ExerciseDefinition exerciseDefinition) {
        return getApplicationHome() + SEPARATOR + getExerciseDirectory(exerciseDefinition) + SEPARATOR;
    }

    private static String getExerciseDirectory(ExerciseDefinition exerciseDefinition) {
        return EXERCISE_FOLDER_PREFIX + exerciseDefinition.getCode();
    }

    private static String getApplicationHome() {
        return System.getProperty("guitarbuddy.home");
    }

    public static File toFiles(ExerciseDefinition exerciseDefinition, ExerciseAttachment exerciseAttachment) {
        return new File(getExerciseDirectoryAbsolute(exerciseDefinition) + exerciseAttachment.getFilePath());
    }

    public static BufferedImage scaleImage(BufferedImage image) {
        if (image.getWidth() == DEFAULT_IMAGE_WIDTH) {
            return image;
        }

        int actualWidth = Math.min(DEFAULT_IMAGE_WIDTH, image.getWidth());
        float scale = ((float) actualWidth) / image.getWidth();

        Image scaledInstance = image.getScaledInstance(actualWidth,
                (int) (image.getHeight() * scale), Image.SCALE_SMOOTH);

        BufferedImage bi = new BufferedImage(scaledInstance.getWidth(null),
                scaledInstance.getHeight(null),
                image.getType());

        Graphics2D grph = (Graphics2D) bi.getGraphics();

        grph.drawImage(scaledInstance, 0, 0, null);
        grph.dispose();

        return bi;
    }
}
