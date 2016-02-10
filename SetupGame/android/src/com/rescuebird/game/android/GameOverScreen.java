package com.rescuebird.game.android;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.ArrayList;
import java.util.Iterator;

public class GameOverScreen implements Screen, InputProcessor {

    BirdRiseGame game;
    boolean down;
    boolean replay;
    boolean returnHome;
    long prevGameScore;
    boolean startTimer;
    long currentTime;
    long previousTime;
    boolean pressReplay;
    boolean pressHome;
    ArrayList<Cloud> clouds = new ArrayList<Cloud>();
    int gameOverScoreWidth;
    Boolean newHighscore;

    public GameOverScreen (BirdRiseGame gameRef) {
        game = gameRef;
        prevGameScore = game.score;
        newHighscore = false;

        // Obtain previous high score
        FileHandle file = Gdx.files.local("data/rescue_bird_highscores.txt");

        if(file.exists()){
            game.highscore = Long.valueOf(file.readString());
            if (game.score > game.highscore) {
                newHighscore = true;
                game.highscore = prevGameScore;
                file.writeString(Long.toString(prevGameScore), false);
            }
        } else {
            game.highscore = game.score;
            file.writeString(Long.toString(game.highscore), false);
        }

        // Spawn the first cloud
        GameUtilities.spawnCloud(true, false, clouds);

        // Initialize a new game screen in case user wants to replay.
        game.gameScreen = new GameScreen(game);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if ((clouds.isEmpty()) || clouds.size() < 2) {
            GameUtilities.spawnCloud(false, false, clouds);
        }

        // Move the clouds.
        Iterator<Cloud> cloudIter = clouds.iterator();
        while (cloudIter.hasNext()) {
            Cloud fallingCloud = cloudIter.next();
            fallingCloud.y -= fallingCloud.dropSpeed * Gdx.graphics.getDeltaTime();

            // Remove the cloud if below the screen.
            if (fallingCloud.y + fallingCloud.height < 0) {
                cloudIter.remove();
            }
        }

        // Trigger a small delay. This is primarily used for the buttons after you let go of it.
        if (startTimer) {
            currentTime = TimeUtils.nanoTime();
            int time = (int) ((currentTime - previousTime) / 100000000);

            if (time >= 1) {
                startTimer = false;

                if (pressReplay) {
                    pressReplay = false;
                    game.adsController.hideBannerAd();
                    game.setScreen(game.gameScreen);
                } else if (pressHome) {
                    pressHome = false;
                    game.adsController.showBannerAd();
                    game.setScreen(game.mainMenuScreen);
                }
            }
        }

        // Draw game over screen and game ending stats.
        drawObjects();
    }

    public void drawObjects() {
        game.batch.begin();
        game.font.setColor(Color.BLACK);
        game.batch.draw(Assets.blueBackground, 0, 0, GameUtilities.X_MAX, GameUtilities.Y_MAX, 0, 0, 768, 1024, false, false);

        for (Cloud fallingCloud: clouds) {
            game.batch.draw(Assets.cloud, fallingCloud.x, fallingCloud.y, fallingCloud.width, fallingCloud.height, 0, 0, 449, 319, false, false);
        }

        if (!down && pressReplay) {
            game.batch.draw(Assets.replay, (GameUtilities.CENTER_X / 3) - (int) (GameUtilities.X_MAX / 14.4), GameUtilities.CENTER_Y, (int) (GameUtilities.X_MAX / 2.4), (int) (GameUtilities.X_MAX / 7.2), 0, 0, 633, 216, false, false);
            startTimer = true;
        }
        else if (down && replay) {
            game.batch.draw(Assets.replay, (GameUtilities.CENTER_X / 3) - (int) (GameUtilities.X_MAX / 14.4), (GameUtilities.CENTER_Y) - 10, (int) (GameUtilities.X_MAX / 2.4), (int) (GameUtilities.X_MAX / 7.2), 0, 0, 633, 216, false, false);
        } else {
            game.batch.draw(Assets.replay, (GameUtilities.CENTER_X / 3) - (int) (GameUtilities.X_MAX / 14.4), GameUtilities.CENTER_Y, (int) (GameUtilities.X_MAX / 2.4), (int) (GameUtilities.X_MAX / 7.2), 0, 0, 633, 216, false, false);
        }

        if (!down && pressHome) {
            game.batch.draw(Assets.returnHome, GameUtilities.CENTER_X + (GameUtilities.CENTER_X / 3) - (int) (GameUtilities.X_MAX / 14.4), GameUtilities.CENTER_Y, (int) (GameUtilities.X_MAX / 3.3), (int) (GameUtilities.X_MAX / 7.2), 0, 0, 515, 216, false, false);
            startTimer = true;
        }
        else if (down && returnHome) {
            game.batch.draw(Assets.returnHome, GameUtilities.CENTER_X + (GameUtilities.CENTER_X / 3) - (int) (GameUtilities.X_MAX / 14.4), (GameUtilities.CENTER_Y) - 10, (int) (GameUtilities.X_MAX / 3.3), (int) (GameUtilities.X_MAX / 7.2), 0, 0, 515, 216, false, false);
        } else {
            game.batch.draw(Assets.returnHome, GameUtilities.CENTER_X + (GameUtilities.CENTER_X / 3) - (int) (GameUtilities.X_MAX / 14.4), GameUtilities.CENTER_Y, (int) (GameUtilities.X_MAX / 3.3), (int) (GameUtilities.X_MAX / 7.2), 0, 0, 515, 216, false, false);
        }

        if (newHighscore) {
            game.batch.draw(Assets.trophy, GameUtilities.CENTER_X - (int)(GameUtilities.X_MAX / 19.2), (GameUtilities.CENTER_Y / 3) + (int)(GameUtilities.Y_MAX / 6.5), (int)(GameUtilities.X_MAX / 9.6), (int)(GameUtilities.X_MAX / 9.6), 0, 0, 300, 300, false, false);
        }

        gameOverScoreWidth = Math.max((int)(game.font.getBounds("Score").width), (int) game.font.getBounds(Long.toString(prevGameScore)).width) + (int)(GameUtilities.X_MAX / 7.2) + Math.max((int)(game.font.getBounds("Best").width), (int) game.font.getBounds(Long.toString(game.highscore)).width);

        game.font.draw(game.batch, "Score", GameUtilities.CENTER_X - (gameOverScoreWidth / 2),	(GameUtilities.CENTER_Y / 3) + (int)(GameUtilities.Y_MAX / 7.87));
        game.font.draw(game.batch, Long.toString(prevGameScore), GameUtilities.CENTER_X - (gameOverScoreWidth / 2),	(GameUtilities.CENTER_Y / 3) + (int)(GameUtilities.Y_MAX / 15.7));
        game.font.draw(game.batch, "Best", GameUtilities.CENTER_X - (gameOverScoreWidth / 2) + Math.max((int)(game.font.getBounds("Score").width), (int) game.font.getBounds(Long.toString(prevGameScore)).width) + (int)(GameUtilities.X_MAX / 7.2), (GameUtilities.CENTER_Y / 3) + (int)(GameUtilities.Y_MAX / 7.87));
        game.font.draw(game.batch, Long.toString(game.highscore), GameUtilities.CENTER_X - (gameOverScoreWidth / 2) + Math.max((int)(game.font.getBounds("Score").width), (int) game.font.getBounds(Long.toString(prevGameScore)).width) + (int)(GameUtilities.X_MAX / 7.2), (GameUtilities.CENTER_Y / 3) + (int)(GameUtilities.Y_MAX / 15.7));
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
        System.gc();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        System.gc();
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

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        int screenYCorrected = GameUtilities.Y_MAX - screenY;

        if ((screenX > (GameUtilities.CENTER_X / 3) - (int) (GameUtilities.X_MAX / 14.4) && screenX < (GameUtilities.CENTER_X / 3) - (int) (GameUtilities.X_MAX / 14.4) + (int) (GameUtilities.X_MAX / 2.4))
                && (screenYCorrected > (GameUtilities.CENTER_Y) && screenYCorrected < (GameUtilities.CENTER_Y) + (int) (GameUtilities.X_MAX / 7.2))) {
            down = true;
            replay = true;
            Assets.swish.play();
        }

        if ((screenX > GameUtilities.CENTER_X + (GameUtilities.CENTER_X / 3) - (int) (GameUtilities.X_MAX / 14.4) && screenX < GameUtilities.CENTER_X + (GameUtilities.CENTER_X / 3) - (int) (GameUtilities.X_MAX / 14.4) + (int) (GameUtilities.X_MAX / 3.3))
                && (screenYCorrected > (GameUtilities.CENTER_Y) && screenYCorrected < (GameUtilities.CENTER_Y) + (int) (GameUtilities.X_MAX / 7.2))) {
            down = true;
            returnHome = true;
            Assets.swish.play();
        }

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (down && replay) {
            down = false;
            replay = false;
            pressReplay = true;
        }

        if (down && returnHome) {
            down = false;
            replay = false;
            pressHome = true;
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        // TODO Auto-generated method stub
        return false;
    }
}
