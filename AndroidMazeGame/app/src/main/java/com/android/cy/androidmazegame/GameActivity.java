package com.android.cy.androidmazegame;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import com.android.cy.androidmazegame.GamePad.GamePadMoveCallback;
import com.android.cy.androidmazegame.GamePad.GamePadView;
import com.android.cy.androidmazegame.GameView.GameSurfaceView;
import com.android.cy.androidmazegame.GameView.GameViewCallback;
import com.android.cy.androidmazegame.Models.Map;
import com.android.cy.androidmazegame.Models.Session;

import io.realm.Realm;

public class GameActivity extends Activity {

    private GameSurfaceView mGameView;
    private GamePadView mGamePadView;
    private int mapId;
    private final Realm database = Realm.getDefaultInstance();
    boolean isFinish = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mapId = getIntent().getIntExtra("mapId",1);
        Log.e("TAG", "onCreate: " +mapId );
        // Game view
        mGameView = new GameSurfaceView(this, mapId);
        mGameView.setGameViewCallback(new GameViewCallback() {
            @Override
            public void onGameStart() {
                mGamePadView.startTimer();
            }
        });
        setContentView(mGameView);


        // Fake empty container layout
        RelativeLayout lContainerLayout = new RelativeLayout(this);
        lContainerLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        // Custom view
        mGamePadView = new GamePadView(this);
        mGamePadView.setMoveCallback(new GamePadMoveCallback() {
            @Override
            public void onMove(float x, float y) {
                mGameView.onCameraTargetUpdate(x, y);
            }

            @Override
            public void onKeyDown(int direction) {
                mGameView.onCharacterKeyDown(direction);
            }

            @Override
            public void onKeyUp() {
                mGameView.onCharacterKeyUp();
            }
        });

        RelativeLayout.LayoutParams lButtonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lButtonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mGamePadView.setLayoutParams(lButtonParams);
        lContainerLayout.addView(mGamePadView);

        // Adding full screen container
        addContentView(lContainerLayout, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

    }


    public void showWinnerModal() {
        int seconds = mGamePadView.getTimer();

        mGamePadView.isFinish = true;

        if (!isFinish) {
            isFinish = true;
            runOnUiThread(() -> {
                WinnerModal winnerModal = new WinnerModal(GameActivity.this);
                winnerModal.setOnDismissListener(dialog -> {
                    Number currentIdNum = database.where(Session.class).max("id");

                    int nextId;
                    if (currentIdNum == null) {
                        nextId = 1;
                    } else {
                        nextId = currentIdNum.intValue() + 1;
                    }

                    database.beginTransaction();

                    Session session = new Session();
                    session.setStartDate(mGamePadView.startDate);
                    session.setDurationTime(seconds);
                    session.setMap(mapId);
                    session.setId(nextId);

                    Log.e("TAG", "showWinnerModal: " +session.toString() );

                    database.insertOrUpdate(session);
                    database.commitTransaction();
                    GameActivity.this.finish();
                });

                winnerModal.show();
            });

        }

    }
}