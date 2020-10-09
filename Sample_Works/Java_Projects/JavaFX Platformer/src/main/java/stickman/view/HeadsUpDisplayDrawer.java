package stickman.view;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import stickman.model.GameEngine;
import stickman.model.levels.Level;
import stickman.model.levels.ScoreKeeper;

import java.util.*;

public class HeadsUpDisplayDrawer implements ForegroundDrawer {
  private static final double X_PADDING = 50;
  private static final double Y_PADDING = 5;
  private Pane pane;
  private GameEngine model;
  private Label timeElapsed = new Label();
  private Label health = new Label();
  private Label message = new Label();
  private Label score = new Label();
  private Label previousTitle = new Label();
  private Label saveState = new Label();
  private ArrayList<Label> previousScores = new ArrayList<>();
  private ScoreKeeper scoreKeeper;


  @Override
  public void draw(GameEngine model, Pane pane) {
    this.model = model;
    this.pane = pane;

    update();

    timeElapsed.setFont(new Font("Monospaced Regular", 20));
    health.setFont(new Font("Monospaced Regular", 20));
    message.setFont(new Font("Monospaced Regular", 50));
    score.setFont(new Font("Monospaced Regular", 20));
    previousTitle.setFont(new Font("Monospaced Regular", 20));
    saveState.setFont(new Font("Monospaced Regular", 50));

    this.pane.getChildren().addAll(timeElapsed, health, message, score, previousTitle, saveState);

  }

  @Override
  public void update() {
    // Set the positions of the labels

    this.scoreKeeper = model.getScoreKeeper();

    timeElapsed.setLayoutX(X_PADDING / 2);
    timeElapsed.setLayoutY(Y_PADDING);

    health.setLayoutX(timeElapsed.getLayoutX() + X_PADDING + timeElapsed.getWidth());
    health.setLayoutY(Y_PADDING);
    score.setLayoutX(health.getLayoutX() + X_PADDING + health.getWidth());
    score.setLayoutY(Y_PADDING);

    message.setLayoutX(pane.getWidth() / 20);
    message.setLayoutY(pane.getHeight() / 4);


    if(model.getHeadsUpDisplayMessage() == null){
      saveState.setLayoutX(pane.getWidth() / 20);
      saveState.setLayoutY(pane.getHeight() / 4);
    }

    else{
      saveState.setLayoutX(pane.getWidth() / 20);
      saveState.setLayoutY(pane.getHeight() / 10);
    }

    previousTitle.setLayoutX(score.getLayoutX() + 150 + score.getWidth());
    previousTitle.setLayoutY(Y_PADDING);

    // Set the text
    timeElapsed.setText(String.format("TIME%n %03d", model.getTimeSinceStart() / 1000));
    health.setText(String.format("HEALTH%n   %03d", model.getHeroHealth()));
    score.setText(String.format("SCORE%n  %03d", model.getScore()));
    message.setText(model.getHeadsUpDisplayMessage());
    previousTitle.setText("TOTAL SCORE\n        " + (scoreKeeper.getScores()));
    saveState.setText(model.getSaveMessage());





  }
}
