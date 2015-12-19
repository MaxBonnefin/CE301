package Utilities;


import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import java.io.File;


public class SoundManager {

    final static String path = "res/Sounds/";

    // note: having too many clips open may cause
    // "LineUnavailableException: No Free Voices"

    public final static Clip arrr = getClip("arrr");
    public final static Clip menuMove = getClip("menuMove");
    public final static Clip menuSelect = getClip("menuSelect");


    public final static Clip[] clips = {arrr,
                                       menuMove,
                                       menuSelect,
                                       };

    public static void play(Clip clip) {
        clip.setFramePosition(0);
        clip.start();
    }

    private static Clip getClip(String filename) {
        Clip clip = null;
        try {
            clip = AudioSystem.getClip();
            AudioInputStream sample = AudioSystem.getAudioInputStream(new File(path
                    + filename + ".wav"));
            clip.open(sample);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clip;
    }

}

