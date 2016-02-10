package com.rescuebird.game.android;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.birdrise.game.AdsController;

public class BirdRiseGame extends Game {

    //Screens
    MainMenuScreen mainMenuScreen;
    GameScreen gameScreen;
    LearningScreen learningScreen;
    GameOverScreen gameOverScreen;

    public AdsController adsController;

    //Game variables
    SpriteBatch batch;
    BitmapFont font;
    OrthographicCamera camera;
    int gameTime = GameUtilities.GAME_TIME;
    int livesLeft = GameUtilities.MAX_LIVES;
    int circlesGathered = 0;
    long score = 0l;
    enum Colour {blue, green, red, yellow};
    enum Powerup {doublePoints, immunity, none};
    long highscore = 0l;
    int fontSize;
    int highscoreWidth;
    int firstLearningWidth;
    int secondLearningWidth;
    int thirdLearningWidth;
    int firstScoreWidth;
    int secondScoreWidth;
    int thirdScoreWidth;

    public BirdRiseGame(AdsController adsController) {
        this.adsController = adsController;
    }

    @Override
    public void create() {
        // Set and update camera.
        setUpCamera();

        // Create new sprite batch.
        batch = new SpriteBatch();

        // Load assets.
        Assets.load();

        // Initialize screens.
        initializeScreens();

        // Create the game font.
        createGameFont();

        // Get the widths of all the written text in game.
        getTextWidths();

        FileHandle file = Gdx.files.local("data/rescue_bird_highscores.txt");

        // If file doesnt exist, maybe fetch from db containing user names
        if(file.exists()){
            highscore = Long.valueOf(file.readString());
        }

        if (highscore == 0) {
            adsController.hideBannerAd();
        } else {
            adsController.showBannerAd();
        }

        // Enter main menu screen.
        this.setScreen(mainMenuScreen);
    }

    public void setUpCamera() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 1200);
        camera.update();
    }

    public void initializeScreens() {
        mainMenuScreen = new MainMenuScreen(this);
        gameScreen = new GameScreen(this);
        gameOverScreen = new GameOverScreen(this);
        learningScreen = new LearningScreen(this);
    }

    public void getTextWidths() {
        firstLearningWidth = (int)(font.getBounds("Swipe Left or Right to Move").width);
        secondLearningWidth = (int)(font.getBounds("Rescue Eggs with Same Colour as Robin").width);
        thirdLearningWidth = (int)(font.getBounds("Swipe Down to Speed the Drop").width);
        firstScoreWidth = (int)(font.getBounds("+ 50 points").width);
        secondScoreWidth = (int)(font.getBounds("+ 100 points").width);
        thirdScoreWidth = (int)(font.getBounds("Invincible for 10 seconds").width);
    }

    public void createGameFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("OpenSans-CondLight.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontSize = (int) (GameUtilities.X_MAX / 14.4); // Measured in pixels.
        parameter.size = fontSize;
        font = generator.generateFont(parameter);
        font.setColor(Color.BLACK);

        // Dispose to avoid memory leaks.
        generator.dispose();
    }

    @Override
    public void dispose() {
        Assets.dispose();
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}