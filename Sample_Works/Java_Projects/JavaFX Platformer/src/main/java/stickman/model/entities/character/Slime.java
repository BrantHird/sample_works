package stickman.model.entities.character;

import stickman.model.entities.MovableEntity;
import stickman.model.entities.character.movementStrategies.MovementStrategy;
import stickman.model.entities.utilities.MovableEntityAnimator;

public class Slime extends AbstractCharacter {
  private static final double RATIO = 1.4;
  private static final double HEIGHT = 20;

  private static String[] frames = new String[] {"/slimeBa.png", "/slimeBb.png"};
  private MovableEntityAnimator slimeAnimator = new MovableEntityAnimator(frames, this);
  private MovementStrategy strategy;

  public Slime(double xPosition, double yPosition, MovementStrategy strategy) {
    super(xPosition, yPosition, "/slimeBa.png");
    this.strategy = strategy;
  }

  @Override
  public double getHeight() {
    return HEIGHT;
  }

  @Override
  public double getWidth() {
    return getHeight() * RATIO;
  }

  @Override
  public Layer getLayer() {
    return Layer.FOREGROUND;
  }

  @Override
  public void moveTick() {
    super.moveTick();
    slimeAnimator.setNextFrame();
    strategy.move(this);
  }

  @Override
  public void collisionUnder(MovableEntity e) {
    if (e.getYPos() + e.getHeight() / 2 < this.getYPos()) {
      die();
    }
    super.collisionUnder(e);
  }
}
