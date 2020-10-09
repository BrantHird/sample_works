package stickman.model.levels;

import stickman.model.config.ConfigParser;
import stickman.model.config.EntityConfig;
import stickman.model.config.EntityConfig.SlimeMovement;
import stickman.model.config.LevelConfig;
import stickman.model.config.Position;
import stickman.model.entities.background.CloudEntity;
import stickman.model.entities.character.Hero;
import stickman.model.entities.character.Slime;
import stickman.model.entities.character.movementStrategies.GuardSpot;
import stickman.model.entities.character.movementStrategies.MovementStrategy;
import stickman.model.entities.character.movementStrategies.RandomMovement;
import stickman.model.entities.character.movementStrategies.Stay;
import stickman.model.entities.platforms.Goal;
import stickman.model.entities.platforms.LogPlatform;

public class LevelBuilder extends AbstractLevelBuilder {
  private Level level;

  public LevelBuilder(ConfigParser cp) {
    level = new EmptyLevel(cp);
  }

  /**
   * Build a level from the config
   *
   * @param configParser the configuration that has been selected
   * @return A LevelBuilder matching the configuration
   */
  public static LevelBuilder fromConfig(ConfigParser configParser) {
    LevelBuilder levelBuilder = new LevelBuilder(configParser);
    LevelConfig levelConfig = configParser.getLevel();

    // Set up the entities
    for (EntityConfig ec : levelConfig.getEntities()) {
      switch (ec.getEntityType()) {
        case CLOUD:
          levelBuilder.addCloud(ec.getPosition(), configParser.getCloudVelocity());
          break;
        case SLIME:
          levelBuilder.addSlime(ec.getPosition(), ec.getSlimeMovement());
          break;
        case STATIC_PLATFORM:
          levelBuilder.addStaticPlatform(ec.getPosition());
          break;
        case GOAL:
          levelBuilder.addGoal(ec.getPosition());
          break;
        default:
          // This should be impossible
          throw new IllegalStateException("Unexpected value: " + ec.getEntityType().name());
      }
    }

    // Add the hero to the level
    levelBuilder.addHero(
            levelConfig.getStickmanPos(), levelConfig.getFloorHeight(), configParser.getStickmanSize());

    try{
      //Set the target level time
      if(configParser.getTargetTime() > 0){
        levelBuilder.setTargetTime(configParser.getTargetTime());
      }

      else throw new IllegalStateException("Invalid target time in level config! Please set ideal time to be > 0");
    }

    catch(IllegalStateException e){e.printStackTrace(); System.exit(1);}

    levelBuilder.setName(configParser.getName());

    return levelBuilder;
  }

  /**
   * Build the level
   *
   * @return the level you've been building
   * @throws IllegalStateException level is not ready to be built
   */
  @Override
  public Level build() throws IllegalStateException {
    if (!isReady) {
      throw new IllegalStateException("Level is not in ready ready state");
    }
    return level;
  }

  /**
   * Reset the level construction
   */
  @Override
  public void reset() {
    super.isReady = false;
  }

  /**
   * Sets the hero to a particular position in the level
   *
   * @param position position of the hero
   */
  @Override
  public void addHero(Position<Double> position, double floorHeight, Hero.Size size) {
    super.isReady = true;
    Hero hero = new Hero(position.getX(), floorHeight, size);
    level.addHero(hero);
  }

  /**
   * Adds a new cloud to the level
   *
   * @param position      starting position of the cloud
   * @param cloudVelocity x velocity of the cloud
   */
  @Override
  public void addCloud(Position<Double> position, double cloudVelocity) {
    level.addDynamicEntity(new CloudEntity(position.getX(), position.getY(), cloudVelocity));
  }

  /**
   * Adds a new slime to the level
   *
   * @param position starting position of the slime
   */
  @Override
  public void addSlime(Position<Double> position, SlimeMovement slimeMovement) {
    MovementStrategy strategy;

    switch (slimeMovement) {
      default:
      case STAY:
        strategy = new Stay();
        break;
      case RANDOM:
        strategy = new RandomMovement();
        break;
      case GUARD:
        strategy = new GuardSpot();
        break;
    }

    level.addDynamicEntity(new Slime(position.getX(), position.getY(), strategy));
  }

  /**
   * Adds a new static platform to the level (a log)
   *
   * @param position position of the platform
   */
  @Override
  public void addStaticPlatform(Position<Double> position) {
    level.addStaticEntity(new LogPlatform(position));
  }

  /**
   * Adds a new goal/finish line to the level
   *
   * @param position position of the goal
   */
  @Override
  public void addGoal(Position<Double> position) {
    level.addStaticEntity(new Goal(position));
  }

  @Override
  public void setTargetTime(long targetTime) {
    level.setTargetTime(targetTime);
  }

  @Override
  public void setName(String levelName) {
    level.setName(levelName);
  }
}


