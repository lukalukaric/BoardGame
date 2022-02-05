package com.mygdx.game.common;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.mygdx.game.CellState;
import com.mygdx.game.CellStateDif;
import com.mygdx.game.NonogramGame;

public class GameManager {
    public static final GameManager INSTANCE = new GameManager();

    private static final String INIT_MOVE_KEY = "initMove";
    private static final String INIT_DIFFICULTY_KEY = "initDif";

    private final Preferences PREFS;
    private CellState initMove = CellState.SET;
    private CellStateDif initDif = CellStateDif.EASY;

    private GameManager() {
        PREFS = Gdx.app.getPreferences(NonogramGame.class.getSimpleName());
        String moveName = PREFS.getString(INIT_MOVE_KEY, CellState.SET.name());
        initMove = CellState.valueOf(moveName);

        String difName = PREFS.getString(INIT_DIFFICULTY_KEY, CellStateDif.EASY.name());
        initDif = CellStateDif.valueOf(difName);
    }

    public CellState getInitMove() {
        return initMove;
    }

    public void setInitMove(CellState move) {
        initMove = move;

        PREFS.putString(INIT_MOVE_KEY, move.name());
        PREFS.flush();
    }
    public CellStateDif getInitDif() {
        return initDif;
    }

    public void setInitDif(CellStateDif move) {
        initDif = move;

        PREFS.putString(INIT_DIFFICULTY_KEY, move.name());
        PREFS.flush();
    }
}
