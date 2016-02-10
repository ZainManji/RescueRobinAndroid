package com.rescuebird.game.android;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.ArrayList;
import java.util.Iterator;

public class MainMenuScreen implements Screen, InputProcessor {

    BirdRiseGame game;
    boolean down;
    boolean instructionsDown;
    float stateTime;
    boolean playGame;
    boolean learningScreen;
    TextureRegion currentFrame;
    Animation redBirdAnimation;
    TextureRegion[] redFlyFrames;
    long previousTime = TimeUtils.nanoTime();
    long currentTime;
    boolean startTimer = false;
    ArrayList<Cloud> clouds = new ArrayList<Cloud>();

    public MainMenuScreen(BirdRiseGame gameRef) {
        this.game = gameRef;

        //Create texture region from each bird colour animation sheet
        TextureRegion[][] redBird = TextureRegion.split(Assets.redBirdAnimationSheet,
                Assets.redBirdAnimationSheet.getWidth() / GameScreen.BIRD_ANIMATION_FRAME_COLS,
                Assets.redBirdAnimationSheet.getHeight() / GameScreen.BIRD_ANIMATION_FRAME_ROWS);

        //Create array that will hold all of the separate images in the red flying bird animation
        redFlyFrames = GameScreen.initializeTextureRegion(GameScreen.BIRD_ANIMATION_FRAME_ROWS, GameScreen.BIRD_ANIMATION_FRAME_COLS, redBird);
        redBirdAnimation = new Animation(0.025f, redFlyFrames);

        GameUtilities.spawnCloud(true, false, clouds);
    }

    @Override
    public void render(float delta) {

        if ((clouds.isEmpty()) || clouds.size() < 3) {
            GameUtilities.spawnCloud(false, false, clouds);
        }

        game.font.setColor(Color.BLACK);
        game.highscoreWidth = (int)(game.font.getBounds(Long.toString(game.highscore)).width);


        // Move the clouds.
        Iterator<Cloud> cloudIter = clouds.iterator();
        while (cloudIter.hasNext()) {
            Cloud fallingCloud = cloudIter.next();
            fallingCloud.y -= fallingCloud.dropSpeed * Gdx.graphics.getDeltaTime();

            // Remove cloud if below the screen.
            if (fallingCloud.y + fallingCloud.height < 0) {
                cloudIter.remove();
            }
        }

        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (startTimer) {
            currentTime = TimeUtils.nanoTime();
            int time = (int) ((currentTime - previousTime) / 100000000);

            if (time >= 1) {
                startTimer = false;

                if (playGame) {
                    playGame = false;
                    game.adsController.hideBannerAd();
                    game.setScreen(game.gameScreen);
                }

                if (learningScreen) {
                    learningScreen = false;
                    game.learningScreen = new LearningScreen(game);
                    game.adsController.hideBannerAd();
                    game.setScreen(game.learningScreen);
                }
            }
        }

        drawObjects();
    }

    public void drawObjects() {
        game.batch.begin();
        game.batch.draw(Assets.blueBackground, 0, 0, GameUtilities.X_MAX, GameUtilities.Y_MAX, 0, 0, 768, 1024, false, false);

        for (Cloud fallingCloud: clouds) {
            game.batch.draw(Assets.cloud, fallingCloud.x, fallingCloud.y, fallingCloud.width, fallingCloud.height, 0, 0, 449, 319, false, false);
        }

        if (!down && playGame) {
            game.batch.draw(Assets.playButton, GameUtilities.CENTER_X - (GameUtilities.X_MAX / 7), GameUtilities.CENTER_Y / 3, (int) (GameUtilities.X_MAX / 3.5), GameUtilities.X_MAX / 7, 0, 0, 415, 216, false, false);

            if (startTimer == false) {
                previousTime = TimeUtils.nanoTime();
            }
            startTimer = true;

        } else if (!down) {
            game.batch.draw(Assets.playButton, GameUtilities.CENTER_X - (GameUtilities.X_MAX / 7), GameUtilities.CENTER_Y / 3, (int) (GameUtilities.X_MAX / 3.5), GameUtilities.X_MAX / 7, 0, 0, 415, 216, false, false);
        } else {
            game.batch.draw(Assets.playButton, GameUtilities.CENTER_X - (GameUtilities.X_MAX / 7), (GameUtilities.CENTER_Y / 3) - 10, (int) (GameUtilities.X_MAX / 3.5), GameUtilities.X_MAX / 7, 0, 0, 415, 216, false, false);
        }

        if (!instructionsDown && learningScreen) {
            game.batch.draw(Assets.instructions, GameUtilities.CENTER_X - (int) (GameUtilities.X_MAX / (9.0 * 2 / 5)), (GameUtilities.CENTER_Y / 6), (int) (GameUtilities.X_MAX / (9.0 / 5)), GameUtilities.X_MAX / 9, 0, 0, 1011, 216, false, false);

            if (startTimer == false) {
                previousTime = TimeUtils.nanoTime();
            }
            startTimer = true;

        } else if (!instructionsDown) {
            game.batch.draw(Assets.instructions, GameUtilities.CENTER_X - (int) (GameUtilities.X_MAX / (9.0 * 2 / 5)), (GameUtilities.CENTER_Y / 6), (int) (GameUtilities.X_MAX / (9.0 / 5)), GameUtilities.X_MAX / 9, 0, 0, 1011, 216, false, false);

        } else {
            game.batch.draw(Assets.instructions, GameUtilities.CENTER_X - (int) (GameUtilities.X_MAX / (9.0 * 2 / 5)), (GameUtilities.CENTER_Y / 6) - 10, (int) (GameUtilities.X_MAX / (9.0 / 5)), GameUtilities.X_MAX / 9, 0, 0, 1011, 216, false, false);

        }

        //Get the stateTime for the animation of the flying bird
        stateTime += Gdx.graphics.getDeltaTime();
//
        //Get the image of the correct coloured bird from the respective animation sheet
        currentFrame = redBirdAnimation.getKeyFrame(stateTime, true);

        FileHandle file = Gdx.files.local("data/rescue_bird_highscores.txt");

        // If file doesnt exist, maybe fetch from db containing user names
        if(file.exists()){
            game.highscore = Long.valueOf(file.readString());
        }

        if (game.highscore == 0) {
            game.adsController.hideBannerAd();
        }


        int highscoreCenter = (int)(GameUtilities.X_MAX / 9.6) + (int)(GameUtilities.X_MAX / 14.4) + game.highscoreWidth;
        game.font.draw(game.batch, Long.toString(game.highscore), GameUtilities.CENTER_X - (int)(highscoreCenter / 2) + (int) (GameUtilities.X_MAX / 9.6) + (int) (GameUtilities.X_MAX / 14.4), (GameUtilities.CENTER_Y / 3) + (int)(GameUtilities.Y_MAX / 6.5) + game.fontSize);
        game.batch.draw(Assets.trophy, GameUtilities.CENTER_X - (int)(highscoreCenter / 2), (GameUtilities.CENTER_Y / 3) + (int)(GameUtilities.Y_MAX / 6.5), (int)(GameUtilities.X_MAX / 9.6), (int)(GameUtilities.X_MAX / 9.6), 0, 0, 300, 300, false, false);
        game.batch.draw(Assets.eggCatchLogo, GameUtilities.CENTER_X - ((int) (GameUtilities.X_MAX / 1.15) / 2), GameUtilities.CENTER_Y * 3 / 2, (int) (GameUtilities.X_MAX / 1.15), (int) (GameUtilities.X_MAX / 4.8), 0, 0, 1185, 216, false, false);
        game.batch.draw(currentFrame, GameUtilities.CENTER_X - ((int) (GameUtilities.X_MAX / 9.6)), GameUtilities.CENTER_Y + (int) (GameUtilities.Y_MAX / 29.6), (int) (GameUtilities.X_MAX / 4.8), (int) (GameUtilities.X_MAX / 4.8));
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        int screenYCorrected = GameUtilities.Y_MAX - screenY;

        if ((screenX > GameUtilities.CENTER_X - (GameUtilities.X_MAX / 7) && screenX < GameUtilities.CENTER_X + (GameUtilities.X_MAX / 7))
                && (screenYCorrected > (GameUtilities.CENTER_Y / 3) && screenYCorrected < (GameUtilities.CENTER_Y / 3) + (GameUtilities.X_MAX / 7))) {
            down = true;
        }

        if ((screenX > GameUtilities.CENTER_X - (int) (GameUtilities.X_MAX / (9.0 * 2 / 5)) && screenX < GameUtilities.CENTER_X + (int) (GameUtilities.X_MAX / (9.0 * 2 / 5)))
                && (screenYCorrected > (GameUtilities.CENTER_Y / 6) && screenYCorrected < (GameUtilities.CENTER_Y / 6) + (GameUtilities.X_MAX / 9))) {
            instructionsDown = true;
        }

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        if (down) {
            down = false;
            playGame = true;
            Assets.swish.play();
        }

        if (instructionsDown) {
            instructionsDown = false;
            learningScreen = true;
            Assets.swish.play();
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        // TODO Auto-generated method stub
        return false;
    }


}
