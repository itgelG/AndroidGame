package com.android.cy.androidmazegame;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.android.cy.androidmazegame.Models.Map;

import io.realm.Realm;
import io.realm.RealmResults;

public class ChooseMapModal  extends Dialog {

    public Activity activity;
    private final Realm database = Realm.getDefaultInstance();

    public ChooseMapModal(@NonNull Activity activity) {
        super(activity);

        this.activity = activity;

    }

    public int chosenMapId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_map);

        LinearLayout container = findViewById(R.id.chooserContainer);

        RealmResults<Map> realmResults = database.where(Map.class).findAll();

        for (Map map:
             realmResults) {
            Button button = new Button(activity);
            button.setText(map.getName() + " - Level " + map.getLevel());
            button.setOnClickListener(v -> {
                chosenMapId = map.getId();
                dismiss();
            });
            container.addView(button);
        }
    }

}
