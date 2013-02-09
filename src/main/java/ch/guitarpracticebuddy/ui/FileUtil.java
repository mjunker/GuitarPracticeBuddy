package ch.guitarpracticebuddy.ui;

import ch.guitarpracticebuddy.domain.ExerciseAttachment;
import ch.guitarpracticebuddy.domain.ExerciseDefinition;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    public static final String SEPARATOR = "/";
    public static final String EXERCISE_FOLDER_PREFIX = "exercise_data/exercise_";

    public static List<String> copyFilesToApplicationHome(File[] selectedFiles, ExerciseDefinition exerciseDefinition) {
        List<String> fileNames = new ArrayList<String>();
        for (File selectedFile : selectedFiles) {
            try {
                new File(getApplicationHome() + SEPARATOR + getExerciseDirectory(exerciseDefinition)).mkdirs();
                File destination = new File(getExerciseDirectoryAbsolute(exerciseDefinition) + selectedFile.getName());
                Files.copy(selectedFile, destination);
                fileNames.add(selectedFile.getName());
            } catch (IOException e) {
                throw new IllegalArgumentException("Cannot copy file", e);
            }
        }
        return fileNames;
    }

    private static String getExerciseDirectoryAbsolute(ExerciseDefinition exerciseDefinition) {
        return getApplicationHome() + SEPARATOR + getExerciseDirectory(exerciseDefinition) + SEPARATOR;
    }

    private static String getExerciseDirectory(ExerciseDefinition exerciseDefinition) {
        return EXERCISE_FOLDER_PREFIX + exerciseDefinition.getId();
    }

    private static String getApplicationHome() {
        return System.getProperty("guitarbuddy.home");
    }

    public static File toFiles(ExerciseDefinition exerciseDefinition, ExerciseAttachment exerciseAttachment) {
        return new File(getExerciseDirectoryAbsolute(exerciseDefinition) + exerciseAttachment.getFilePath());
    }
}
