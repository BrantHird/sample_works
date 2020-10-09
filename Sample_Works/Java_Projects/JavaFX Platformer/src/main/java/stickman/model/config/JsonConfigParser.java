package stickman.model.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import stickman.model.entities.character.Hero;
import stickman.model.entities.character.Hero.Size;

public class JsonConfigParser extends ConfigParser {

  /**
   * Parse the JSON configuration at a given path
   *
   * @param pathToJsonConfig the path to the json file
   * @throws IOException if the file does not exist
   * @throws JSONException if the json is not well formed
   */
  public JsonConfigParser(String pathToJsonConfig) throws IOException, JSONException {
    String nextLevel1 = null ;

    String rawJsonText = new String(Files.readAllBytes(Paths.get(pathToJsonConfig)));

    JSONObject json = new JSONObject(rawJsonText);
    JSONObject jsonLevel = json.getJSONObject("level");
    JSONArray jsonEntities = jsonLevel.getJSONArray("entities");

    Size size = validateStickmanSize(json.getString("stickmanSize"));
    double cloudVelocity = json.getDouble("cloudVelocity");
    double height = jsonLevel.getDouble("height");
    double width = jsonLevel.getDouble("width");
    double floorHeight = jsonLevel.getDouble("floorHeight");
    boolean finalLevel = json.getBoolean("finalLevel");
    long idealTime = json.getLong("idealTime");
    String levelName = json.getString("levelName");
    if(!finalLevel){
        nextLevel1 = json.getString("nextLevel");
    }
    // The y position cannot be read from the configuration file just yet as it may not exist
    // it isn't read from anywhere in the source code yet so its fine
    Double stickmanPosX = jsonLevel.getJSONObject("stickmanPos").getDouble("x");
    Double stickmanPosY = null;
    Position<Double> stickmanPos = new Position<Double>(stickmanPosX, stickmanPosY);
    LevelConfig level = new LevelConfig(height, width, floorHeight, stickmanPos);

    // We can't use the for each loop in java here because of the JsonArray types
    for (int i = 0; i < jsonEntities.length(); i++) {
      JSONObject entity = jsonEntities.getJSONObject(i);
      String name = entity.getString("name");
      String movement = entity.optString("movement");
      double x = entity.getJSONObject("position").getDouble("x");
      double y = entity.getJSONObject("position").getDouble("y");
      level.addEntity(name, movement, x, y);
    }

    super.cloudVelocity = cloudVelocity;
    super.stickmanSize = size;
    super.level = level;
    super.targetTime = idealTime;
    super.name = levelName;

    if(!finalLevel){
      super.nextLevel = nextLevel1;
    }
  }

  /**
   * Ensures that the size of the stickman is a valid size; Returns the related enum for the size
   * The function is case insensitive
   *
   * @param stickmanSize the string found in the configuration
   * @return an enum for the stickman size
   * @throws JSONException if the stickman size is invalid
   */
  private Hero.Size validateStickmanSize(String stickmanSize) throws JSONException {
    if (sizeValidator.containsKey(stickmanSize.toLowerCase())) {
      return sizeValidator.get(stickmanSize.toLowerCase());
    }

    throw new JSONException("Invalid stickman size");
  }


  public JsonConfigParser makeCopy(){
    JsonConfigParser configObject = null ;
    try{
      configObject = (JsonConfigParser) super.clone();
    }
    catch(CloneNotSupportedException e){
      e.printStackTrace();
    }

    return configObject;
  }

}
