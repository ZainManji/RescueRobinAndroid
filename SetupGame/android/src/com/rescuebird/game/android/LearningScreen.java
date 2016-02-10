package com.rescuebird.game.android;

import java.util.Iterator;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

public class LearningScreen implements Screen, InputProcessor, GestureListener {

    BirdRiseGame game;
    int circlesPerRow;
    ArrayList<Cloud> clouds;
    int dropSpeed;
    int pastGameTime;
    int circlesGatheredInARow;
    long previousTime = TimeUtils.nanoTime();
    long currentTime;
    int prevCirclesGathered;
    int powerupTime;
    Paddle paddle;
    Paddle learningPaddle;
    Boolean gamePaused = false;
    Boolean learningScreen = true;
    Boolean endGame = false;
    Boolean homeScreen = false;
    Boolean down = false;
    Boolean startTimer = false;


    static final int BIRD_ANIMATION_FRAME_COLS = 5;
    static final int BIRD_ANIMATION_FRAME_ROWS = 3;
    Animation redBirdAnimation;
    Animation blueBirdAnimation;
    Animation yellowBirdAnimation;
    Animation greenBirdAnimation;
    TextureRegion[] redFlyFrames;
    TextureRegion[] blueFlyFrames;
    TextureRegion[] greenFlyFrames;
    TextureRegion[] yellowFlyFrames;
    TextureRegion currentFrame;
    float stateTime;
    int tempGameTime = 0;

    public LearningScreen(BirdRiseGame gameRef) {
        this.game = gameRef;
        this.learningScreen = true;

        //Animation construction for flying bird of different colours
        animationConstruction();

        //Reinitialize all the variables to values at the beginning of a new game
        reinitialize();

        //Create the paddle
        paddle = new Paddle();

        learningPaddle = new Paddle();
        learningPaddle.colour = BirdRiseGame.Colour.red;
        learningPaddle.x = (float)(GameUtilities.ROW_COORDINATE_3[1]);
        learningPaddle.y = (int) (GameUtilities.Y_MAX / 4.5);

        //Set paddle's first colour
        paddle.colour = BirdRiseGame.Colour.red;

        GameUtilities.spawnCloud(true, false, clouds);

    }


    //Animation construction needs improvement
    public void animationConstruction() {

        //Create texture region from each bird colour animation sheet
        TextureRegion[][] redBird = TextureRegion.split(Assets.redBirdAnimationSheet,
                Assets.redBirdAnimationSheet.getWidth() / BIRD_ANIMATION_FRAME_COLS,
                Assets.redBirdAnimationSheet.getHeight() / BIRD_ANIMATION_FRAME_ROWS);
        TextureRegion[][] blueBird = TextureRegion.split(Assets.blueBirdAnimationSheet,
                Assets.blueBirdAnimationSheet.getWidth() / BIRD_ANIMATION_FRAME_COLS,
                Assets.blueBirdAnimationSheet.getHeight() / BIRD_ANIMATION_FRAME_ROWS);
        TextureRegion[][] greenBird = TextureRegion.split(Assets.greenBirdAnimationSheet,
                Assets.greenBirdAnimationSheet.getWidth() / BIRD_ANIMATION_FRAME_COLS,
                Assets.greenBirdAnimationSheet.getHeight() / BIRD_ANIMATION_FRAME_ROWS);
        TextureRegion[][] yellowBird = TextureRegion.split(Assets.yellowBirdAnimationSheet,
                Assets.yellowBirdAnimationSheet.getWidth() / BIRD_ANIMATION_FRAME_COLS,
                Assets.yellowBirdAnimationSheet.getHeight() / BIRD_ANIMATION_FRAME_ROWS);

        //Create array that will hold all of the separate images in the red flying bird animation
        redFlyFrames = initializeTextureRegion(BIRD_ANIMATION_FRAME_ROWS, BIRD_ANIMATION_FRAME_COLS, redBird);
        redBirdAnimation = new Animation(0.025f, redFlyFrames);

        //Create array that will hold all of the separate images in the blue flying bird animation
        blueFlyFrames = initializeTextureRegion(BIRD_ANIMATION_FRAME_ROWS, BIRD_ANIMATION_FRAME_COLS, blueBird);
        blueBirdAnimation = new Animation(0.025f, blueFlyFrames);

        //Create array that will hold all of the separate images in the yellow flying bird animation
        yellowFlyFrames = initializeTextureRegion(BIRD_ANIMATION_FRAME_ROWS, BIRD_ANIMATION_FRAME_COLS, yellowBird);
        yellowBirdAnimation = new Animation(0.025f, yellowFlyFrames);

        //Create array that will hold all of the separate images in the green flying bird animation
        greenFlyFrames = initializeTextureRegion(BIRD_ANIMATION_FRAME_ROWS, BIRD_ANIMATION_FRAME_COLS, greenBird);
        greenBirdAnimation = new Animation(0.025f, greenFlyFrames);

        stateTime = 0f;
    }


    public static TextureRegion[] initializeTextureRegion(int frameRows, int frameCols, TextureRegion [][] spriteSheet) {
        TextureRegion [] tmp = new TextureRegion[frameCols * frameRows];
        int index = 0;
        for (int i = 0; i < frameRows; i++) {
            for (int j = 0; j < frameCols; j++) {
                tmp[index++] = spriteSheet[i][j];
            }
        }
        return tmp;
    }

    //Update method
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (startTimer) {
            currentTime = TimeUtils.nanoTime();
            int time = (int) ((currentTime - previousTime) / 100000000);

            if (time >= 1) {
                startTimer = false;

                if (homeScreen) {
                    homeScreen = false;
                    game.adsController.showBannerAd();
                    game.setScreen(game.mainMenuScreen);
                }
            }
        }

        if ((clouds.isEmpty()) || clouds.size() < 3) {
            GameUtilities.spawnCloud(false, false, clouds);
        }

        // Move the clouds.
        Iterator<Cloud> cloudIter = clouds.iterator();
        while (cloudIter.hasNext()) {
            Cloud fallingCloud = cloudIter.next();
            if (endGame) {
                fallingCloud.y += (int) (GameUtilities.Y_MAX / 1.68) * Gdx.graphics.getDeltaTime();
            } else {
                fallingCloud.y -= fallingCloud.dropSpeed * Gdx.graphics.getDeltaTime();
            }

            // Remove cloud if below the screen.
            if (fallingCloud.y + fallingCloud.height < -1 * (int) (GameUtilities.Y_MAX / 7.8) || fallingCloud.y > GameUtilities.Y_MAX) {
                cloudIter.remove();
            }
        }

        //Get current time in nanoseconds
        currentTime = TimeUtils.nanoTime();
        int time = (int) ((currentTime - previousTime) / 1000000000);

        if (time >= 1) {
            tempGameTime++;
            previousTime = currentTime;
        }

        if (tempGameTime > 4) {
            tempGameTime = 0;
        }

        drawObjects();
    }

    public void drawObjects() {
        game.batch.begin();
        game.font.setColor(Color.WHITE);
        game.batch.draw(Assets.blueBackground, 0, 0, GameUtilities.X_MAX, GameUtilities.Y_MAX, 0, 0, 768, 1024, false, false);

        for (Cloud fallingCloud: clouds) {
            game.batch.draw(Assets.cloud, fallingCloud.x, fallingCloud.y, fallingCloud.width, fallingCloud.height, 0, 0, 449, 319, false, false);
        }

        game.batch.draw(Assets.textBG, (int) (GameUtilities.X_MAX * 0.01), (int) (GameUtilities.Y_MAX * 0.31), (int) (GameUtilities.X_MAX * 0.98), (int) (GameUtilities.Y_MAX * 0.63), 0, 0, 600, 600, false, false);

        if (!down && homeScreen) {
            game.batch.draw(Assets.backButton, GameUtilities.CENTER_X - (int) (GameUtilities.X_MAX / 9), (int) (GameUtilities.Y_MAX / 11.8), (int) (GameUtilities.X_MAX / 4.5), (int) (GameUtilities.X_MAX / 8), 0, 0, 451, 216, false, false);
            if (startTimer == false) {
                previousTime = TimeUtils.nanoTime();
            }
            startTimer = true;

        } else if (!down) {
            game.batch.draw(Assets.backButton, GameUtilities.CENTER_X - (int) (GameUtilities.X_MAX / 9), (int) (GameUtilities.Y_MAX / 11.8), (int) (GameUtilities.X_MAX / 4.5), (int) (GameUtilities.X_MAX / 8), 0, 0, 451, 216, false, false);
        } else {
            game.batch.draw(Assets.backButton, GameUtilities.CENTER_X - (int) (GameUtilities.X_MAX / 9), (int) (GameUtilities.Y_MAX / 11.8) - 10, (int) (GameUtilities.X_MAX / 4.5), (int) (GameUtilities.X_MAX / 8), 0, 0, 451, 216, false, false);
        }


        //Get the stateTime for the animation of the flying bird
        stateTime += Gdx.graphics.getDeltaTime();

        int learningWidth = (int) (GameUtilities.X_MAX / 12) + game.thirdScoreWidth + (int)(GameUtilities.X_MAX / 9.6);
        int iconPos = GameUtilities.CENTER_X - (learningWidth / 2);
        int explanationPos = iconPos + (int)(GameUtilities.X_MAX / 9.6) + (int) (GameUtilities.X_MAX / 12);

        game.font.draw(game.batch, "Swipe Left or Right to Move", GameUtilities.CENTER_X - (int)(game.firstLearningWidth / 2),	(int)(GameUtilities.Y_MAX * 0.9));
        game.font.draw(game.batch, "Swipe Down to Speed the Drop", GameUtilities.CENTER_X - (int)(game.thirdLearningWidth / 2),	(int)(GameUtilities.Y_MAX * 0.8));

        //Get the image of the correct coloured bird from the respective animation sheet
        currentFrame = redBirdAnimation.getKeyFrame(stateTime, true);

        //Draw correct coloured paddle (Bird in this case) (size was originally 50x50)
        game.batch.draw(currentFrame, learningPaddle.x, learningPaddle.y, (int) (GameUtilities.X_MAX / 7.2), (int) (GameUtilities.X_MAX / 7.2));
        game.font.draw(game.batch, "Rescue Eggs with Same Colour as Robin", GameUtilities.CENTER_X - (int)(game.secondLearningWidth / 2), (int)(GameUtilities.Y_MAX * 0.7));

        if (tempGameTime <= 1) {
            game.batch.draw(Assets.redCircle, iconPos, (int)(GameUtilities.Y_MAX * 0.55), (int) (GameUtilities.X_MAX / 12), (int) (GameUtilities.X_MAX / 12), 0, 0, 256, 256, false, false);
            game.font.draw(game.batch, "+ 50 points", explanationPos, (int)(GameUtilities.Y_MAX * 0.55) + (int) (GameUtilities.X_MAX / 14.4));
            game.batch.draw(Assets.red_powerup, iconPos, (int)(GameUtilities.Y_MAX * 0.45), (int) (GameUtilities.X_MAX / 12), (int) (GameUtilities.X_MAX / 12), 0, 0, 791, 791, false, false);
            game.font.draw(game.batch, "+ 100 points", explanationPos, (int)(GameUtilities.Y_MAX * 0.45) + (int) (GameUtilities.X_MAX / 14.4));
            game.batch.draw(Assets.star, iconPos, (int)(GameUtilities.Y_MAX * 0.35), (int) (GameUtilities.X_MAX / 12), (int) (GameUtilities.X_MAX / 12), 0, 0, 256, 256, false, false);
            game.font.draw(game.batch, "Invincible for 10 seconds", explanationPos, (int)(GameUtilities.Y_MAX * 0.35) + (int) (GameUtilities.X_MAX / 14.4));
            currentFrame = redBirdAnimation.getKeyFrame(stateTime, true);
            game.batch.draw(currentFrame, learningPaddle.x, learningPaddle.y, (int) (GameUtilities.X_MAX / 7.2), (int) (GameUtilities.X_MAX / 7.2));
        } else if (tempGameTime <= 2) {
            game.batch.draw(Assets.blueCircle, iconPos, (int)(GameUtilities.Y_MAX * 0.55), (int) (GameUtilities.X_MAX / 12), (int) (GameUtilities.X_MAX / 12), 0, 0, 256, 256, false, false);
            game.font.draw(game.batch, "+ 50 points", explanationPos, (int)(GameUtilities.Y_MAX * 0.55) + (int) (GameUtilities.X_MAX / 14.4));
            game.batch.draw(Assets.blue_powerup, iconPos, (int)(GameUtilities.Y_MAX * 0.45), (int) (GameUtilities.X_MAX / 12), (int) (GameUtilities.X_MAX / 12), 0, 0, 791, 791, false, false);
            game.font.draw(game.batch, "+ 100 points", explanationPos, (int)(GameUtilities.Y_MAX * 0.45) + (int) (GameUtilities.X_MAX / 14.4));
            game.batch.draw(Assets.star, iconPos, (int)(GameUtilities.Y_MAX * 0.35), (int) (GameUtilities.X_MAX / 12), (int) (GameUtilities.X_MAX / 12), 0, 0, 256, 256, false, false);
            game.font.draw(game.batch, "Invincible for 10 seconds", explanationPos, (int)(GameUtilities.Y_MAX * 0.35) + (int) (GameUtilities.X_MAX / 14.4));
            currentFrame = blueBirdAnimation.getKeyFrame(stateTime, true);
            game.batch.draw(currentFrame, learningPaddle.x, learningPaddle.y, (int) (GameUtilities.X_MAX / 7.2), (int) (GameUtilities.X_MAX / 7.2));
        } else if (tempGameTime <= 3) {
            game.batch.draw(Assets.greenCircle, iconPos, (int)(GameUtilities.Y_MAX * 0.55), (int) (GameUtilities.X_MAX / 12), (int) (GameUtilities.X_MAX / 12), 0, 0, 256, 256, false, false);
            game.font.draw(game.batch, "+ 50 points", explanationPos, (int)(GameUtilities.Y_MAX * 0.55) + (int) (GameUtilities.X_MAX / 14.4));
            game.batch.draw(Assets.green_powerup, iconPos, (int)(GameUtilities.Y_MAX * 0.45), (int) (GameUtilities.X_MAX / 12), (int) (GameUtilities.X_MAX / 12), 0, 0, 791, 791, false, false);
            game.font.draw(game.batch, "+ 100 points", explanationPos, (int)(GameUtilities.Y_MAX * 0.45) + (int) (GameUtilities.X_MAX / 14.4));
            game.batch.draw(Assets.star, iconPos, (int)(GameUtilities.Y_MAX * 0.35), (int) (GameUtilities.X_MAX / 12), (int) (GameUtilities.X_MAX / 12), 0, 0, 256, 256, false, false);
            game.font.draw(game.batch, "Invincible for 10 seconds", explanationPos, (int)(GameUtilities.Y_MAX * 0.35) + (int) (GameUtilities.X_MAX / 14.4));
            currentFrame = greenBirdAnimation.getKeyFrame(stateTime, true);
            game.batch.draw(currentFrame, learningPaddle.x, learningPaddle.y, (int) (GameUtilities.X_MAX / 7.2), (int) (GameUtilities.X_MAX / 7.2));
        } else if (tempGameTime <= 4) {
            game.batch.draw(Assets.yellowCircle, iconPos, (int)(GameUtilities.Y_MAX * 0.55), (int) (GameUtilities.X_MAX / 12), (int) (GameUtilities.X_MAX / 12), 0, 0, 256, 256, false, false);
            game.font.draw(game.batch, "+ 50 points", explanationPos, (int)(GameUtilities.Y_MAX * 0.55) + (int) (GameUtilities.X_MAX / 14.4));
            game.batch.draw(Assets.yellow_powerup, iconPos, (int)(GameUtilities.Y_MAX * 0.45), (int) (GameUtilities.X_MAX / 12), (int) (GameUtilities.X_MAX / 12), 0, 0, 791, 791, false, false);
            game.font.draw(game.batch, "+ 100 points", explanationPos, (int)(GameUtilities.Y_MAX * 0.45) + (int) (GameUtilities.X_MAX / 14.4));
            game.batch.draw(Assets.star, iconPos, (int)(GameUtilities.Y_MAX * 0.35), (int) (GameUtilities.X_MAX / 12), (int) (GameUtilities.X_MAX / 12), 0, 0, 256, 256, false, false);
            game.font.draw(game.batch, "Invincible for 10 seconds", explanationPos, (int)(GameUtilities.Y_MAX * 0.35) + (int) (GameUtilities.X_MAX / 14.4));
            currentFrame = yellowBirdAnimation.getKeyFrame(stateTime, true);
            game.batch.draw(currentFrame, learningPaddle.x, learningPaddle.y, (int) (GameUtilities.X_MAX / 7.2), (int) (GameUtilities.X_MAX / 7.2));
        } else if (tempGameTime > 4) {
            previousTime = currentTime;
            this.learningScreen = false;
        }

        game.batch.draw(Assets.leftArrow, learningPaddle.x - (int) (GameUtilities.X_MAX / 4.8), learningPaddle.y, (int) (GameUtilities.X_MAX / 9.6), (int) (GameUtilities.X_MAX / 18), 0, 0, 256, 256, false, false);
        game.batch.draw(Assets.rightArrow, learningPaddle.x + (int) (GameUtilities.X_MAX / 4.8), learningPaddle.y, (int) (GameUtilities.X_MAX / 9.6), (int) (GameUtilities.X_MAX / 18), 0, 0, 256, 256, false, false);
        game.batch.end();
    }

    public void reinitialize() {
        dropSpeed = (int) (GameUtilities.Y_MAX / 3.933);
        pastGameTime = 0;
        circlesPerRow = 4;
        prevCirclesGathered = 0;
        game.livesLeft = GameUtilities.MAX_LIVES;
        game.score = 0;
        game.circlesGathered = 0;
        prevCirclesGathered = 0;
        game.gameTime = 0;
        clouds = new ArrayList<Cloud>();
        powerupTime = 0;
        circlesGatheredInARow = 0;
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new GestureDetector(this));
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
        Assets.dispose();
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

        if ((screenX > GameUtilities.CENTER_X - (int) (GameUtilities.X_MAX / 9) && screenX < GameUtilities.CENTER_X + (int) (GameUtilities.X_MAX / 9))
                && (screenYCorrected > (int) (GameUtilities.Y_MAX / 11.8) && screenYCorrected < (int) (GameUtilities.Y_MAX / 11.8) + (int) (GameUtilities.X_MAX / 8))) {
            down = true;
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        if (down) {
            down = false;
            homeScreen = true;
            Assets.swish.play();
        }
        return true;
    }

    @Override
    public boolean touchDown(float screenX, float screenY, int pointer, int button) {
        int screenYCorrected = (int)(GameUtilities.Y_MAX - screenY);

        if ((screenX > GameUtilities.CENTER_X - (int) (GameUtilities.X_MAX / 9) && screenX < GameUtilities.CENTER_X + (int) (GameUtilities.X_MAX / 9))
                && (screenYCorrected > (int) (GameUtilities.Y_MAX / 11.8) && screenYCorrected < (int) (GameUtilities.Y_MAX / 11.8) + (int) (GameUtilities.X_MAX / 8))) {
            down = true;
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

    @Override
    public boolean tap(float x, float y, int count, int button) {
        if (down) {
            down = false;
            homeScreen = true;
            Assets.swish.play();
        }
        return true;
    }

    @Override
    public boolean longPress(float x, float y) {
        // TODO Auto-generated method stub
        return false;
    }

    public void movePaddleLeft() {
        if (learningPaddle.index != 0) {
            learningPaddle.x = (float)(GameUtilities.ROW_COORDINATE_3[learningPaddle.index - 1]);
            learningPaddle.index = learningPaddle.index - 1;
        }
    }

    public void movePaddleRight() {
        if (learningPaddle.index != 2) {
            learningPaddle.x = (float)(GameUtilities.ROW_COORDINATE_3[learningPaddle.index + 1]);
            learningPaddle.index = learningPaddle.index + 1;
        }
    }

    //Checks if screen is swiped left or right
    @Override
    public boolean fling(float velocityX, float velocityY, int button) {

        if (!gamePaused) {
            if(Math.abs(velocityX) > Math.abs(velocityY)){
                if (velocityX > 0) {
                    movePaddleRight();
                } else if (velocityX < 0) {
                    movePaddleLeft();
                }
            } else {
                // Ignore the input, because we don't care about up/down swipes.
            }
        }
        return true;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
                         Vector2 pointer1, Vector2 pointer2) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean panStop(float floatx, float floaty, int x, int y ) {
        // TODO Auto-generated method stub
        return false;
    }
}
