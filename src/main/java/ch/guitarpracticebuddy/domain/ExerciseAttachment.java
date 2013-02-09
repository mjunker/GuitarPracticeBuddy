package ch.guitarpracticebuddy.domain;

import javax.persistence.Entity;

@Entity
public class ExerciseAttachment {

    private String path;

    public ExerciseAttachment(String filePath) {
        this.path = filePath;
    }

    public String getFilePath() {
        return path;
    }

}
