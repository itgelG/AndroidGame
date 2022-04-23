package com.android.cy.androidmazegame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.android.cy.androidmazegame.GamePad.GamePadMoveCallback;
import com.android.cy.androidmazegame.GamePad.GamePadView;
import com.android.cy.androidmazegame.GameView.GameSurfaceView;
import com.android.cy.androidmazegame.GameView.GameViewCallback;
import com.android.cy.androidmazegame.Models.Map;
import com.android.cy.androidmazegame.Models.Session;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends Activity {

    private final Realm database = Realm.getDefaultInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonStart = findViewById(R.id.startButton);
        Button buttonHistory = findViewById(R.id.historyButton);

        long mapSize = database.where(Map.class).count();


        if (mapSize == 0) {
            database.beginTransaction();
            database.insertOrUpdate(new Map(1, "World", 1));
            database.insertOrUpdate(new Map(2, "Galaxy", 2));
            database.insertOrUpdate(new Map(3, "Universe", 3));
            database.commitTransaction();
        }



        buttonStart.setOnClickListener((asd) -> {
            runOnUiThread(() -> {
                ChooseMapModal modal = new ChooseMapModal(MainActivity.this);
                modal.setOnDismissListener(dialog -> {
                    Intent intent = new Intent(this, GameActivity.class);
                    intent.putExtra("mapId", modal.chosenMapId);

                    startActivity(intent);
                });

                modal.show();
            });


        });

        buttonHistory.setOnClickListener((asd) -> {
            startActivity(new Intent(this, HistoryActivity.class));
        });


    }
}
