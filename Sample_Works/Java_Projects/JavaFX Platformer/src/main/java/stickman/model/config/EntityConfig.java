package stickman.model.config;

import java.util.Map;

public class EntityConfig {

  private static final Map<String, EntityType> entityTypeValidator =
      Map.of(
          "cloud", EntityType.CLOUD,
          "slime", EntityType.SLIME,
          "staticplatform", EntityType.STATIC_PLATFORM,
          "goal", EntityType.GOAL);

  private static final Map<String, SlimeMovement> slimeMovementValidator =
      Map.of(
          "", SlimeMovement.STAY,
          "stay", SlimeMovement.STAY,
          "random", SlimeMovement.RANDOM,
          "guard", SlimeMovement.GUARD);

  private EntityType entityType;
  private SlimeMovement slimeMovement;
  private Position<Double> position;

  EntityConfig(String type, String movement, double x, double y) {
    this.entityType = validateEntityType(type);
    this.slimeMovement = validateSlimeMovement(movement);
    this.position = new Position<Double>(x, y);
  }

  public EntityType getEntityType() {
    return entityType;
  }

  public Position<Double> getPosition() {
    return position;
  }

  public SlimeMovement getSlimeMovement() {
    return slimeMovement;
  }

  private EntityType validateEntityType(String type) throws TypeNotPresentException {
    if (entityTypeValidator.containsKey(type.toLowerCase())) {
      return entityTypeValidator.get(type.toLowerCase());
    }

    throw new TypeNotPresentException(type, new Exception("Configuration entity type invalid"));
  }

  private SlimeMovement validateSlimeMovement(String type) throws TypeNotPresentException {
    if (slimeMovementValidator.containsKey(type.toLowerCase())) {
      return slimeMovementValidator.get(type.toLowerCase());
    }

    throw new TypeNotPresentException(type, new Exception("Configuration slime movement invalid"));
  }

  public enum EntityType {
    CLOUD,
    SLIME,
    STATIC_PLATFORM,
    GOAL,
  }

  public enum SlimeMovement {
    STAY,
    RANDOM,
    GUARD
  }
}
