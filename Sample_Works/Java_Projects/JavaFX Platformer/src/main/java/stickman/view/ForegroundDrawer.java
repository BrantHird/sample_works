package stickman.view;

import javafx.scene.layout.Pane;
import stickman.model.GameEngine;

public interface ForegroundDrawer {
  void draw(GameEngine model, Pane pane);

  void update();
}
