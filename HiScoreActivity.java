package com.example.joshc.gaia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;

public class HiScoreActivity extends Activity implements View.OnClickListener//, Runnable
{
    ImageView imageAsteroid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hi_score);

        Button buttonMenu = (Button)findViewById(R.id.buttonMenu);
        buttonMenu.setOnClickListener(this);

        imageAsteroid = (ImageView) findViewById(R.id.imageAsteroid);




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
    public void onClick(View view)
    {
        Intent i;
        i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    /*public void UpdateGame()
    {
        if (imageAsteroid.getX() >= imageAsteroid.getWidth())
        {
            TranslateAnimation astroTranslate = new TranslateAnimation(0, 4000, 0, 0);
            astroTranslate.setDuration(10000);
            astroTranslate.setFillAfter(true);
            imageAsteroid.startAnimation(astroTranslate);
        }
        else
        {
            TranslateAnimation astroTranslate = new TranslateAnimation(-4000, -2000, 0, 0);
            astroTranslate.setDuration(10000);
            astroTranslate.setFillAfter(true);
            imageAsteroid.startAnimation(astroTranslate);
        }

    }

    @Override
    public void run() {
        //update function
        UpdateGame();
        //draw function
        //fps controller
    }*/
}
