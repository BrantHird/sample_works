package stickman.model.entities.character.movementStrategies;

import stickman.model.entities.character.AbstractCharacter;

/** "Guard" a spot, alternate moving left and right over it */
public class GuardSpot extends MovementStrategy {
  private boolean left = true;

  @Override
  void internalMove(AbstractCharacter character) {
    if (left) {
      character.moveLeft();
    } else {
      character.moveRight();
    }
    left = !left;
  }
}
