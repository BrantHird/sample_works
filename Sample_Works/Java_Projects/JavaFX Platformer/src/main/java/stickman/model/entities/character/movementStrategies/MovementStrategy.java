package stickman.model.entities.character.movementStrategies;

import stickman.model.entities.character.AbstractCharacter;

public abstract class MovementStrategy {
  private final long RATE = 60;
  private long frame = 0;

  public void move(AbstractCharacter character) {
    if (frame % RATE == 0) {
      internalMove(character);
    }
    frame++;
  }

  abstract void internalMove(AbstractCharacter character);
}
