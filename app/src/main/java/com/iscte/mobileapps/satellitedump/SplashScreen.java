package com.iscte.mobileapps.satellitedump;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


public class SplashScreen extends AppCompatActivity {

    private TextView tv;
    private ImageView iv;

    Animation fromBottomAnim;
    Animation scaleAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        tv = (TextView) findViewById(R.id.loadingText);
        iv = (ImageView) findViewById(R.id.Logo);

        fromBottomAnim = AnimationUtils.loadAnimation(this, R.anim.splashtransition);
        scaleAnim = AnimationUtils.loadAnimation(this, R.anim.splashscale);

        tv.startAnimation(fromBottomAnim);
        iv.startAnimation(scaleAnim);

        final Intent main_activity_intent = new Intent(this, MainActivity.class);

        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    startActivity(main_activity_intent);
                    finish();
                }
            }
        };

        timer.start();
    }
}
