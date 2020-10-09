package stickman.view;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SoundPlayer {
  private Map<String, MediaPlayer> sounds = new HashMap<>();

  public SoundPlayer() {
    URL mediaUrl = getClass().getResource("/jump.wav");
    String jumpURL = mediaUrl.toExternalForm();

    Media sound = new Media(jumpURL);
    MediaPlayer mediaPlayer = new MediaPlayer(sound);
    sounds.put("jump", mediaPlayer);
  }

  /** Play the jumping sound */
  public void playJumpSound() {
    MediaPlayer jumpPlayer = sounds.get("jump");
    jumpPlayer.stop();
    jumpPlayer.play();
  }
}
