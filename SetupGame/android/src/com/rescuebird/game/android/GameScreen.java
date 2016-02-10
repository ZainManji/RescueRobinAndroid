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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

public class GameScreen implements Screen, InputProcessor, GestureListener {

    BirdRiseGame game;
    int circlesPerRow;
    ArrayList<Circle> circles;
    ArrayList<Cloud> clouds;
    int dropSpeed;
    int pastGameTime;
    int quickSpeed;
    int randomNum;
    int randomCircleNum;
    int circlesGatheredInARow;
    long previousTime = TimeUtils.nanoTime();
    long currentTime;
    long sizeTime;
    int prevCirclesGathered;
    int prevCirclesGatheredForSpeed;
    int powerupTime;
    Paddle paddle;
    Boolean showTransition = false;
    Boolean showTwinkle = false;
    Boolean endGame = false;
    Boolean quickDrop = false;
    int endGameTime;
    int birdSize = (int) (GameUtilities.X_MAX / 7.2);

    //Animation variables
    private static final int TRANSITION_ANIMATION_FRAME_COLS = 3;
    private static final int TRANSITION_ANIMATION_FRAME_ROWS = 4;
    Animation transitionAnimation;
    TextureRegion[] transitionFrames;
    TextureRegion transitionFrame;
    float transitionStateTime;

    private static final int TWINKLE_ANIMATION_FRAME_COLS = 12;
    private static final int TWINKLE_ANIMATION_FRAME_ROWS = 1;
    Animation twinkleAnimation;
    TextureRegion[] twinkleFrames;
    float twinkleStateTime;
    TextureRegion twinkleFrame;

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
    int skipButtonY = 100;


    public GameScreen(BirdRiseGame gameRef) {
        this.game = gameRef;

        // Animation construction for flying bird of different colours.
        animationConstruction();

        // Reinitialize all the variables to values at the beginning of a new game.
        reinitialize();

        // Create the paddle, which is the rescue bird.
        paddle = new Paddle();

        //Set paddle's first colour
        paddle.colour = newColour(false);

        GameUtilities.spawnCloud(true, false, clouds);

        // Spawn the first row of eggs.
        spawnRow();
    }

    public void animationConstruction() {
        // Create texture region from each bird colour animation sheet.
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
        TextureRegion[][] transitionFrameArray = TextureRegion.split(Assets.transitionAnimationSheet,
                Assets.transitionAnimationSheet.getWidth() / TRANSITION_ANIMATION_FRAME_COLS,
                Assets.transitionAnimationSheet.getHeight() / TRANSITION_ANIMATION_FRAME_ROWS);
        TextureRegion[][] twinkleFrameArray = TextureRegion.split(Assets.twinkleAnimationSheet,
                Assets.twinkleAnimationSheet.getWidth() / TWINKLE_ANIMATION_FRAME_COLS,
                Assets.twinkleAnimationSheet.getHeight() / TWINKLE_ANIMATION_FRAME_ROWS);

        // Create array that will hold all of the separate images in the twinkle animation.
        twinkleFrames = initializeTextureRegion(TWINKLE_ANIMATION_FRAME_ROWS, TWINKLE_ANIMATION_FRAME_COLS,
                twinkleFrameArray);
        twinkleAnimation = new Animation(0.04f, twinkleFrames);
        twinkleStateTime = 0f;

        // Create array that will hold all of the separate images in the transition animation.
        transitionFrames = initializeTextureRegion(TRANSITION_ANIMATION_FRAME_ROWS, TRANSITION_ANIMATION_FRAME_COLS,
                transitionFrameArray);
        transitionAnimation = new Animation(0.06f, transitionFrames);
        transitionStateTime = 0f;

        // Create array that will hold all of the separate images in the red flying bird animation.
        redFlyFrames = initializeTextureRegion(BIRD_ANIMATION_FRAME_ROWS, BIRD_ANIMATION_FRAME_COLS, redBird);
        redBirdAnimation = new Animation(0.025f, redFlyFrames);

        // Create array that will hold all of the separate images in the blue flying bird animation.
        blueFlyFrames = initializeTextureRegion(BIRD_ANIMATION_FRAME_ROWS, BIRD_ANIMATION_FRAME_COLS, blueBird);
        blueBirdAnimation = new Animation(0.025f, blueFlyFrames);

        // Create array that will hold all of the separate images in the yellow flying bird animation.
        yellowFlyFrames = initializeTextureRegion(BIRD_ANIMATION_FRAME_ROWS, BIRD_ANIMATION_FRAME_COLS, yellowBird);
        yellowBirdAnimation = new Animation(0.025f, yellowFlyFrames);

        // Create array that will hold all of the separate images in the green flying bird animation.
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

    private void spawnRow() {
        // Choose which egg in the row will contain the same colour as the bird.
        randomCircleNum = MathUtils.random(0, circlesPerRow - 1);
        int secondRandomNum = MathUtils.random(0, circlesPerRow - 1);

        while (secondRandomNum == randomCircleNum) {
            secondRandomNum = MathUtils.random(0, circlesPerRow - 1);
        }

        // Create all the eggs in the new row.
        for (int i = 0; i < circlesPerRow; i++) {
            Circle fallingCircle = new Circle();
            fallingCircle.x = GameUtilities.ROW_COORDINATE_4[i];
            fallingCircle.y = GameUtilities.Y_MAX;
            fallingCircle.width = (int) (GameUtilities.X_MAX / 14.4);
            fallingCircle.height = (int) (GameUtilities.X_MAX / 14.4);

            // If the egg we are creating in the row is the circle we chose to match the bird, then assign the bird's
            // colour to that egg, else pick a different random colour for the egg. Also assign a power up to the
            // egg.
            if (randomCircleNum == i) {
                fallingCircle.colour = paddle.colour;

                // If the bird doesn't have a power up assigned to it, then randomly assign a power up to the egg
                // we are creating (power up can be none). However, if the bird already has a power up assigned to
                // it, then don't give a power up to the egg.
                if (paddle.powerup == BirdRiseGame.Powerup.none) {
                    fallingCircle.powerup = newPowerup();
                }
                else {
                    fallingCircle.powerup = BirdRiseGame.Powerup.none;
                }

                if (fallingCircle.powerup == BirdRiseGame.Powerup.immunity) {
                    fallingCircle.powerup = BirdRiseGame.Powerup.none;
                }
            }
            else if (secondRandomNum == i) {
                fallingCircle.colour = newColour(false);
                fallingCircle.powerup = newPowerup();

                if (fallingCircle.powerup != BirdRiseGame.Powerup.immunity) {
                    fallingCircle.powerup = BirdRiseGame.Powerup.none;
                }
            }
            else {
                fallingCircle.colour = newColour(false);
                fallingCircle.powerup = BirdRiseGame.Powerup.none;
            }

            //Add egg to the list of all eggs.
            circles.add(fallingCircle);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.font.setColor(Color.BLACK);

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

            // Remove egg if below the screen.
            if (fallingCloud.y + fallingCloud.height < -1 * (int) (GameUtilities.Y_MAX / 7.8) || fallingCloud.y > GameUtilities.Y_MAX) {
                cloudIter.remove();
            }
        }

        // If 2 eggs dropped has passed and the speed at which the circles drop is below 500, increase the speed at
        // which the circles drop (Max drop speed is 500) (Increase drop speed by 4%).
        if ((game.circlesGathered - prevCirclesGatheredForSpeed) > 1 && dropSpeed < (int) (GameUtilities.Y_MAX * 1.1)) {
            pastGameTime = game.gameTime;
            dropSpeed = (int) (1.04 * dropSpeed);
            prevCirclesGatheredForSpeed = game.circlesGathered;
        }

        //Change paddle colour after user catches 3 circles of the correct colour
        if ((game.circlesGathered - prevCirclesGathered) > 3) {
            paddle.colour = newColour(true);
            birdSize = (int) (GameUtilities.X_MAX / 6);
            sizeTime = TimeUtils.nanoTime();

            // Play new paddle colour sound.
            Assets.dingSound.play();
            prevCirclesGathered = game.circlesGathered;
        }

        // Set the bird size to it's original size.
        if ((int) ((currentTime - sizeTime) / 300000000) >= 1) {
            birdSize = (int) (GameUtilities.X_MAX / 7.2);
        }

        // Get current time in nanoseconds.
        currentTime = TimeUtils.nanoTime();

        // Check when to spawn next row (i.e. when no row exists, or when only one row exists and has dropped a certain
        // amount on the screen).
        if ((circles.isEmpty()) || ((circles.get(0).y < 0.3 * GameUtilities.Y_MAX) && (circles.size() == circlesPerRow))) {
            spawnRow();
        }

        // Move the eggs down the screen, remove any eggs that are beneath the bottom edge of the screen
        // or that hit the bird.
        Iterator<Circle> iter = circles.iterator();
        while (iter.hasNext()) {
            Circle fallingCircle = iter.next();

            if (endGame) {
                fallingCircle.y += (int) (GameUtilities.Y_MAX / 1.68) * Gdx.graphics.getDeltaTime();
            } else if (quickDrop) {
                fallingCircle.y -= quickSpeed * Gdx.graphics.getDeltaTime();
            } else {
                fallingCircle.y -= dropSpeed * Gdx.graphics.getDeltaTime();
            }

            if (fallingCircle.y + (int) (GameUtilities.X_MAX / 14.4) < 0) {
                iter.remove();
            }

            // Egg hits bird.
            if (fallingCircle.overlaps(paddle)) {

                quickDrop = false;

                if ((fallingCircle.colour == paddle.colour) || (paddle.powerup == BirdRiseGame.Powerup.immunity) ||
                       (fallingCircle.powerup == BirdRiseGame.Powerup.immunity)) {

                    // Play good catch sound.
                    Assets.catchSound.play();

                    showTwinkle = true;
                    game.circlesGathered++;
                    circlesGatheredInARow++;

                    // Check if circle has a power up, and if so, change paddle's power up to the circle's power up
                    // and reset the power up time.
                    if (fallingCircle.powerup == BirdRiseGame.Powerup.immunity) {
                        paddle.powerup = fallingCircle.powerup;
                        powerupTime = GameUtilities.POWERUP_TIME;
                        incrementScore(false);
                    } else if (fallingCircle.powerup == BirdRiseGame.Powerup.doublePoints) {
                        incrementScore(true);
                    } else {
                        incrementScore(false);
                    }

                } else if (fallingCircle.colour != paddle.colour) {
                    //Play bad sound
                    showTransition = true;
                    circlesGatheredInARow = 0;
                    game.livesLeft--;
                    Assets.error.play();
                    paddle.powerup = BirdRiseGame.Powerup.none;
                }

                iter.remove();
            }
        }

        // Update time every second
        if ((int) ((currentTime - previousTime) / 1000000000) >= 1) {
            previousTime = currentTime;
            game.gameTime++;
            pastGameTime = 0;

            if (endGame) {
                endGameTime++;
            }

            // Decrement power up time as long as it is greater than 0, once it hits 0 then remove the power up from
            // bird.
            if (powerupTime > 0) {
                powerupTime--;
            } else {
                paddle.powerup = BirdRiseGame.Powerup.none;
            }
        }

        // If user loses all lives, reinitialize all variables to that of a new game, and move to the game over screen.
        if (game.livesLeft <= 0) {

            // Wait 3 seconds before going to game over screen
            // Create 2 clouds from the bottom and make them rise.
            if (!endGame) {
                GameUtilities.spawnCloud(true, true, clouds);
                // Create 2 clouds from the bottom
                GameUtilities.spawnCloud(true, true, clouds);
            }

            endGame = true;

            currentTime = TimeUtils.nanoTime();

            if ((int) ((currentTime - previousTime) / 1000000000) >= 1) {
                previousTime = currentTime;
                pastGameTime = 0;

                if (endGame) {
                    endGameTime++;
                    showTransition = true;
                }
            }

            if (endGameTime > 2) {
                Assets.swish.play();
                endGame = false;
                endGameTime = 0;
                game.adsController.showBannerAd();
                game.setScreen(new GameOverScreen(game));
            }
        }

        // Move the eggs up the screen, remove any circles that are above the top edge of the screen.
        // This happens at the end of the game.
        iter = circles.iterator();

        while (iter.hasNext()) {
            Circle fallingCircle = iter.next();
            if (endGame) {
                fallingCircle.y += (int) (GameUtilities.Y_MAX / 1.18) * Gdx.graphics.getDeltaTime();
            }
        }

        //Draw all the objects to the screen
        drawObjects();
    }


    public void incrementScore(Boolean doublePoints) {
        // Multiplier increases by 1 every 5 circles gathered in a row.
        int comboMultiplier = (circlesGatheredInARow / 5) + 1;

        // Apply appropriate powerup to score.
        if (doublePoints) {
            game.score += (50 * 2) * comboMultiplier;
        } else {
            game.score += 50 * comboMultiplier;
        }
    }

    public BirdRiseGame.Powerup newPowerup() {
        // Pick a random number with probability 1/20.
        int randNum;
        randNum = MathUtils.random(1, 20);

        // Return the appropriate powerup.
        if (randNum == 2 || randNum == 3) {
            return BirdRiseGame.Powerup.doublePoints;
        } else if (randNum == 4) {
            return BirdRiseGame.Powerup.immunity;
        } else {
            return BirdRiseGame.Powerup.none;
        }
    }

    public BirdRiseGame.Colour newColour(Boolean changePaddle) {
        int randomColumn;

        // Gets index of last circle in the array.
        int index = circles.size() - 1;

        // Checks if we need to change the paddle colour.
        if (changePaddle) {
            randomColumn = MathUtils.random(0, circlesPerRow - 1);
            while (circles.get(index - randomColumn).colour == paddle.colour) {
                randomColumn = MathUtils.random(0, circlesPerRow - 1);
            }
            return circles.get(index - randomColumn).colour;
        }

        // Pick a colour for a circle that is different than the paddle colour and return that colour.
        randomNum = MathUtils.random(0, 3);
        while (BirdRiseGame.Colour.values()[randomNum] == paddle.colour) {
            randomNum = MathUtils.random(0, 3);
        }

        return BirdRiseGame.Colour.values()[randomNum];
    }

    public void reinitialize() {
        dropSpeed = (int) (GameUtilities.Y_MAX / 3.933);
        quickSpeed = (int) (GameUtilities.Y_MAX);
        pastGameTime = 0;
        circlesPerRow = 4;
        prevCirclesGathered = 0;
        prevCirclesGatheredForSpeed = 0;
        game.livesLeft = GameUtilities.MAX_LIVES;
        game.score = 0;
        game.circlesGathered = 0;
        prevCirclesGathered = 0;
        game.gameTime = 0;
        circles = new ArrayList<Circle>();
        clouds = new ArrayList<Cloud>();
        powerupTime = 0;
        circlesGatheredInARow = 0;
    }

    public void drawObjects() {
        //Get the stateTime for the animation of the flying bird
        stateTime += Gdx.graphics.getDeltaTime();

        if (paddle.colour == BirdRiseGame.Colour.blue) {
            currentFrame = blueBirdAnimation.getKeyFrame(stateTime, true);
        } else if (paddle.colour == BirdRiseGame.Colour.green) {
            currentFrame = greenBirdAnimation.getKeyFrame(stateTime, true);
        } else if (paddle.colour == BirdRiseGame.Colour.red) {
            currentFrame = redBirdAnimation.getKeyFrame(stateTime, true);
        } else if (paddle.colour == BirdRiseGame.Colour.yellow) {
            currentFrame = yellowBirdAnimation.getKeyFrame(stateTime, true);
        }

        if (showTransition) {
            transitionStateTime += Gdx.graphics.getDeltaTime();
            transitionFrame = transitionAnimation.getKeyFrame(transitionStateTime, true);

            if (transitionStateTime > 0.7) {
                showTransition = false;
                transitionStateTime = 0f;
            }
        }

        if (showTwinkle) {
            twinkleStateTime += Gdx.graphics.getDeltaTime();
            twinkleFrame = twinkleAnimation.getKeyFrame(twinkleStateTime, true);

            if (twinkleStateTime > 0.5) {
                showTwinkle = false;
                twinkleStateTime = 0f;
            }
        }

        game.batch.begin();
        game.batch.draw(Assets.blueBackground, 0, 0, GameUtilities.X_MAX, GameUtilities.Y_MAX, 0, 0, 1024, 768, false, false);

        for (Cloud fallingCloud: clouds) {
            game.batch.draw(Assets.cloud, fallingCloud.x, fallingCloud.y, fallingCloud.width, fallingCloud.height, 0, 0, 449, 319, false, false);
        }

        if (showTwinkle) {
            game.batch.draw(twinkleFrame, paddle.x - 10, paddle.y - 25, (int) (GameUtilities.X_MAX / 5.76), (int) (GameUtilities.X_MAX / 5.76));
        }

        if (showTransition) {
            game.batch.draw(transitionFrame, paddle.x, paddle.y, (int) (GameUtilities.X_MAX / 5.76), (int) (GameUtilities.X_MAX / 5.76));
        }

        if (endGameTime < 2) {
            game.batch.draw(currentFrame, paddle.x, paddle.y, birdSize, birdSize);
        }

        // Draw the row of eggs and powerups.
        for (Circle fallingCircle: circles) {
            if (fallingCircle.powerup == BirdRiseGame.Powerup.doublePoints && fallingCircle.colour == BirdRiseGame.Colour.red) {
                game.batch.draw(Assets.red_powerup, fallingCircle.x, fallingCircle.y, (int) (GameUtilities.X_MAX / 13.1), (int) (GameUtilities.X_MAX / 13.1), 0, 0, 791, 791, false, false);
            } else if (fallingCircle.powerup == BirdRiseGame.Powerup.doublePoints && fallingCircle.colour == BirdRiseGame.Colour.yellow) {
                game.batch.draw(Assets.yellow_powerup, fallingCircle.x, fallingCircle.y, (int) (GameUtilities.X_MAX / 13.1), (int) (GameUtilities.X_MAX / 13.1), 0, 0, 791, 791, false, false);
            } else if (fallingCircle.powerup == BirdRiseGame.Powerup.doublePoints && fallingCircle.colour == BirdRiseGame.Colour.green) {
                game.batch.draw(Assets.green_powerup, fallingCircle.x, fallingCircle.y, (int) (GameUtilities.X_MAX / 13.1), (int) (GameUtilities.X_MAX / 13.1),	0, 0, 791, 791, false, false);
            } else if (fallingCircle.powerup == BirdRiseGame.Powerup.doublePoints && fallingCircle.colour == BirdRiseGame.Colour.blue) {
                game.batch.draw(Assets.blue_powerup, fallingCircle.x, fallingCircle.y, (int) (GameUtilities.X_MAX / 13.1), (int) (GameUtilities.X_MAX / 13.1), 0, 0, 791, 791, false, false);
            } else if (fallingCircle.powerup == BirdRiseGame.Powerup.immunity) {
                game.batch.draw(Assets.star, fallingCircle.x, fallingCircle.y, (int) (GameUtilities.X_MAX / 13.1),(int) (GameUtilities.X_MAX / 13.1), 0, 0, 256, 256, false, false);
            } else if (fallingCircle.colour == BirdRiseGame.Colour.blue) {
                game.batch.draw(Assets.blueCircle, fallingCircle.x, fallingCircle.y, (int) (GameUtilities.X_MAX / 13.1), (int) (GameUtilities.X_MAX / 13.1), 0, 0, 256, 256, false, false);
            } else if (fallingCircle.colour == BirdRiseGame.Colour.green) {
                game.batch.draw(Assets.greenCircle, fallingCircle.x, fallingCircle.y, (int) (GameUtilities.X_MAX / 13.1), (int) (GameUtilities.X_MAX / 13.1), 0, 0, 256, 256, false, false);
            } else if (fallingCircle.colour == BirdRiseGame.Colour.red) {
                game.batch.draw(Assets.redCircle, fallingCircle.x, fallingCircle.y, (int) (GameUtilities.X_MAX / 13.1), (int) (GameUtilities.X_MAX / 13.1),	0, 0, 256, 256, false, false);
            } else if (fallingCircle.colour == BirdRiseGame.Colour.yellow) {
                game.batch.draw(Assets.yellowCircle, fallingCircle.x, fallingCircle.y, (int) (GameUtilities.X_MAX / 13.1), (int) (GameUtilities.X_MAX / 13.1), 0, 0, 256, 256, false, false);
            }
        }

        game.font.draw(game.batch, "Score: " + game.score + "       " + Integer.toString((circlesGatheredInARow / 5) + 1) + "x", 10, GameUtilities.Y_MAX - 10);
        game.font.draw(game.batch, "Lives Left: " + game.livesLeft, GameUtilities.X_MAX - game.font.getBounds("lives Left: 3  ").width, GameUtilities.Y_MAX - 10);

        if (paddle.powerup == BirdRiseGame.Powerup.immunity) {
            showTwinkle = true;
            game.font.draw(game.batch, "Immunity: " + powerupTime, 10, 10 + (game.font.getBounds("Immunity: ").height * 1));
        }

        game.batch.end();
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
                && (screenYCorrected > skipButtonY && screenYCorrected < skipButtonY + (int) (GameUtilities.X_MAX / 8))) {
            skipButtonY = (int) (GameUtilities.Y_MAX / 11.8) - 10;
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        int screenYCorrected = GameUtilities.Y_MAX - screenY;

        if ((screenX > GameUtilities.CENTER_X - (int) (GameUtilities.X_MAX / 9) && screenX < GameUtilities.CENTER_X + (int) (GameUtilities.X_MAX / 9))
                && (screenYCorrected > skipButtonY && screenYCorrected < skipButtonY + (int) (GameUtilities.X_MAX / 8))) {
            skipButtonY = (int) (GameUtilities.Y_MAX / 11.8);
        }
        return true;
    }

    @Override
    public boolean touchDown(float screenX, float screenY, int pointer, int button) {
        int screenYCorrected = (int)(GameUtilities.Y_MAX - screenY);

        if ((screenX > GameUtilities.CENTER_X - (int) (GameUtilities.X_MAX / 9) && screenX < GameUtilities.CENTER_X + (int) (GameUtilities.X_MAX / 9))
                && (screenYCorrected > skipButtonY && screenYCorrected < skipButtonY + (int) (GameUtilities.X_MAX / 8))) {
            skipButtonY = (int) (GameUtilities.Y_MAX / 11.8) - 10;
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
        return true;
    }

    @Override
    public boolean longPress(float x, float y) {
        // TODO Auto-generated method stub
        return false;
    }

    public void movePaddleLeft() {
        if (circlesPerRow == 4) {
            if (paddle.index != 0) {
                paddle.x = (float)(GameUtilities.ROW_COORDINATE_4[paddle.index - 1]);
                paddle.index = paddle.index - 1;
            }
        }
    }

    public void movePaddleRight() {
        if (circlesPerRow == 4) {
            if (paddle.index != 3) {
                paddle.x = (float)(GameUtilities.ROW_COORDINATE_4[paddle.index + 1]);
                paddle.index = paddle.index + 1;
            }
        }
    }

    //Checks if screen is swiped left or right
    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        if(Math.abs(velocityX) > Math.abs(velocityY)){ // left/right swipes.
            if (velocityX > 0) {
                movePaddleRight();
            } else if (velocityX < 0) {
                movePaddleLeft();
            }
        } else { // up/down swipes.
            if (velocityY > 0) {
                quickDrop = true;
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
