package stickman.model.levels;

import java.util.List;
import stickman.model.entities.Entity;
import stickman.model.entities.MovableEntity;
import stickman.model.entities.StaticEntity;
import stickman.model.entities.character.Hero;

public interface Level extends Cloneable {
  List<Entity> getEntities();

  List<Entity> getStaticEntities();

  List<MovableEntity> getDynamicEntities();

  double getHeight();

  double getWidth();

  void tick();

  double getFloorHeight();

  Hero getHero();

  void addStaticEntity(Entity entity);

  void addDynamicEntity(MovableEntity movableEntity);

  void addHero(Hero hero);

  long getTargetTime();

  void setTargetTime(long idealTime);

  String getName();

  void setName(String name);

  Level makeCopy();

  void setHero(Hero hero);

  void setMovableEntities(List<MovableEntity> moveables);

  void setStaticEntities(List<Entity> statics);



}

