package ch.guitarpracticebuddy.util;

import ch.guitarpracticebuddy.javafx.GuitarPracticeBuddyApplication;
import org.apache.commons.io.IOUtils;

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
        InputStream bufferedIn = new BufferedInputStream(GuitarPracticeBuddyApplication.class.getResourceAsStream(soundFile.getFileName()));

        AudioInputStream inputStream = AudioSystem.getAudioInputStream(bufferedIn);
        clip.open(inputStream);
        return clip;
    }

    public static void play(SoundFile soundFile) {
        try {
            tryToPlay(soundFile);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void tryToPlay(SoundFile soundFile) throws LineUnavailableException, IOException {
        final AudioFormat audioFormat = new AudioFormat(44100, 8, 1, true, true);
        final DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat, 1);
        final SourceDataLine soundLine;

        soundLine = (SourceDataLine) AudioSystem.getLine(info);

        soundLine.open(audioFormat);
        soundLine.start();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(GuitarPracticeBuddyApplication.class.getResourceAsStream(soundFile.getFileName()));

        byte[] bytes = IOUtils.toByteArray(bufferedInputStream);
        int bufferSize = 2200;
        byte[] buffer = new byte[bufferSize];
        for (int i = 0; i < bufferSize; i++) {

            buffer[i] = (byte) (30);
        }
        // the next call is blocking until the entire buffer is
        // sent to the SourceDataLine
        soundLine.write(bytes, 0, bytes.length);

    }
}
