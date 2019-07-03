package com.example.joshc.gaia;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;

public class ScoutShip {

    private Context context;
    private int health;

    public Bitmap ship;
    public int frameHeight;
    public int frameWidth;
    public int numFrames;
    public int frameNumber;
    public Rect rectToBeDrawn;
    public Point shipPosition;
    public int velocity;

    public ScoutShip(Context current, int positionX, int positionY)
    {
        context = current;
        ship = BitmapFactory.decodeResource(context.getResources(), R.drawable.scoutship);
        numFrames = 4;
        frameHeight = ship.getHeight();
        frameWidth = ship.getWidth()/numFrames;
        shipPosition = new Point();
        shipPosition.x = positionX;
        shipPosition.y = positionY;
        velocity = 2;
        health = 100;
    }

    void updateSprite() {
        //frame to draw
        rectToBeDrawn = new Rect((frameNumber * frameWidth) - 1, 0, (frameNumber * frameWidth + frameWidth) - 1, frameHeight);

        //next frame
        frameNumber++;

        //reset when out of frames
        if (frameNumber >= numFrames) {
            frameNumber = 0;//back to the first frame
        }
    }

    void dealDamage(Satellite target)
    {
        target.takeDamage(5);
    }

    void takeDamage(int damage)
    {
        health = health - damage;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int newHealth) {
        health = newHealth;
    }
}
