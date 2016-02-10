package com.rescuebird.game.android;

import com.badlogic.gdx.math.Rectangle;

public class Paddle extends Rectangle {

    public int index;
    public BirdRiseGame.Colour colour;
    public BirdRiseGame.Powerup powerup;

    public Paddle() {
        this.index = 1;
        this.x = (float)(GameUtilities.ROW_COORDINATE_4[this.index]);
        this.y = (float)(GameUtilities.Y_MAX / 9.83);
        this.width = 100;
        this.height = 100;
        this.powerup = BirdRiseGame.Powerup.none;
    }

}