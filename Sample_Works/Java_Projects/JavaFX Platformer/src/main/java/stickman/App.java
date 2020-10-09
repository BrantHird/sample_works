package stickman;

import java.io.IOException;
import java.util.Map;
import javafx.application.Application;
import javafx.stage.Stage;
import org.json.JSONException;
import stickman.model.GameEngine;
import stickman.model.GameEngineImpl;
import stickman.model.config.ConfigParser;
import stickman.model.config.JsonConfigParser;
import stickman.view.GameWindow;

public class App extends Application {
  private static ConfigParser config;

  public static void main(String[] args) {
    String configPath = "./LevelOne.json";


    if (args.length >= 1) {
      configPath = args[0];
      System.out.printf("Loading configuration from %s%n", configPath);
    }

    try {
      config = new JsonConfigParser(configPath);
    } catch (IOException e) {
      System.err.println("IO error when attempting to read configuration");
      e.printStackTrace();
      System.exit(1);
    } catch (JSONException e) {
      System.err.println(
          "Configuration is not well formed, please refer to example.json for an "
              + "example of a well formed configuration");
      e.printStackTrace();
      System.exit(2);
    }
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    Map<String, String> params = getParameters().getNamed();

    GameEngine model = new GameEngineImpl(config);
    model.startLevel();
    GameWindow window = new GameWindow(model, 640, 400);

    primaryStage.setTitle("Stickman");
    primaryStage.setScene(window.getScene());
    primaryStage.show();

    window.run();




  }
}
