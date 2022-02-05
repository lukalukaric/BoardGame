package com.mygdx.game.screen;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.NonogramGame;
import com.mygdx.game.assets.AssetDescriptors;
import com.mygdx.game.assets.RegionNames;
import com.mygdx.game.config.GameConfig;

public class IntroScreen extends ScreenAdapter {
    public static final float INTRO_DURATION_IN_SEC = 2.5f;   // duration of the (intro) animation

    private final NonogramGame game;
    private final AssetManager assetManager;

    private Viewport viewport;
    private TextureAtlas gameplayAtlas;

    private float duration = 0f;

    private Stage stage;

    public IntroScreen(NonogramGame game) {
        this.game = game;
        assetManager = game.getAssetManager();
    }

    @Override
    public void show() {
        viewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);
        stage = new Stage(viewport, game.getBatch());

        // load assets
        assetManager.load(AssetDescriptors.UI_FONT);
        assetManager.load(AssetDescriptors.UI_SKIN);
        assetManager.load(AssetDescriptors.GAMEPLAY);
        assetManager.finishLoading();   // blocks until all assets are loaded

        gameplayAtlas = assetManager.get(AssetDescriptors.GAMEPLAY);
        stage.addActor(createBackground());
        stage.addActor(createSmiley());
        stage.addActor(createThinkingCloud());
        stage.addActor(createAnimation());
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(65 / 255f, 159 / 255f, 221 / 255f, 0f);

        duration += delta;

        // go to the MenuScreen after INTRO_DURATION_IN_SEC seconds
        if (duration > INTRO_DURATION_IN_SEC) {
            game.setScreen(new MenuScreen(game));
        }

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

    private Actor createSmiley() {
        Image smiley = new Image(gameplayAtlas.findRegion(RegionNames.SMILEY_FACE));
        // position the image to the center of the window
        smiley.setSize(200,200);
        smiley.setPosition(0,viewport.getWorldHeight() / 2f - smiley.getHeight());
        float x = viewport.getWorldWidth() / 2f - smiley.getWidth() / 2f;
        float y = viewport.getWorldHeight() / 2f - smiley.getHeight() ;
        smiley.addAction(
                /* animationDuration = Actions.sequence + Actions.rotateBy + Actions.scaleTo
                                      = 1.5 + 1 + 0.5 = 3 sec */
                Actions.sequence(
                    Actions.moveTo(x, y, 1f)
                )
        );
        return smiley;
    }

    private Actor createBackground() {
        Image background = new Image(gameplayAtlas.findRegion(RegionNames.BACKGROUND_INTRO));
        // position the image to the center of the window
        background.setPosition(viewport.getWorldWidth() / 2f - background.getWidth() / 2f,
                viewport.getWorldHeight() / 2f - background.getHeight() / 2f);
        return background;
    }

    private Actor createThinkingCloud() {
        Image thinkingCloud = new Image(gameplayAtlas.findRegion(RegionNames.THINKING_CLOUD));
        thinkingCloud.setSize(200,200);
        thinkingCloud.setPosition(viewport.getWorldWidth() /2f,viewport.getWorldHeight());
        float x = viewport.getWorldWidth() / 2f ;
        float y = viewport.getWorldHeight() / 2f ;
        thinkingCloud.addAction(
                Actions.sequence(
                        Actions.moveTo(x, y, 1f)
                )
        );

        return thinkingCloud;
    }

    private Actor createAnimation() {
        Image lightBulb = new Image(gameplayAtlas.findRegion(RegionNames.LIGHT_BULB));
        lightBulb.setSize(100,100);
        lightBulb.setPosition(viewport.getWorldWidth() /2f + 50,viewport.getWorldHeight() + 70);
        float x = viewport.getWorldWidth() / 2f + 50 ;
        float y = viewport.getWorldHeight() / 2f + 70 ;
        lightBulb.addAction(
                Actions.sequence(
                        Actions.moveTo(x, y, 1f),
                        Actions.parallel(
                                Actions.scaleBy(2,2,0.5f),
                                Actions.moveTo(x-100,y-100,0.5f)
                        ),
                        Actions.parallel(
                                Actions.scaleBy(-2,-2,0.5f),
                                Actions.moveTo(x,y,0.5f)
                        ),
                        Actions.parallel(
                                Actions.scaleBy(20,20,0.5f),
                                Actions.moveTo(x-1200,y-1200,0.5f)
                        )
                )
        );
        return lightBulb;
    }
}
