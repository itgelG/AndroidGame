package com.android.cy.androidmazegame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.android.cy.androidmazegame.GamePad.GamePadMoveCallback;
import com.android.cy.androidmazegame.GamePad.GamePadView;
import com.android.cy.androidmazegame.GameView.GameSurfaceView;
import com.android.cy.androidmazegame.GameView.GameViewCallback;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.startButton);

        button.setOnClickListener((asd)->{
            startActivity(new Intent(this, GameActivity.class));
        });
    }
}
