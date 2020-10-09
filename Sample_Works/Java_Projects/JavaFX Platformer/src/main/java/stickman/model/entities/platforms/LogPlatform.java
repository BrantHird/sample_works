package stickman.model.entities.platforms;

import stickman.model.config.Position;
import stickman.model.entities.StaticEntity;

public class LogPlatform extends StaticEntity {
  private static final String LOG_PATH = "/log.png";

  public LogPlatform(Position<Double> position) {
    super(position, LOG_PATH, Layer.FOREGROUND, 9, 76);
  }
}
