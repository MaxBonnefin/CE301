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
    public final static Clip charDeath = getClip("char_Death");
    public final static Clip brawlerDeath = getClip("brawler_Death");
    public final static Clip brawlerHit = getClip("brawler_Hit");
    public final static Clip slash = getClip("slash");
    public final static Clip swoosh = getClip("swoosh");



    public final static Clip[] clips = {arrr,
                                       menuMove,
                                       menuSelect,
                                       charDeath,
                                       brawlerHit,
                                       brawlerDeath,
                                       slash,
                                       swoosh,
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

