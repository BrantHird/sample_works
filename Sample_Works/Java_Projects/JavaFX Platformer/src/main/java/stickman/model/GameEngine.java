package stickman.model;

import stickman.model.State.SaveGame;
import stickman.model.config.ConfigParser;
import stickman.model.levels.Level;
import stickman.model.levels.ScoreKeeper;

public interface GameEngine {
  Level getCurrentLevel();

  void startLevel();

  void restartLevel();

  // Hero inputs - boolean for success (possibly for sound feedback)
  boolean jump();

  boolean moveLeft();

  boolean moveRight();

  boolean stopMoving();

  long getTimeSinceStart();

  long getHeroHealth();

  long getScore();

  void tick();

  boolean isGameOver();

  void setHeadsUpDisplayMessage(String message);

  /**
   * When a level is restarted we need to clean out the old entity views, this is how we signal to
   * the
   *
   * @return true if the game engine needs the entities refreshed
   */
  boolean needsRefresh();

  /** After a refresh is completed, the clean method must be called to reset the flag */
  void clean();

  String getHeadsUpDisplayMessage();

  ScoreKeeper getScoreKeeper();

  void saveGame();

  void restoreGame();

  void setCurrentLevel(Level current);

  void setScoreKeeper(ScoreKeeper sc);

  void setStartTime(long StartTime);

  void setLevelTracker(int levelTracker);

  String getSaveMessage();

  ConfigParser getConfig();

  void setConfig(ConfigParser config);




}
