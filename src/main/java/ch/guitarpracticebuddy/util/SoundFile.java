package ch.guitarpracticebuddy.util;

/**
 * Created with IntelliJ IDEA.
 * User: mjunker
 * Date: 1/29/13
 * Time: 10:17 PM
 * To change this template use File | Settings | File Templates.
 */
public enum SoundFile {
    DONE("done.wav"),
    CLICK("click.wav");

    private final String fileName;

    SoundFile(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
