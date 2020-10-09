package stickman.model.entities.background;

import stickman.model.entities.MovableEntity;

/** A cloud that will move across the background at a constant speed */
public class CloudEntity extends MovableEntity {

  public CloudEntity(double xPosition, double yPosition, double xVelocity) {
    super(xPosition, yPosition, xVelocity / 60.0, 0, "/cloud_1.png");
  }

  @Override
  public double getHeight() {
    return 25;
  }

  @Override
  public double getWidth() {
    return 100;
  }

  @Override
  public Layer getLayer() {
    return Layer.BACKGROUND;
  }

}
