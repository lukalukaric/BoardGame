package com.mygdx.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.CellActor;
import com.mygdx.game.CellState;
import com.mygdx.game.CellStateDif;
import com.mygdx.game.NonogramGame;
import com.mygdx.game.assets.AssetDescriptors;
import com.mygdx.game.assets.RegionNames;
import com.mygdx.game.common.GameManager;
import com.mygdx.game.config.GameConfig;

public class GameScreen extends ScreenAdapter {
    private static final Logger log = new Logger(GameScreen.class.getSimpleName(), Logger.DEBUG);

    private final NonogramGame game;
    private final AssetManager assetManager;

    private Viewport viewport;
    private Viewport hudViewport;

    private Stage gameplayStage;
    private Stage hudStage;

    private Skin skin;
    private TextureAtlas gameplayAtlas;

    private CellState move = GameManager.INSTANCE.getInitMove();
    private CellStateDif dif = GameManager.INSTANCE.getInitDif();
    private Image infoImage;
    public int[][] array;
    public int[][] arrayEasy;


    public GameScreen(NonogramGame game) {
        this.game = game;
        assetManager = game.getAssetManager();
        initArray();
    }

    @Override
    public void show() {
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);
        hudViewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);

        gameplayStage = new Stage(viewport, game.getBatch());
        hudStage = new Stage(hudViewport, game.getBatch());

        skin = assetManager.get(AssetDescriptors.UI_SKIN);
        gameplayAtlas = assetManager.get(AssetDescriptors.GAMEPLAY);

        gameplayStage.addActor(createBackground());
        if(dif == CellStateDif.EASY)
            gameplayStage.addActor(createGrid(5, 5, 3));
        else
            gameplayStage.addActor(createGrid(11, 8, 3));


        hudStage.addActor(createInfo());
        hudStage.addActor(createBackButton());
        hudStage.addActor(createDif());
        hudStage.addActor(createNumbersTop());
        hudStage.addActor(createNumbersLeft());
        Gdx.input.setInputProcessor(new InputMultiplexer(gameplayStage, hudStage));


    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        hudViewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(195 / 255f, 195 / 255f, 195 / 255f, 0f);

        // update
        gameplayStage.act(delta);
        hudStage.act(delta);

        // draw
        gameplayStage.draw();
        hudStage.draw();
        if(gameOver())
            game.setScreen(new WinnerScreen(game));

    }

    private boolean gameOver() {
        int[][] end= new int[][]{
                {0,0,0,0,0,0,0,0},
                {0,1,1,1,1,0,0,0},
                {0,1,1,1,1,1,1,0},
                {0,1,1,0,0,1,1,0},
                {0,1,1,0,0,1,1,0},
                {0,1,1,1,1,1,1,0},
                {0,1,1,1,1,0,0,0},
                {0,1,1,0,0,0,0,0},
                {0,1,1,0,0,0,0,0},
                {0,1,1,0,0,0,0,0},
                {0,0,0,0,0,0,0,0},
        };
        int[][] endEasy= new int[][]{
                {1,0,0,0,0},
                {1,1,0,0,0},
                {1,1,1,0,0},
                {1,1,1,1,0},
                {1,1,1,1,1},
        };
        if(dif == CellStateDif.EASY){
            for (int i=0;i<5;i++){
                for (int j =0;j<5;j++){
                    if (arrayEasy[i][j] != endEasy[i][j])
                        return false;
                }
            }
        }
        else {
            for (int i=0;i<11;i++){
                for (int j =0;j<8;j++){
                    if (array[i][j] != end[i][j])
                        return false;
                }
            }
        }

        return true;
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        gameplayStage.dispose();
        hudStage.dispose();
    }
    private void initArray(){
        arrayEasy = new int[5][5];
        for (int i=0;i<5;i++){
            for (int j =0;j<5;j++){
                arrayEasy[i][j] = 0;
            }
        }
        array = new int[11][8];
        for (int i=0;i<11;i++){
            for (int j =0;j<8;j++){
                array[i][j] = 0;
            }
        }
    }
    private Actor createDif(){
        final Label label;
        if(dif == CellStateDif.EASY)
            label= new Label("EASY mode", skin);
        else
            label= new Label("HARD mode!", skin);
        label.setWidth(100);
        label.setPosition(20, GameConfig.HUD_HEIGHT - 50);
        return label;
    }
    private Actor createBackground() {
        Image background = new Image(gameplayAtlas.findRegion(RegionNames.BACKGROUND));
        background.setSize(GameConfig.WORLD_WIDTH,GameConfig.WORLD_HEIGHT);
        // position the image to the center of the window
        background.setPosition(viewport.getWorldWidth() / 2f - background.getWidth() / 2f,
                viewport.getWorldHeight() / 2f - background.getHeight() / 2f);
        return background;
    }

    private Actor createNumbersTop(){
        Table table = new Table();
        table.setDebug(false);
        if(dif == CellStateDif.EASY){
            table.add(new Label("5   ",skin));
            table.add(new Label("   4   ",skin));
            table.add(new Label("   3   ",skin));
            table.add(new Label("   2   ",skin));
            table.add(new Label("   1   ",skin));
            table.setPosition(0, 90);
        }
        else{
            table.add(new Label("      ", skin));
            table.add(new Label("      ", skin));
            table.add(new Label("      ", skin));
            table.add(new Label("   2  ", skin));
            table.add(new Label("   2  ", skin));
            table.add(new Label("      ", skin));
            table.add(new Label("      ", skin));
            table.add(new Label("      ", skin));
            table.row();
            table.add(new Label("   0  ", skin));
            table.add(new Label("   9  ", skin));
            table.add(new Label("   9  ", skin));
            table.add(new Label("   2  ", skin));
            table.add(new Label("   2  ", skin));
            table.add(new Label("   4  ", skin));
            table.add(new Label("   4  ", skin));
            table.add(new Label("   0  ", skin));
            table.setPosition(0, 185);
        }
        table.setFillParent(true);
        table.pack();
        return table;
    }

    private Actor createNumbersLeft(){
        Table table = new Table();
        table.setDebug(false);
        table.defaults().size(25,30);
        if(dif == CellStateDif.EASY){
            table.add(new Label("1",skin)).row();
            table.add(new Label("2",skin)).row();
            table.add(new Label("3",skin)).row();
            table.add(new Label("4",skin)).row();
            table.add(new Label("5",skin)).row();
            table.setPosition(-95, -5);
        }
        else {
            table.add(new Label(" ", skin));
            table.add(new Label("0", skin)).row();
            table.add(new Label(" ", skin));
            table.add(new Label("4", skin)).row();
            table.add(new Label(" ", skin));
            table.add(new Label("6", skin)).row();
            table.add(new Label("2", skin));
            table.add(new Label("2", skin)).row();
            table.add(new Label("2", skin));
            table.add(new Label("2", skin)).row();
            table.add(new Label("", skin));
            table.add(new Label("6", skin)).row();
            table.add(new Label("", skin));
            table.add(new Label("4", skin)).row();
            table.add(new Label("", skin));
            table.add(new Label("2", skin)).row();
            table.add(new Label("", skin));
            table.add(new Label("2", skin)).row();
            table.add(new Label("", skin));
            table.add(new Label("2", skin)).row();
            table.add(new Label("", skin));
            table.add(new Label("0", skin)).row();
            table.setPosition(-140, -10);
        }
        table.setFillParent(true);
        table.pack();
        return table;
    }

    private Actor createGrid(int rows, int columns, final float cellSize) {
        final Table table = new Table();
        table.setDebug(false);   // turn on all debug lines (table, cell, and widget)

        final Table grid = new Table();
        grid.defaults().size(cellSize);   // all cells will be the same size
        grid.setDebug(false);

        final TextureRegion emptyRegion = gameplayAtlas.findRegion(RegionNames.BELA);
        final TextureRegion xRegion = gameplayAtlas.findRegion(RegionNames.POLNO_POLJE);
        final TextureRegion oRegion = gameplayAtlas.findRegion(RegionNames.PREKRIZANO_POLJE);
        final TextureRegion prvaVrsta = gameplayAtlas.findRegion(RegionNames.BELABREZ);

        if (move == CellState.SET) {
            infoImage = new Image(xRegion);
        } else if (move == CellState.X) {
            infoImage = new Image(oRegion);
        }

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                CellActor cell = new CellActor(prvaVrsta);
                cell = new CellActor(emptyRegion);
                final int finalRow = row;
                final int finalColumn = column;
                cell.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                final CellActor clickedCell = (CellActor) event.getTarget(); // it will be an image for sure :-)

                switch (move) {
                    case SET:
                        clickedCell.setState(move);
                        clickedCell.setDrawable(xRegion);
                        infoImage.setDrawable(new TextureRegionDrawable(oRegion));
                        if(dif == CellStateDif.HARD)
                            array[finalRow][finalColumn]=1;
                        else
                            arrayEasy[finalRow][finalColumn]=1;
                        break;
                    case X:
                        clickedCell.setState(move);
                        clickedCell.setDrawable(oRegion);
                        infoImage.setDrawable(new TextureRegionDrawable(xRegion));
                        break;
                    case EMPTY:
                        clickedCell.setState(move);
                        clickedCell.setDrawable(emptyRegion);
                        infoImage.setDrawable(new TextureRegionDrawable(emptyRegion));
                        if(dif == CellStateDif.HARD)
                            array[finalRow][finalColumn]=0;
                        else
                            arrayEasy[finalRow][finalColumn]=0;
                        if(dif == CellStateDif.HARD){
                            for (int i=0;i<11;i++){
                                for (int j =0;j<8;j++){
                                    log.debug(array[i][j] + " ");
                                }
                                log.debug("\n");
                            }
                        }
                        else {
                            for (int i=0;i<5;i++){
                                for (int j =0;j<5;j++){
                                    log.debug(arrayEasy[i][j] + " ");
                                }
                                log.debug("\n");
                            }
                        }

                        break;
                }

                log.debug("clicked");
                    }
                });
                grid.add(cell);

            }
            grid.row();
        }

        table.add(grid).row();
        table.center();
        table.setFillParent(true);
        table.pack();

        return table;
    }

    private Actor createBackButton() {
        final TextButton backButton = new TextButton("Back", skin);
        backButton.setWidth(100);
        backButton.setPosition(GameConfig.HUD_WIDTH / 2f - backButton.getWidth() / 2f, 20f);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });
        return backButton;
    }

    private Actor createInfo() {
        final TextureRegion xRegion = gameplayAtlas.findRegion(RegionNames.POLNO_POLJE);
        final TextureRegion oRegion = gameplayAtlas.findRegion(RegionNames.PREKRIZANO_POLJE);
        final TextureRegion emptyRegion = gameplayAtlas.findRegion(RegionNames.BELA);
        final Table table = new Table();
        table.add(new Label("Choose: ", skin));
        CellActor cell = new CellActor(xRegion);
        cell.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                final CellActor clickedCell = (CellActor) event.getTarget(); // it will be an image for sure :-)
                move = CellState.SET;
                log.debug("clicked");
            }
        });
        table.add(cell).size(50);

        CellActor cell2 = new CellActor(oRegion);
        cell2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                final CellActor clickedCell = (CellActor) event.getTarget(); // it will be an image for sure :-)
                move = CellState.X;
                log.debug("clicked");
            }
        });
        table.add(cell2).size(50);

        CellActor cell3 = new CellActor(emptyRegion);
        cell3.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                final CellActor clickedCell = (CellActor) event.getTarget(); // it will be an image for sure :-)
                move = CellState.EMPTY;
                log.debug("clicked");
            }
        });
        table.add(cell3).size(50);

        table.center();
        table.pack();
        table.setPosition(
                GameConfig.HUD_WIDTH / 2f - table.getWidth() / 2f,
                GameConfig.HUD_HEIGHT - table.getHeight() - 20f
        );
        return table;
    }
}
