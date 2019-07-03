package com.example.joshc.gaia;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class Satellite {

    private Context context;
    private int health;

    public Bitmap satellite;
    public int frameHeight;
    public int frameWidth;
    public int numFrames;
    public int frameNumber;
    public Rect rectToBeDrawn;
    public boolean isShooting;
    public boolean ready;
    public boolean isDead;




    public Satellite(Context current)
    {
        this.context = current;
        satellite = BitmapFactory.decodeResource(context.getResources(), R.drawable.sat);
        numFrames = 2;
        frameWidth = satellite.getWidth() / numFrames;
        frameHeight = satellite.getHeight();
        health = 100;
        isShooting = false;
        ready = true;
        isDead = false;
    }

    public int getHealth() {
        return health;
    }

    public void takeDamage(int damage){
        health = health - damage;
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


}
