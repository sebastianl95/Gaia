package com.example.joshc.gaia;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.util.Random;


public class GameActivity extends Activity  {

    Context context;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String dataName = "MyData";
    String intName = "MyInt";
    int defaultInt = 0;
    int highScore;

    Canvas canvas;
    GaiaGameView gaiaGameView;
    Paint paint;

    Bitmap background;
    Bitmap earth;

    Display display;
    Point size;
    int screenWidth;
    int screenHeight;
    int screenCenter;

    boolean satMovingLeft;
    boolean satMovingRight;
    Point satPosition;

    public Bullet[] satBullets;

    public ScoutShip[] scoutShips;


    long lastFrameTime;
    int fps;
    int score;
    int lastScore;

    Satellite satellite;

    CountDownTimer gameOverTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gaiaGameView = new GaiaGameView(this);
        setContentView(gaiaGameView);

        prefs = getSharedPreferences(dataName, MODE_PRIVATE);
        editor = prefs.edit();
        highScore = prefs.getInt(intName, defaultInt);



        context = this;

        display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        screenCenter = screenWidth / 2;

        //set our satellites position variables
        satPosition = new Point();
        satPosition.x = screenWidth / 2;
        satPosition.y = screenHeight - 180;

        //reset our score
        score = 0;

        //set our background bitmaps
        background = BitmapFactory.decodeResource(getResources(), R.drawable.spacebackground);
        earth = BitmapFactory.decodeResource(getResources(), R.drawable.earth1);

        //create our satellite
        satellite = new Satellite(this);

        //create our array of bullets for the satellite to shoot
        satBullets = new Bullet[20];
        for(int i = 0; i < satBullets.length; i++)
        {
            satBullets[i] = new Bullet(this,satPosition.x, satPosition.y);
        }

        //create our array of enemy scoutShips to attack the earth
        scoutShips = new ScoutShip[4];
        for(int i = 0; i < scoutShips.length; i++)
        {
            Random randomNumber = new Random();
            int startX = randomNumber.nextInt(screenWidth - 64);//set a random x value to start the enemy ship at
            int startY = randomNumber.nextInt(1000);//random number to subtract from 0 to ensure the scoutShips y value starts off screen
            //pass in our random values to the constructor
            scoutShips[i] = new ScoutShip(this, startX + 64, 0 - startY - 64);
        }

        gameOverTimer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Intent i;
                i = new Intent(context, MainActivity.class);
                startActivity(i);
            }
        };
    }

    class GaiaGameView extends SurfaceView implements Runnable {

        Thread ourThread = null;
        SurfaceHolder ourHolder;
        volatile boolean playingGaia;

        public GaiaGameView(Context context) {
            super(context);
            ourHolder = getHolder();
        }

        @Override
        public void run() {
            while (playingGaia) {
                update();
                draw();
                controlFPS();
            }
        }

        public void update()
        {
            //move the satellite
            if (satMovingRight)
            {
                satPosition.x = satPosition.x + 10;

                if(satPosition.x < screenCenter)
                {
                   satPosition.y = satPosition.y - 2;
                }else{
                    satPosition.y = satPosition.y + 2;
                }
            }
            if (satMovingLeft)
            {
                satPosition.x = satPosition.x - 10;

                if(satPosition.x > screenCenter)
                {
                    satPosition.y = satPosition.y - 2;
                }else{
                    satPosition.y = satPosition.y + 2;
                }
            }

            //keep the satellite from dropping as it moves back and forth
            if(satPosition.x == screenCenter)
            {
                satPosition.y = screenHeight - 180;
            }

            //did the satellite hit the sides of the screen
            if(satMovingLeft && satPosition.x <= 0)
            {
                satPosition.x = 0;
                satPosition.y = satPosition.y - 2;
            }
            if(satMovingRight && satPosition.x >= screenWidth)
            {
                satPosition.x = screenWidth;
                satPosition.y = satPosition.y - 2;
            }

            //update the unfired bullets to move with the satellite
            for(int i = 0; i < satBullets.length; i++)
            {
                if(!satBullets[i].isMoving)
                {
                    satBullets[i].bulletPosition.x = satPosition.x;
                    satBullets[i].bulletPosition.y = satPosition.y;
                }
            }

            //when shootEnemy is called, check for a bullet that isn't fired and set it to start moving
            if(satellite.isShooting)
            {
                for(int i = 0; i < satBullets.length; i++)
                {
                    if(!satBullets[i].isMoving)
                    {
                        satBullets[i].isMoving = true;
                        satellite.isShooting = false;
                        break;
                    }
                }
            }

            //update the position of the bullets that are moving
            for(int i = 0; i < satBullets.length; i++)
            {
                if(satBullets[i].isMoving)
                {
                    satBullets[i].bulletPosition.y = satBullets[i].bulletPosition.y - satBullets[i].velocity;
                    //reset the bullets when they move off screen
                    if(satBullets[i].bulletPosition.y < 0 - satBullets[i].frameHeight)
                    {
                        satBullets[i].isMoving = false;
                        satBullets[i].bulletPosition.y = satPosition.y;
                    }
                }
            }

            //update the enemy scoutShips position
            for(int i = 0; i < scoutShips.length; i++)
            {
                scoutShips[i].shipPosition.y = scoutShips[i].shipPosition.y + scoutShips[i].velocity;
                //check if the ship reached the earth
                if(scoutShips[i].shipPosition.y > screenHeight + scoutShips[i].frameHeight)
                {
                    //if it has, reset the ship's position and deal damage
                    Random randomNumber = new Random();
                    scoutShips[i].shipPosition.x = randomNumber.nextInt(screenWidth - scoutShips[i].frameWidth);
                    scoutShips[i].shipPosition.y = 0 - randomNumber.nextInt(1000);
                    scoutShips[i].dealDamage(satellite);
                }
            }

            //check if a bullet hit a ship
            for(int i = 0; i < satBullets.length; i++)
            {
                for(int j = 0; j < scoutShips.length; j++)
                {
                    //did the bullet hit the ship's y value
                    if((satBullets[i].bulletPosition.y - satBullets[i].frameHeight / 2) <= (scoutShips[j].shipPosition.y + scoutShips[j].frameHeight / 2) && satBullets[i].isMoving)
                    {
                        int bulletX = satBullets[i].bulletPosition.x;
                        int shipX = scoutShips[j].shipPosition.x;
                        int halfBullet = satBullets[i].frameWidth / 2;
                        int halfShip = scoutShips[j].frameWidth / 2;

                        //did the bullet hit the ship's x value
                        if(bulletX + halfBullet > (shipX - halfShip) && bulletX - halfBullet < (shipX + halfShip))
                        {
                            //reset the bullet
                            satBullets[i].isMoving = false;
                            satBullets[i].bulletPosition.y = satPosition.y;
                            //damage the ship
                            scoutShips[j].takeDamage(50);
                            //check if the ship needs to be destroyed/reset
                            if(scoutShips[j].getHealth() <= 0)
                            {
                                Random randomNumber = new Random();
                                scoutShips[j].shipPosition.x = randomNumber.nextInt(screenWidth - scoutShips[i].frameWidth);
                                scoutShips[j].shipPosition.y = 0 - randomNumber.nextInt(1000);
                                scoutShips[j].setHealth(100);
                                score = score + 100;
                            }
                        }
                    }

                }
            }

            //increase the ships velocity
            if(score/1000 > lastScore)
            {
                for(int i = 0; i < scoutShips.length; i++)
                {
                    scoutShips[i]. velocity++;
                }
                lastScore = score/1000;
            }

            if(satellite.getHealth() <= 0)
            {
                satellite.isDead = true;
                if(score > highScore)
                {
                    highScore = score;
                    editor.putInt(intName, highScore);
                    editor.commit();
                }
            }

            //update the satellite sprite
            satellite.updateSprite();
            //update the ship sprites
            for(int i = 0; i < scoutShips.length; i++)
            {
                scoutShips[i].updateSprite();
            }
        }

        public void draw(){
            if (ourHolder.getSurface().isValid())
                {
                    canvas = ourHolder.lockCanvas();
                    canvas.drawBitmap(background, 0, 0, null);//draw the background
                    canvas.drawBitmap(earth, screenCenter - 1180, screenHeight - 175, null);//draw the earth
                    Rect destRect = new Rect(satPosition.x - (satellite.frameWidth/2), satPosition.y - (satellite.frameHeight/2),
                            satPosition.x + (satellite.frameWidth/2), satPosition.y + (satellite.frameHeight/2));//set where to draw the satellite
                    canvas.drawBitmap(satellite.satellite, satellite.rectToBeDrawn, destRect, null);//draw the satellite

                    //draw all the scoutShips
                    for(int i = 0; i < scoutShips.length; i++)
                    {
                        destRect = new Rect(scoutShips[i].shipPosition.x - (scoutShips[i].frameWidth/2), scoutShips[i].shipPosition.y - (scoutShips[i].frameHeight/2),
                                scoutShips[i].shipPosition.x + (scoutShips[i].frameWidth/2), scoutShips[i].shipPosition.y + (scoutShips[i].frameHeight/2));
                        canvas.drawBitmap(scoutShips[i].ship, scoutShips[i].rectToBeDrawn, destRect, null);
                    }
                    //draw all the bullets
                    for(int i = 0; i < satBullets.length; i ++)
                    {
                        if(satBullets[i].isMoving)
                        {
                            destRect = new Rect(satBullets[i].bulletPosition.x - (satBullets[i].frameWidth/2), satBullets[i].bulletPosition.y - (satBullets[i].frameHeight/2),
                                    satBullets[i].bulletPosition.x + (satBullets[i].frameWidth/2), satBullets[i].bulletPosition.y + (satBullets[i].frameHeight/2));
                            canvas.drawBitmap(satBullets[i].bullet, satBullets[i].rectToBeDrawn, destRect, null);
                        }
                    }

                    //print out the player's health
                    paint = new Paint();
                    paint.setColor(Color.argb(255, 255, 255, 255));
                    paint.setTextSize(45);
                    canvas.drawText("Health: " + satellite.getHealth(), 20, 60, paint);
                    canvas.drawText("Score: " + score, screenWidth - 300, 60, paint);

                    //game over
                    if(satellite.isDead)
                    {
                        //canvas.drawColor(Color.BLACK);
                        paint.setTextSize(100);
                        canvas.drawText("GAME OVER", 250, 960, paint);
                        paint.setTextSize(80);
                        canvas.drawText("Score: " + score, 250, 1060, paint);
                        canvas.drawText("High Score: " + highScore, 250, 1160, paint);
                        gameOverTimer.start();
                        playingGaia = false;
                    }
                    ourHolder.unlockCanvasAndPost(canvas);
                }
            }

        public void controlFPS() {
            long timeThisFrame = (System.currentTimeMillis() - lastFrameTime);
            long timeToSleep = 15 - timeThisFrame;
            if (timeThisFrame > 0) {
                fps = (int) (1000 / timeThisFrame);
            }
            if (timeToSleep > 0) {

                try {
                    ourThread.sleep(timeToSleep);
                } catch (InterruptedException e) {
                }

            }

            lastFrameTime = System.currentTimeMillis();
        }

        public void pause() {
            playingGaia = false;
            try {
                ourThread.join();
            } catch (InterruptedException e){
            }
        }

        public void resume() {
            playingGaia = true;
            ourThread = new Thread(this);
            ourThread.start();
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {

            switch (motionEvent.getAction() & motionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    if(!satellite.isDead) {
                        if (motionEvent.getX() >= (screenWidth / 2 + 100)) {
                            satMovingRight = true;
                            satMovingLeft = false;
                        } else if (motionEvent.getX() <= (screenWidth / 2 - 100)) {
                            satMovingLeft = true;
                            satMovingRight = false;
                        } else {
                            satellite.isShooting = true;
                        }
                        break;
                    }

                case MotionEvent.ACTION_UP:
                    if(!satellite.isDead) {
                        satMovingRight = false;
                        satMovingLeft = false;
                        if (satellite.isShooting) {
                            satellite.isShooting = false;
                        }
                        break;
                    }
            }
            return true;
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    protected void onStop() {
        super.onStop();
        while (true) {
            gaiaGameView.pause();
            break;
        }

        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gaiaGameView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gaiaGameView.pause();
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }


}
