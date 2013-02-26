package ch.guitarpracticebuddy.util;

import ch.guitarpracticebuddy.javafx.GuitarPracticeBuddyApplication;
import org.apache.commons.io.IOUtils;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mjunker
 * Date: 1/29/13
 * Time: 10:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class SoundUtil {

    private static Map<SoundFile, byte[]> soundFileCache = new HashMap<>();

    public static void playSound(SoundFile soundFile) {
        try {
            final Clip clip = AudioSystem.getClip();
            clip.open(loadClip(soundFile));
            clip.start();
            clip.addLineListener(new LineListener() {
                @Override
                public void update(LineEvent event) {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                }
            });

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static AudioInputStream loadClip(SoundFile soundFile) throws LineUnavailableException, UnsupportedAudioFileException, IOException {
        if (soundFileCache.get(soundFile) == null) {
            InputStream fileInputStream = GuitarPracticeBuddyApplication.class.getResourceAsStream(soundFile.getFileName());
            soundFileCache.put(soundFile, IOUtils.toByteArray(fileInputStream));
        }
        BufferedInputStream bufferedIn = new BufferedInputStream(new ByteArrayInputStream(soundFileCache.get(soundFile)));
        AudioInputStream inputStream = AudioSystem.getAudioInputStream(bufferedIn);
        return inputStream;
    }


}
