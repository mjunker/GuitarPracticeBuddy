package ch.guitarpracticebuddy.util;

import ch.guitarpracticebuddy.ui.PracticeForm;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

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
        InputStream bufferedIn = new BufferedInputStream(PracticeForm.class.getResourceAsStream(soundFile.getFileName()));

        AudioInputStream inputStream = AudioSystem.getAudioInputStream(bufferedIn);
        clip.open(inputStream);
        return clip;
    }
}
