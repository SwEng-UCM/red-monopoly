package View;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class MusicPlayer {
    private Clip clip;
    private FloatControl volumeControl;

    public void playMusic(String urlString) {
        try {
            URL audioUrl = new URL(urlString);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioUrl);
            clip = AudioSystem.getClip();
            clip.open(audioStream);

            // Loop music indefinitely
            clip.loop(Clip.LOOP_CONTINUOUSLY);

            // Volume control
            volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            setVolume(0.5f);
            clip.start();
        } catch (Exception e) {
            System.err.println("Failed to play music: " + urlString);
            e.printStackTrace();
        }
    }

    public void setVolume(float volume) {
        if (volumeControl != null) {
            float min = volumeControl.getMinimum();
            float max = volumeControl.getMaximum();
            float newVolume = min + (max - min) * volume;
            volumeControl.setValue(newVolume);
        }
    }

    public void stopMusic() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    public static void playSoundEffect(URL soundUrl) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundUrl);
            Clip soundClip = AudioSystem.getClip();
            soundClip.open(audioStream);
            soundClip.start();
        } catch (Exception e) {
            System.err.println("Failed to play sound effect: " + soundUrl);
            e.printStackTrace();
        }
    }

    public float getVolume() {
        if (volumeControl != null) {
            float min = volumeControl.getMinimum();
            float max = volumeControl.getMaximum();
            float current = volumeControl.getValue();
            return (current - min) / (max - min);
        }
        return 0.5f;
    }
}
