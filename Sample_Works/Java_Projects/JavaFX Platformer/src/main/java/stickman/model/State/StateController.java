package stickman.model.State;

import stickman.model.GameEngine;
import stickman.model.config.ConfigParser;
import stickman.model.levels.Level;
import stickman.model.levels.ScoreKeeper;

public class StateController {
    GameEngine model ;
    SaveGame gameSaver;
    String saveMessage;


    public StateController(GameEngine game){
        this.model = game;
    }



    public void saveGame(int modelLevelTracker) {
        Level savedLevel = model.getCurrentLevel().makeCopy();
        ScoreKeeper savedScore = model.getScoreKeeper().makeCopy();
        ConfigParser saveConfig = model.getConfig().makeCopy();
        savedScore.setCurrentLevel(savedLevel);
        gameSaver = new SaveGame(savedLevel, savedScore, model.getTimeSinceStart(), saveConfig, modelLevelTracker);
        saveMessage = "QUICKSAVE";
    }



    public void restoreGame() {

        if(gameSaver == null){
            saveMessage = "NO SAVED GAMES";
            return ;
        }

        model.restartLevel();

        model.setCurrentLevel(gameSaver.getSavedGame());
        model.setScoreKeeper(gameSaver.getSavedScore());

        model.setStartTime(System.currentTimeMillis()  - gameSaver.getTimeSinceStart());
        model.setConfig(gameSaver.getLevelConfiguration());
        model.setLevelTracker(gameSaver.getLevelNumber());

        ScoreKeeper savedScoreKeeper = model.getScoreKeeper().makeCopy();
        Level savedLevel = model.getCurrentLevel().makeCopy();
        savedScoreKeeper.setCurrentLevel(savedLevel);

        gameSaver = new SaveGame(savedLevel, savedScoreKeeper, model.getTimeSinceStart(), model.getConfig(), gameSaver.getLevelNumber());

        saveMessage = "RESTORE" ;

    }


    public String getSaveMessage(){
        return saveMessage;
    }


    public void removeSaveMessage(){
        saveMessage = null;
    }







}
