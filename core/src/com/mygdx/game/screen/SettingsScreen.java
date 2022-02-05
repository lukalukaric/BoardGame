package com.mygdx.game.screen;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.CellState;
import com.mygdx.game.CellStateDif;
import com.mygdx.game.NonogramGame;
import com.mygdx.game.assets.AssetDescriptors;
import com.mygdx.game.assets.RegionNames;
import com.mygdx.game.common.GameManager;
import com.mygdx.game.config.GameConfig;

public class SettingsScreen extends ScreenAdapter {
    private final NonogramGame game;
    private final AssetManager assetManager;

    private Viewport viewport;
    private Stage stage;

    private ButtonGroup<CheckBox> checkBoxGroup;
    private CheckBox checkBoxX;
    private CheckBox checkBoxO;
    private ButtonGroup<CheckBox> checkBoxGroupDif;
    private CheckBox checkBoxEasy;
    private CheckBox checkBoxHard;

    public SettingsScreen(NonogramGame game) {
        this.game = game;
        assetManager = game.getAssetManager();
    }

    @Override
    public void show() {
        viewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);
        stage = new Stage(viewport, game.getBatch());

        stage.addActor(createUi());
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 0f);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    private Actor createUi() {
        Table table = new Table();
        table.defaults().pad(20);

        Skin uiSkin = assetManager.get(AssetDescriptors.UI_SKIN);
        TextureAtlas gameplayAtlas = assetManager.get(AssetDescriptors.GAMEPLAY);

        TextureRegion backgroundRegion = gameplayAtlas.findRegion(RegionNames.BACKGROUND);
        table.setBackground(new TextureRegionDrawable(backgroundRegion));

        ChangeListener listener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CheckBox checked = checkBoxGroup.getChecked();
                if (checked == checkBoxX) {
                    GameManager.INSTANCE.setInitMove(CellState.SET);
                } else if (checked == checkBoxO) {
                    GameManager.INSTANCE.setInitMove(CellState.X);
                }
            }
        };

        ChangeListener listenerDif = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CheckBox checked = checkBoxGroupDif.getChecked();
                if (checked == checkBoxEasy) {
                    GameManager.INSTANCE.setInitDif(CellStateDif.EASY);
                } else if (checked == checkBoxHard) {
                    GameManager.INSTANCE.setInitDif(CellStateDif.HARD);
                }
            }
        };

        checkBoxX = new CheckBox(CellState.SET.name(), uiSkin);
        checkBoxO = new CheckBox(CellState.X.name(), uiSkin);

        checkBoxEasy = new CheckBox(CellStateDif.EASY.name(), uiSkin);
        checkBoxHard = new CheckBox(CellStateDif.HARD.name(), uiSkin);

        checkBoxEasy.addListener(listenerDif);
        checkBoxHard.addListener(listenerDif);

        checkBoxX.addListener(listener);
        checkBoxO.addListener(listener);

        checkBoxGroup = new ButtonGroup<>(checkBoxX, checkBoxO);
        checkBoxGroup.setChecked(GameManager.INSTANCE.getInitMove().name());

        checkBoxGroupDif = new ButtonGroup<>(checkBoxEasy, checkBoxHard);
        checkBoxGroupDif.setChecked(GameManager.INSTANCE.getInitDif().name());

        TextButton backButton = new TextButton("Back", uiSkin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        Table contentTable = new Table(uiSkin);

        TextureRegion menuBackground = gameplayAtlas.findRegion(RegionNames.FRAME);
        contentTable.setBackground(new TextureRegionDrawable(menuBackground));

        contentTable.add(new Label("Settings", uiSkin)).padBottom(50).colspan(2).row();
        contentTable.add(new Label("Choose first move", uiSkin)).colspan(2).row();
        contentTable.add(checkBoxX);
        contentTable.add(checkBoxO).row();
        contentTable.add(new Label("Choose difficulty:", uiSkin)).colspan(2).row();
        contentTable.add(checkBoxEasy);
        contentTable.add(checkBoxHard).row();
        contentTable.add(backButton).width(100).padTop(50).colspan(2);

        table.add(contentTable);
        table.center();
        table.setFillParent(true);
        table.pack();

        return table;
    }
}
