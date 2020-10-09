package stickman.model.entities;

/** An entity which can move by setting its x and y velocity */
public abstract class MovableEntity extends Entity {
  private double xPosition;
  private double yPosition;
  private double xVelocity;
  private double yVelocity;

  public MovableEntity(
      double xPosition, double yPosition, double xVelocity, double yVelocity, String imagePath) {
    this.xPosition = xPosition;
    this.yPosition = yPosition;
    this.xVelocity = xVelocity;
    this.yVelocity = yVelocity;
    super.setImagePath(imagePath);
  }

  public MovableEntity(double xPosition, double yPosition, String imagePath) {
    this(xPosition, yPosition, 0, 0, imagePath);
  }

  public void accelerate(double velXMod, double velYMod) {
    setXVelocity(getXVelocity() + velXMod);
    setYVelocity(getYVelocity() + velYMod);
  }

  public void moveTick() {
    // We must use the getters here
    xPosition += getXVelocity();
    yPosition += getYVelocity();
  }

  @Override
  public double getXPos() {
    return xPosition;
  }

  protected void setXPos(double x) {
    this.xPosition = x;
  }

  @Override
  public double getYPos() {
    return yPosition;
  }

  protected void setYPos(double y) {
    this.yPosition = y;
  }

  public double getXVelocity() {
    return xVelocity;
  }

  public void setXVelocity(double velX) {
    this.xVelocity = velX;
  }

  public double getYVelocity() {
    return yVelocity;
  }

  public void setYVelocity(double velY) {
    this.yVelocity = velY;
  }

  /**
   * Called when this is under another entity
   *
   * @param entity the entity 'this' is under
   */
  public void feedbackUnder(Entity entity) {
    // Do nothing by default
  }

  /**
   * Called when this is on top of another entity
   *
   * @param entity the entity 'this' is on top of
   */
  public void feedbackOnTop(Entity entity) {
    // Do nothing by default
  }

  /**
   * Called when this is to the left of another entity
   *
   * @param entity the entity 'this' is left of
   */
  public void feedbackLeftOf(Entity entity) {
    // Do nothing by default
  }

  /**
   * Called when this is to the right of another entity
   *
   * @param entity the entity 'this' is right of
   */
  public void feedbackRightOf(Entity entity) {
    // Do nothing by default
  }

  public MovableEntity makeCopy(){

    MovableEntity entityObject = null ;
    try{
      entityObject = (MovableEntity) super.clone();
    }
    catch(CloneNotSupportedException e){
      e.printStackTrace();
    }

    return entityObject;
  }

  public void touchedGoal() {
    // Do nothing
    // most entities don't trigger the game to win if the goal is touched.
    // The Hero is the only entity that does this in this game.
    // however someone could have a race mode multi-player, or have a item that needs to be carried
    // across, etc.
  }
}
