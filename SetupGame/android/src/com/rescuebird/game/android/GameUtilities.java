package com.rescuebird.game.android;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import java.util.ArrayList;

public class GameUtilities {

    static final int X_MIN = 0;
    static final int X_MAX = Gdx.graphics.getWidth();
    static final int Y_MIN = 0;
    static final int Y_MAX = Gdx.graphics.getHeight();
    static final int CENTER_X = (int)(X_MIN + X_MAX)/2;
    static final int CENTER_Y = (int)(Y_MIN + Y_MAX)/2;

    // Coordinates for circles in a row.
    static final int [] ROW_COORDINATE_3 = {(int)(X_MAX * 0.10), CENTER_X - 50, (int)(X_MAX * 0.80)};
    static final int [] ROW_COORDINATE_4 = {(int)(X_MAX * 0.10), (int)(X_MAX * 0.35), (int)(X_MAX * 0.60), (int)(X_MAX * 0.85)};

    static final int MAX_LIVES = 3;
    static final int GAME_TIME = 0;
    static final int POWERUP_TIME = 10;


    public static ArrayList<Cloud> spawnCloud(Boolean spawn, Boolean bottom, ArrayList<Cloud> clouds) {
        int randomNum = MathUtils.random(0, 3);

        // Create a new cloud and add it to the array.
        if (randomNum == 1 || spawn) {
            Cloud cloud = new Cloud();
            randomNum = MathUtils.random(0, 2);

            // Specify the cloud's x coordinates.
            if (randomNum == 0) {
                cloud.x = GameUtilities.X_MAX / 7;
            } else if (randomNum == 1) {
                cloud.x = GameUtilities.X_MAX / 2;
            } else {
                cloud.x = GameUtilities.X_MAX * 2 / 3;
            }

            randomNum = MathUtils.random(0, 1);

            // Specify the cloud's drop speed.
            if (randomNum == 0) {
                cloud.dropSpeed = (int) ((GameUtilities.X_MAX / 2) / 7);
            } else {
                cloud.dropSpeed = (int) ((GameUtilities.X_MAX / 2) / 5);
            }

            if (bottom) {
                randomNum = MathUtils.random(0, 1);
                if (randomNum == 0) {
                    cloud.y = -1 * (int) (GameUtilities.Y_MAX / 11.8);
                } else {
                    cloud.y = -1 * (int) (GameUtilities.Y_MAX / 7.8);
                }
            } else {
                cloud.y = GameUtilities.Y_MAX;
            }

            cloud.width = GameUtilities.X_MAX / 7;
            cloud.height = GameUtilities.X_MAX / 7;
            clouds.add(cloud);
        }

        return clouds;
    }

}