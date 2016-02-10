package com.rescuebird.game.android;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.audio.Sound;

public class Assets {

    // Circles/Eggs.
    static Texture blueCircle;
    static Texture greenCircle;
    static Texture redCircle;
    static Texture yellowCircle;

    // Background.
    static Texture blueBackground;
    static Texture textBG;

    // Arrows.
    static Texture leftArrow;
    static Texture rightArrow;

    // Power ups
    static Texture star;
    static Texture red_powerup;
    static Texture green_powerup;
    static Texture blue_powerup;
    static Texture yellow_powerup;

    // Buttons
    static Texture playButton;
    static Texture returnHome;
    static Texture replay;
    static Texture backButton;
    static Texture instructions;

    // Animation sheets.
    static Texture redBirdAnimationSheet;
    static Texture greenBirdAnimationSheet;
    static Texture yellowBirdAnimationSheet;
    static Texture blueBirdAnimationSheet;
    static Texture transitionAnimationSheet;
    static Texture twinkleAnimationSheet;

    // Sounds and music.
    static Sound catchSound;
    static Sound dingSound;
    static Sound swish;
    static Sound error;

    // Logo
    static Texture eggCatchLogo;

    // Other assets.
    static Texture cloud;
    static Texture trophy;

    public static void load() {
        // Other assets
        trophy = new Texture(Gdx.files.internal("trophy_new.png"));
        cloud = new Texture(Gdx.files.internal("cloud_new.png"));

        // Logo
        eggCatchLogo = new Texture(Gdx.files.internal("rescuerobinlogocut.png"));

        //load screens
        blueBackground = new Texture(Gdx.files.internal("game_bg_blue_new.png"));
        textBG = new Texture(Gdx.files.internal("textbg_new.png"));

        // Circles and Eggs.
        blueCircle = new Texture(Gdx.files.internal("blue_egg_new.png"));
        greenCircle = new Texture(Gdx.files.internal("green_egg_new.png"));
        redCircle = new Texture(Gdx.files.internal("red_egg_new.png"));
        yellowCircle = new Texture(Gdx.files.internal("yellow_egg_new.png"));

        //learning screen assets
        leftArrow = new Texture(Gdx.files.internal("left_arrow_new.png"));
        rightArrow = new Texture(Gdx.files.internal("right_arrow_new.png"));

        //load power ups
        star = new Texture(Gdx.files.internal("star_powerup_new.png"));
        red_powerup = new Texture(Gdx.files.internal("red_feather_new.png"));
        green_powerup = new Texture(Gdx.files.internal("green_feather_new.png"));
        blue_powerup = new Texture(Gdx.files.internal("blue_feather_new.png"));
        yellow_powerup = new Texture(Gdx.files.internal("yellow_feather_new.png"));

        //load buttons
        playButton = new Texture(Gdx.files.internal("playbtn_new.png"));
        backButton = new Texture(Gdx.files.internal("back_new.png"));
        instructions = new Texture(Gdx.files.internal("instructions_new.png"));
        returnHome = new Texture(Gdx.files.internal("homebtn_new.png"));
        replay = new Texture(Gdx.files.internal("replaybtn_new.png"));

        //load sounds and music
        catchSound = Gdx.audio.newSound(Gdx.files.internal("Blop.mp3"));
        dingSound = Gdx.audio.newSound(Gdx.files.internal("Ding.wav"));
        swish = Gdx.audio.newSound(Gdx.files.internal("swish_cut.mp3"));
        error = Gdx.audio.newSound(Gdx.files.internal("bamboo.mp3"));

        //load animation sheets
        redBirdAnimationSheet = new Texture(Gdx.files.internal("red_bird_sprite_new.png"));
        greenBirdAnimationSheet = new Texture(Gdx.files.internal("green_bird_sprite_new.png"));
        yellowBirdAnimationSheet = new Texture(Gdx.files.internal("yellow_bird_sprite_new.png"));
        blueBirdAnimationSheet = new Texture(Gdx.files.internal("blue_bird_sprite_new.png"));
        transitionAnimationSheet = new Texture(Gdx.files.internal("transitionAnimation_new.png"));
        twinkleAnimationSheet = new Texture(Gdx.files.internal("twinkleAnimation.gif"));
    }

    public static void dispose() {

        // Circles/Eggs.
        blueCircle.dispose();
        greenCircle.dispose();
        redCircle.dispose();
        yellowCircle.dispose();

        // Background.
        blueBackground.dispose();
        textBG.dispose();

        // Arrows.
        leftArrow.dispose();
        rightArrow.dispose();

        // Power ups
        star.dispose();
        red_powerup.dispose();
        green_powerup.dispose();
        blue_powerup.dispose();
        yellow_powerup.dispose();

        // Buttons
        playButton.dispose();
        returnHome.dispose();
        replay.dispose();
        backButton.dispose();
        instructions.dispose();

        // Animation sheets.
        redBirdAnimationSheet.dispose();
        greenBirdAnimationSheet.dispose();
        yellowBirdAnimationSheet.dispose();
        blueBirdAnimationSheet.dispose();
        transitionAnimationSheet.dispose();
        twinkleAnimationSheet.dispose();

        // Sounds and music.
        catchSound.dispose();
        dingSound.dispose();
        swish.dispose();
        error.dispose();

        // Logo
        eggCatchLogo.dispose();

        // Other assets.
        cloud.dispose();
        trophy.dispose();
    }
}