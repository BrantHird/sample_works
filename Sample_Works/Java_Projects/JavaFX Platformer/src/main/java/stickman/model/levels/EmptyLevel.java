package stickman.model.levels;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import stickman.model.config.ConfigParser;
import stickman.model.config.LevelConfig;
import stickman.model.config.Position;
import stickman.model.entities.Entity;
import stickman.model.entities.MovableEntity;
import stickman.model.entities.character.Hero;
import stickman.model.entities.character.movementStrategies.MovementStrategy;
import stickman.model.entities.platforms.LogPlatform;

/** An empty level, used as the basis for all other levels */
public class EmptyLevel implements Level {

  private double floorHeight;
  private double width;
  private double height;
  private Hero hero = null;
  private List<Entity> staticEntities = new ArrayList<Entity>();
  private List<MovableEntity> dynamicEntities = new ArrayList<MovableEntity>();
  private long targetTime ;
  public String name ;

  EmptyLevel(ConfigParser configParser) {
    LevelConfig level = configParser.getLevel();
    this.floorHeight = level.getFloorHeight();
    this.height = level.getHeight();
    this.width = level.getWidth();
    this.name = "Empty Level";
  }

  public Hero getHero() {
    return hero;
  }

  public void setHero(Hero newHero){
    hero = newHero;
  }

  @Override
  public void setMovableEntities(List<MovableEntity> moveables) {
    this.dynamicEntities = moveables ;
  }

  public void setStaticEntities(List<Entity> newEntities){
    staticEntities = newEntities;
  }

  @Override
  public List<Entity> getEntities() {
    // There are no static entities yet, but later there might be!
    return Stream.concat(staticEntities.stream(), dynamicEntities.stream())
        .collect(Collectors.toList());
  }

  @Override
  public List<Entity> getStaticEntities() {
    return staticEntities;
  }

  @Override
  public List<MovableEntity> getDynamicEntities() {
    return dynamicEntities;
  }

  @Override
  public double getHeight() {
    return height;
  }

  @Override
  public double getWidth() {
    return width;
  }

  @Override
  public void tick() {

    // Ensure nothing falls into the ground with an ad-hoc floor entity
    Position<Double> floorPosition = new Position<Double>(0.0, getFloorHeight());
    LogPlatform floor = new LogPlatform(floorPosition);
    for (MovableEntity a : dynamicEntities) {
      if (a.getYPos() + a.getHeight() > getFloorHeight()) {
        a.feedbackOnTop(floor);
      }
    }
  }

  @Override
  public double getFloorHeight() {
    return floorHeight;
  }

  @Override
  public void addStaticEntity(Entity entity) {
    this.staticEntities.add(entity);
  }

  @Override
  public void addDynamicEntity(MovableEntity entity) {
    this.dynamicEntities.add(entity);
  }

  @Override
  public void addHero(Hero hero) {
    if (this.hero != null) {
      throw new Error("Hero already set for the level");
    }
    this.hero = hero;
    this.dynamicEntities.add(hero);
  }

  @Override
  public long getTargetTime() {
    return this.targetTime;
  }

  @Override
  public void setTargetTime(long idealTime) {
    this.targetTime = idealTime;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public void setName(String newName) {
    this.name = newName ;
  }

  @Override
  public Level makeCopy() {

    Level levelObject = null ;

    try{
      levelObject = (Level) super.clone();
      Hero heroObject = hero.makeCopy();
      List<Entity> statics = new ArrayList<>();
      List<MovableEntity> movables = new ArrayList<>();

      for(Entity sE : this.staticEntities){
        statics.add(sE.makeCopy());
      }

      for(MovableEntity mE : this.dynamicEntities){
        movables.add(mE.makeCopy());
      }

      levelObject.setMovableEntities(movables);
      levelObject.setStaticEntities(statics);


      MovableEntity delete = null;

      for(MovableEntity e : levelObject.getDynamicEntities()){
        if(e.getClass().equals(Hero.class)){
          delete = e ;
        }
      }

      levelObject.getDynamicEntities().remove(delete);
      levelObject.setHero(heroObject);
      levelObject.addDynamicEntity(heroObject);

    }
    catch(CloneNotSupportedException e){
      e.printStackTrace();
    }

    return levelObject;
  }


}
