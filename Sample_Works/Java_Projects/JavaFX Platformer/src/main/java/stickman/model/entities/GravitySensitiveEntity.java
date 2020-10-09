package stickman.model.entities;

/** A movable entity who is subject to gravity if it is airborne */
public abstract class GravitySensitiveEntity extends MovableEntity {
  private static final double GRAVITY = 0.098;
  private boolean airborne = true;

  public GravitySensitiveEntity(
      double xPosition, double yPosition, double xVelocity, double yVelocity, String imagePath) {
    super(xPosition, yPosition, xVelocity, yVelocity, imagePath);
  }

  public GravitySensitiveEntity(double xPosition, double yPosition, String imagePath) {
    super(xPosition, yPosition, imagePath);
  }

  protected boolean isAirborne() {
    return airborne;
  }

  @Override
  public void moveTick() {
    super.moveTick();
    super.accelerate(0, GRAVITY);
  }

  public void stopFalling() {
    if (super.getYVelocity() > 0) {
      super.setYVelocity(0);
    }
    airborne = false;
  }

  /**
   * Prevent the entity from falling through the floor
   *
   * @param floor the y co-ordinate of the top of the object the player is intersecting with
   */
  public void stopFalling(double floor) {
    stopFalling();
    double clippingDepth = this.getYPos() + this.getHeight() - floor;
    if (clippingDepth > 0) {
      super.setYPos(this.getYPos() - clippingDepth);
    }
  }

  @Override
  public void setYVelocity(double velY) {
    super.setYVelocity(velY);
    if (0 > velY) {
      airborne = true;
    }
  }

  @Override
  public void feedbackOnTop(Entity entity) {
    // If we're falling and we're at least half way through an entity, stop falling on it
    if (entity.getYPos() > this.getYPos() + this.getHeight() / 2) {
      stopFalling(entity.getYPos());
    }
  }

  public GravitySensitiveEntity makeCopy(){

    GravitySensitiveEntity entityObject = null ;
    try{
      entityObject = (GravitySensitiveEntity) super.clone();
    }
    catch(CloneNotSupportedException e){
      e.printStackTrace();
    }

    return entityObject;
  }




}
