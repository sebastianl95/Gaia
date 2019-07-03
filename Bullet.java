package com.example.joshc.gaia;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;

public class Bullet {

    private Context context;

    public Bitmap bullet;
    public int frameHeight;
    public int frameWidth;
    public Rect rectToBeDrawn;
    public Point bulletPosition;
    public boolean isMoving;
    public int velocity;


    public Bullet(Context current, int positionX, int positionY)
    {
        context = current;
        bullet = BitmapFactory.decodeResource(context.getResources(), R.drawable.satbullet);
        frameWidth = bullet.getWidth();
        frameHeight = bullet.getHeight();
        bulletPosition = new Point();
        bulletPosition.x = positionX;
        bulletPosition.y = positionY;
        isMoving = false;
        velocity = 10;
    }
}
