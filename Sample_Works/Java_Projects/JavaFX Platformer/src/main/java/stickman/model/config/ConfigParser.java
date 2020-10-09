package stickman.model.config;

import java.util.Map;
import stickman.model.entities.character.Hero;

/**
 * The java representation of the configuration, no matter the format it comes in at the moment only
 * JSON is supported, but in the future YAML or JSON5 may be supported and we want to have a
 * consistent interface to get this information out
 *
 * <p>As the configuration gets more complex, this abstract class (and hence those that wish to
 * inherit from it) will need to get more complex too.
 */

public abstract class ConfigParser implements Cloneable {
  static final Map<String, Hero.Size> sizeValidator =
      Map.of(
          "tiny", Hero.Size.TINY,
          "normal", Hero.Size.NORMAL,
          "large", Hero.Size.LARGE,
          "giant", Hero.Size.GIANT);
  Hero.Size stickmanSize;
  double cloudVelocity;
  LevelConfig level;
  String nextLevel = null;
  long targetTime = 0;
  String name = null ;

  public Hero.Size getStickmanSize() {
    return stickmanSize;
  }

  public double getCloudVelocity() {
    return cloudVelocity;
  }

  public LevelConfig getLevel() {
    return level;
  }

  public String getNextLevel(){return nextLevel; }

  public long getTargetTime(){return targetTime;}

  public String getName(){return name; }

  public void setNextLevel(String next){nextLevel = next;}

  public ConfigParser makeCopy(){
    ConfigParser configObject = null ;
    try{
      configObject = (ConfigParser) super.clone();
    }
    catch(CloneNotSupportedException e){
      e.printStackTrace();
    }

    return configObject;
  }

}
