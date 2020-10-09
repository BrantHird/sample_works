package stickman.model.State;

import stickman.model.GameEngine;
import stickman.model.config.ConfigParser;
import stickman.model.levels.Level;
import stickman.model.levels.ScoreKeeper;

public class SaveGame {

    private Level savedGame;
    private ScoreKeeper savedScore ;
    private long time;
    private ConfigParser configuration;
    private int levelTracker;

    public SaveGame(Level level, ScoreKeeper score, long timeSinceStart, ConfigParser levelConfig, int levelTrack){
        savedGame = level ;
        savedScore = score ;
        time = timeSinceStart;
        configuration = levelConfig;
        levelTracker = levelTrack;
    }

    public Level getSavedGame(){
        return savedGame;
    }

    public ScoreKeeper getSavedScore(){
        return savedScore;
    }

    public long getTimeSinceStart(){ return time;}

    public ConfigParser getLevelConfiguration(){return configuration;}

    public int getLevelNumber(){return levelTracker;}






}
