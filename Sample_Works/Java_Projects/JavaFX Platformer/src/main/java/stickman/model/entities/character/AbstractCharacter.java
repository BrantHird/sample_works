package stickman.model.entities.character;

import stickman.model.entities.GravitySensitiveEntity;

/**
 * This represents the stickman / hero / player. It's unclear if they're distinct at the moment The
 * player should be controlled by the keyboard
 */
public abstract class AbstractCharacter extends GravitySensitiveEntity {
  AbstractCharacter(double xPosition, double yPosition, String imagePath) {
    super(xPosition, yPosition, imagePath);
  }

  /** When the character is killed, call this method */
  public void die() {
    this.delete();
  }

  public boolean jump() {
    if (super.isAirborne()) {
      return false;
    }
    setYVelocity(-3.0);
    return true;
  }

  public boolean moveRight() {
    setXVelocity(3.0);
    return true;
  }

  public boolean moveLeft() {
    if (getXPos() <= 0) {
      // Don't go passed the edge of the screen!
      return false;
    }
    setXVelocity(-3.0);
    return true;
  }

  public boolean stopMoving() {
    setXVelocity(0.0);
    return true;
  }

  @Override
  public Layer getLayer() {
    return Layer.FOREGROUND;
  }

  @Override
  public void moveTick() {
    super.moveTick();
    // Don't go passed the edge of the screen
    if (getXPos() < 0) {
      setXVelocity(0);
    }
  }

  @Override
  public int damageInflicted() {
    return isDeleted() ? 0 : 1;
  }
}
