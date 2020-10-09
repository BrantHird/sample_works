package stickman.view;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import stickman.model.GameEngine;
import stickman.model.entities.Entity;

public class GameWindow {
  private static final double VIEWPORT_MARGIN = 280.0;
  private final int width;
  private Scene scene;
  private Pane pane;
  private GameEngine model;
  private List<EntityView> entityViews;
  private BackgroundDrawer backgroundDrawer;
  private ForegroundDrawer foregroundDrawer;
  private double xViewportOffset = 0.0;

  public GameWindow(GameEngine model, int width, int height) {
    this.model = model;
    this.width = width;
    this.pane = new Pane();
    this.scene = new Scene(pane, width, height);

    this.entityViews = new ArrayList<>();

    KeyboardInputHandler keyboardInputHandler = new KeyboardInputHandler(model);

    scene.setOnKeyPressed(keyboardInputHandler::handlePressed);
    scene.setOnKeyReleased(keyboardInputHandler::handleReleased);

    this.backgroundDrawer = new ParallaxAutumnBackground();
    this.foregroundDrawer = new HeadsUpDisplayDrawer();
    backgroundDrawer.draw(model, pane);
    foregroundDrawer.draw(model, pane);
  }

  public Scene getScene() {
    return this.scene;
  }

  public void run() {
    Timeline timeline = new Timeline(new KeyFrame(Duration.millis(17), t -> this.draw()));

    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();

    initialiseEntityViews();
  }

  /** Get all the entity views from the levels entities */
  private void initialiseEntityViews() {
    // Initialise all the entities
    List<Entity> entities = model.getCurrentLevel().getEntities();
    for (Entity entity : entities) {
      EntityView entityView = new EntityViewImpl(entity);
      entityViews.add(entityView);
      pane.getChildren().add(entityView.getNode());
    }
  }

  /** Update the viewport offset according to the hero's x position */
  private void followHero() {
    double heroXPos = model.getCurrentLevel().getHero().getXPos();
    heroXPos -= xViewportOffset;

    if (heroXPos < VIEWPORT_MARGIN) {
      if (xViewportOffset >= 0) { // Don't go further left than the start of the level
        xViewportOffset -= VIEWPORT_MARGIN - heroXPos;
        if (xViewportOffset < 0) {
          xViewportOffset = 0;
        }
      }
    } else if (heroXPos > width - VIEWPORT_MARGIN) {
      xViewportOffset += heroXPos - (width - VIEWPORT_MARGIN);
    }
    backgroundDrawer.update(xViewportOffset);
  }

  /** If an entity is marked as deleted, remove it */
  private void removeDeletedEntityViews() {
    for (EntityView entityView : entityViews) {
      if (entityView.isMarkedForDelete()) {
        pane.getChildren().remove(entityView.getNode());
      }
    }
    entityViews.removeIf(EntityView::isMarkedForDelete);
  }

  private void draw() {
    if (model.needsRefresh()) {
      initialiseEntityViews();
      model.clean();
    }
    model.tick();

    followHero();
    foregroundDrawer.update();

    if (!model.needsRefresh()) {
      List<Entity> entities = model.getCurrentLevel().getEntities();
      for (EntityView view : entityViews) {
        view.update(xViewportOffset);
      }
    }

    removeDeletedEntityViews();
  }
}
