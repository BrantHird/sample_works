package stickman.model;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import stickman.model.State.SaveGame;
import stickman.model.State.StateController;
import stickman.model.config.ConfigParser;
import stickman.model.config.JsonConfigParser;
import stickman.model.entities.Entity;
import stickman.model.entities.MovableEntity;
import stickman.model.entities.character.Hero;
import stickman.model.levels.Level;
import stickman.model.levels.LevelBuilder;
import stickman.model.levels.ScoreKeeper;
import stickman.view.SoundPlayer;

import static java.lang.Thread.sleep;

/** An implementation of the Game Engine interface, allows control of the player */
public class GameEngineImpl implements GameEngine {
  private Level currentLevel;
  private ConfigParser config;
  private String headsUpDisplayMessage = "";
  private boolean needsRefresh = false;
  private boolean gameFinished = false;
  private SoundPlayer soundPlayer = new SoundPlayer();
  private long startTime;
  private long endTime;
  private int levelTracker = 1;
  private ScoreKeeper scoreKeeper;
  private StateController stateController;
  private int timer = 0;

  public GameEngineImpl(ConfigParser config) {
    this.config = config;
    this.scoreKeeper = new ScoreKeeper();
    this.stateController = new StateController(this);
  }

  @Override
  public Level getCurrentLevel() {
    return this.currentLevel;
  }

  @Override
  public void restartLevel() {
      currentLevel.getEntities().forEach(Entity::delete);
      needsRefresh = true;
      this.scoreKeeper.resetExtraPoints();
      headsUpDisplayMessage = null ;
  }

  @Override
  public void startLevel() {
    if (currentLevel != null) {
      // If we're restarting delete all the entities, so they're no longer rendered
      currentLevel.getEntities().forEach(Entity::delete);
      needsRefresh = true;
      this.scoreKeeper.resetExtraPoints();
    }

      currentLevel = LevelBuilder.fromConfig(config).build();
      startTime = System.currentTimeMillis();
      this.scoreKeeper.setCurrentLevel(currentLevel);

    if(headsUpDisplayMessage == null || headsUpDisplayMessage.isBlank() || headsUpDisplayMessage.isEmpty()){
        headsUpDisplayMessage = "LEVEL " + levelTracker + "\n" + currentLevel.getName();
      }

  }

  @Override
  public boolean jump() {
    if (currentLevel.getHero().jump()) {
      soundPlayer.playJumpSound();
      return true;
    }
    return false;
  }

  @Override
  public boolean moveLeft() {
    return currentLevel.getHero().moveLeft();
  }

  @Override
  public boolean moveRight() {
    return currentLevel.getHero().moveRight();
  }

  @Override
  public boolean stopMoving() {
    return currentLevel.getHero().stopMoving();
  }

  @Override
  public long getTimeSinceStart() {
    if (gameFinished) {
      return endTime - startTime;
    }
    return System.currentTimeMillis() - startTime;
  }

  @Override
  public long getHeroHealth() {
    if (currentLevel.getHero() != null) {
      return currentLevel.getHero().getHealth();
    }
    return 0;
  }

  private void updateEntities() {
    // Collect the entities from the current level
    List<Entity> staticEntities = currentLevel.getStaticEntities();
    List<MovableEntity> dynamicEntities = currentLevel.getDynamicEntities();

    // Remove any dead entities
    staticEntities.removeIf(Entity::isDeleted);
    dynamicEntities.removeIf(Entity::isDeleted);

    // Move everything that can move
    for (MovableEntity a : dynamicEntities) {
      a.moveTick();
    }

    // Check for collisions
    for (MovableEntity a : dynamicEntities) {
      for (Entity b : currentLevel.getEntities()) {
        if (a != b && a.overlappingSameLayer(b)) {
          b.handleCollision(a);
          // Only do one collision at a time
          break;
        }
      }
    }
  }

  private void updateState() {

    if(getSaveMessage() != null){
      if(timer < 30){
        timer ++;
      }
      else{
        stateController.removeSaveMessage();
      }
    }
    else{
      timer = 0 ;
    }

    Hero hero = currentLevel.getHero();

    // Check if we need to change state based on the hero
    if (hero.isFinished()) {

      scoreKeeper.addLevel(currentLevel, getScore());
      levelTracker ++ ;
      endTime = System.currentTimeMillis();
      currentLevel.getEntities().forEach(Entity::delete);
      needsRefresh = true;


      if(config.getNextLevel()!= null){
        advanceLevel();
      }


      else {
        headsUpDisplayMessage = "GAME OVER:\nWINNER!!";
        gameFinished = true;
      }

    }


    else if (hero.isDeleted()) {
      headsUpDisplayMessage = "YOU LOSE: TRY AGAIN!";
      this.startLevel();
      return ;

    }

     else if (headsUpDisplayMessage != null && hero.getXVelocity() != 0) {
      headsUpDisplayMessage = null;
    }
  }

  @Override
  public void tick() {
    // Don't update anything once we've completed the game
    if (gameFinished) {
      return;
    }
    updateEntities();
    updateState();

    // Make the level tick if it has anything to do
    this.currentLevel.tick();
  }

  @Override
  public boolean isGameOver() {
    return gameFinished;
  }

  @Override
  public void setHeadsUpDisplayMessage(String message) {
    this.headsUpDisplayMessage = message;

  }

  @Override
  public boolean needsRefresh() {
    return needsRefresh;
  }

  @Override
  public void clean() {
    needsRefresh = false;
  }

  public String getHeadsUpDisplayMessage() {
    return headsUpDisplayMessage;
  }

  @Override
  public long getScore(){
    if(!gameFinished){
      scoreKeeper.updateCurrentScore(this.getTimeSinceStart());
      return scoreKeeper.getCurrentScore();
  }

    else {
      return 0 ;
    }

  }


  private void advanceLevel(){
    String s = config.getNextLevel();
    try {
      config = new JsonConfigParser(config.getNextLevel());
      this.startLevel();

    } catch (IOException e) {
      System.err.println("IO error when attempting to read the next level's configuration\nPlease check if file path is correct\n" + s);
      e.printStackTrace();
      System.exit(1);
    } catch (JSONException e) {
      System.err.println(
              "Next Level's configuration is not well formed, please refer to example.json for an "
                      + "example of a well formed configuration");
      e.printStackTrace();
      System.exit(2);
    }

  }

  @Override
  public ScoreKeeper getScoreKeeper() {
    return scoreKeeper;
  }


  @Override
  public void saveGame() {
    stateController.saveGame(levelTracker);
  }

  @Override
  public void setCurrentLevel(Level current){
    currentLevel = current ;
  }

  @Override
  public void setScoreKeeper(ScoreKeeper sc) {
    this.scoreKeeper = sc ;
  }

  @Override
  public void setStartTime(long time) {
    startTime = time ;
  }

  @Override
  public void setLevelTracker(int number) {
    levelTracker = number;
  }

  @Override
  public void restoreGame() {
    stateController.restoreGame();

  }

  @Override
  public String getSaveMessage(){
    return stateController.getSaveMessage();
  }

  @Override
  public ConfigParser getConfig() {
    return config;
  }

  @Override
  public void setConfig(ConfigParser configuration) {
    config = configuration;
  }

}
