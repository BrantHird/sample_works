package stickman.model.levels;

import stickman.model.config.EntityConfig.SlimeMovement;
import stickman.model.config.Position;
import stickman.model.entities.character.Hero;

/** An abstract class for the level builder class */
public abstract class AbstractLevelBuilder {

  /**
   * A level can only be ready if certain conditions are set, for example the hero has its position
   * set, or there are at most 10 entities.
   */
  boolean isReady;

  /**
   * Build the level
   *
   * @return the level you've been building
   * @throws IllegalStateException level is not ready to be built
   */
  public abstract Level build() throws IllegalStateException;

  /**
   * Reset the level construction
   */
  public abstract void reset();

  /**
   * Sets the hero to a particular position in the level
   *
   * @param position    position of the hero
   * @param floorHeight the height of the floor for the level
   * @param size        the size of the hero
   */
  public abstract void addHero(Position<Double> position, double floorHeight, Hero.Size size);

  /**
   * Adds a new cloud to the level
   *
   * @param position      starting position of the cloud
   * @param cloudVelocity x velocity of the cloud
   */
  public abstract void addCloud(Position<Double> position, double cloudVelocity);

  /**
   * Adds a new slime to the level
   *
   * @param position      starting position of the slime
   * @param slimeMovement The strategy use for the slime movement
   */
  public abstract void addSlime(Position<Double> position, SlimeMovement slimeMovement);

  /**
   * Adds a new static platform to the level
   *
   * @param position position of the platform
   */
  public abstract void addStaticPlatform(Position<Double> position);

  /**
   * Adds a new goal/finish line to the level
   *
   * @param position position of the goal
   */
  public abstract void addGoal(Position<Double> position);

  public abstract void setTargetTime(long targetTime);

  public abstract void setName(String name);
}
