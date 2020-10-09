package stickman.view;

import javafx.scene.Node;

public interface EntityView {
  void update(double xViewportOffset);

  Node getNode();

  /**
   * Check if the underlying entity is deleted
   *
   * @return true if the entity is marked for deletion
   */
  boolean isMarkedForDelete();
}
