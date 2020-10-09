package stickman.model.entities.character.movementStrategies;

import java.util.Random;
import stickman.model.entities.character.AbstractCharacter;

/** Causses the character to jump, move left or right or stop randomly */
public class RandomMovement extends MovementStrategy {
  private static Random random = new Random();

  void internalMove(AbstractCharacter character) {
    // Stop moving before making a movement
    character.stopMoving();
    // pick a random movement and execute it
    int index = random.nextInt(Movement.values().length);
    Movement move = Movement.values()[index];
    switch (move) {
      case JUMP:
        character.jump();
        break;
      case JUMP_LEFT:
        character.jump();
      case LEFT:
        character.moveLeft();
        break;
      case JUMP_RIGHT:
        character.jump();
      case RIGHT:
        character.moveRight();
        break;
      case STOP:
      default:
        // Do nothing
        break;
    }
  }

  private enum Movement {
    STOP,
    JUMP,
    JUMP_LEFT,
    JUMP_RIGHT,
    LEFT,
    RIGHT
  }
}
