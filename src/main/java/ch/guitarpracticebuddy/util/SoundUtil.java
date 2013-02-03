package ch.guitarpracticebuddy.util;

import ch.guitarpracticebuddy.ui.PracticeForm;

import javax.sound.sampled.*;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: mjunker
 * Date: 1/29/13
 * Time: 10:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class SoundUtil {


    public static void playSound(SoundFile soundFile) {
        try {

            loadClip(soundFile).start();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static Clip loadClip(SoundFile soundFile) throws LineUnavailableException, UnsupportedAudioFileException, IOException {
        Clip clip = AudioSystem.getClip();
        AudioInputStream inputStream = AudioSystem.getAudioInputStream(PracticeForm.class.getResourceAsStream(soundFile.getFileName()));
        clip.open(inputStream);
        return clip;
    }
}
