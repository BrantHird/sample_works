package stickman.model.levels;

import javafx.scene.effect.Bloom;
import stickman.model.entities.MovableEntity;

import java.util.*;

public class ScoreKeeper implements Cloneable {

    LinkedHashMap<String, Long> levels;
    private long currentScore;
    private Level currentLevel ;
    private long extraPoints;

    public ScoreKeeper(){
        levels = new LinkedHashMap<>();
        extraPoints = 0 ;
    }

    public void setCurrentLevel(Level thisLevel){
        currentLevel = thisLevel;
    }

    public long getCurrentScore(){

        return  currentScore;
    }

    public void addLevel(Level level, long score){
        if(!levels.containsKey(level.getName())){
            levels.put(level.getName(), score);
        }
    }

    public long getScores(){

        long totalScore = 0 ;

        Iterator it = levels.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            totalScore += (Long)pair.getValue();
        }




        return totalScore;



    }

    public void resetExtraPoints(){
        this.extraPoints = 0 ;
    }


    public void updateCurrentScore(long timeSinceStart){
        long targetTime = currentLevel.getTargetTime();
        long score = targetTime - timeSinceStart/1000;
        for(MovableEntity e : currentLevel.getDynamicEntities()){
            if(e.isDeleted() && e.killedByHero()){
                extraPoints += 100;
            }
        }
        score += extraPoints;

        if(score < 0 ){
            score = 0 ;
        }

        currentScore = score;
    }

    public ScoreKeeper makeCopy() {

        ScoreKeeper scoreKeeperObject = null ;
        LinkedHashMap<String, Long> levelsObject = new LinkedHashMap<>() ;
        try{
            scoreKeeperObject = (ScoreKeeper) super.clone();
            Iterator it = levels.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                levelsObject.put((String)pair.getKey(),(Long)pair.getValue());

            }
            scoreKeeperObject.setLevels(levelsObject);

        }
        catch(CloneNotSupportedException e){
            e.printStackTrace();
        }
        return scoreKeeperObject;
    }

    public LinkedHashMap<String, Long> getLevels(){
        return levels;
    }

    public void setLevels(LinkedHashMap<String,Long> newLevels){
        this.levels = newLevels;
    }













}
