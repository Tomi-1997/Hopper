import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Sound extends TemplateObject
{
    ArrayList<Clip> hitSounds;
    public Sound(String bgSound, String hitSound)
            throws UnsupportedAudioFileException, IOException, LineUnavailableException
    {

        /*
            Starting background music
         */
        URL url = this.getClass().getClassLoader().getResource(bgSound);
        assert url != null;
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
        Clip clip = AudioSystem.getClip();
        clip.open(audioIn);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        clip.start();

        /*
            Load hit sound to play when a player hits an obstacle
         */
        hitSounds = new ArrayList<>();
        for (int i = 1; i <= Game.beepFiles; i++)
            load(hitSound + i + ".wav");

    }

    private void load(String s)
            throws UnsupportedAudioFileException, IOException, LineUnavailableException
    {
        URL url = this.getClass().getClassLoader().getResource(s);
        assert url != null;
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
        Clip clip = AudioSystem.getClip();
        clip.open(audioIn);
        hitSounds.add(clip);
    }

    @Override
    public void collide(TemplateObject to)
    {
        if (Math.random() > Game.beepProb) return;
        hitSounds.get(Game.randInt(0, hitSounds.size())).start();
    }
}