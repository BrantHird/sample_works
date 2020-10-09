package stickman.model.entities;

public abstract class Entity implements Cloneable{
  private String imagePath;
  private boolean deleted;
  private boolean heroKill ;

  public abstract double getXPos();

  public abstract double getYPos();

  public abstract double getHeight();

  public abstract double getWidth();

  public abstract Layer getLayer();

  /**
   * Call this method after colliding with an entity
   *
   * @param e the entity we're colliding with
   */
  public void handleCollision(MovableEntity e) {
    if (e.getYVelocity() > 0) {
      collisionUnder(e);
    } else if (e.getYVelocity() < 0) {
      collisionOnTop(e);
    }

    if (e.getXVelocity() > 0) {
      collisionLeft(e);
    } else if (e.getXVelocity() < 0) {
      collisionRight(e);
    }
  }

  /**
   * Handle collision when the entity is under something
   *
   * @param e the entity that we're under
   */
  public void collisionUnder(MovableEntity e) {
    e.feedbackOnTop(this);
  }

  /**
   * Handle collision when the entity is on top of something
   *
   * @param e the entity that we're on top of
   */
  public void collisionOnTop(MovableEntity e) {
    e.feedbackUnder(this);
  }

  /**
   * Handle collision when the entity is on the left of something
   *
   * @param e the entity that we're left of
   */
  public void collisionLeft(MovableEntity e) {
    e.feedbackRightOf(this);
  }

  /**
   * Handle collision when the entity is on the right of something
   *
   * @param e the entity that we're right of
   */
  public void collisionRight(MovableEntity e) {
    e.feedbackLeftOf(this);
  }

  public String getImagePath() {
    return this.imagePath;
  }

  public void setImagePath(String imagePath) {
    this.imagePath = getClass().getResource(imagePath).toExternalForm();
  }

  public boolean isDeleted() {
    return deleted;
  }

  public boolean killedByHero() {return heroKill; }

  public void setHeroKill(){heroKill = true ;}

  /** Mark the entity as deleted when it no longer needs to be rendered */
  public void delete() {
    deleted = true;
  }

  /**
   * Check if the current entity overlaps with entity a
   *
   * @param e another entity, 'e'
   * @return true if the bounding boxes of the two entities overlap
   */
  public boolean overlap(Entity e) {
    return (this.getXPos() < (e.getXPos() + e.getWidth()))
        && ((this.getXPos() + this.getWidth()) > e.getXPos())
        && (this.getYPos() < (e.getYPos() + e.getHeight()))
        && ((this.getYPos() + this.getHeight()) > e.getYPos());
  }

  /**
   * Check if two entities are overlapping and in the same layer
   *
   * @param e another entity, 'e'
   * @return true if the bounding boxes are overlapping and they're on the same layer
   */
  public boolean overlappingSameLayer(Entity e) {
    return this.getLayer() == e.getLayer() && overlap(e);
  }

  /**
   * amount of damage that this entity can inflict
   *
   * <p>The default is 0, but some entities may
   *
   * @return the amount of damage that this entity inflicts on other entities
   */
  public int damageInflicted() {
    return 0;
  }

  public abstract Entity makeCopy();

  public enum Layer {
    BACKGROUND,
    FOREGROUND,
    EFFECT
  }
}
