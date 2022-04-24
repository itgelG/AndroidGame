package com.android.cy.androidmazegame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.android.cy.androidmazegame.Models.Map;
import com.android.cy.androidmazegame.Models.Session;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class HistoryActivity extends AppCompatActivity {
    private final String TAG = getClass().getName();

    private final List<Session> sessions = new ArrayList<>();
    private final Realm database = Realm.getDefaultInstance();

    private LinearLayout mapContainer;
    private SessionAdapter sessionAdapter;
    private RealmResults<Map> maps;
    private RecyclerView recyclerView;
    private int selectedMapId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        mapContainer = findViewById(R.id.mapContainer);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        setTitle("Game Session Histories");

        recyclerView = findViewById(R.id.historyRecyclerView);

        sessionAdapter = new SessionAdapter(this, sessions);
        recyclerView.setAdapter(sessionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        initMaps();
    }

    private void initMaps() {
        maps = database.where(Map.class).findAll();

        if (maps.size() > 0) {
            selectedMapId = maps.get(0).getId();
        }

        for (Map map :
                maps) {
            Button button = new Button(this);
            button.setText(map.getName());

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(20, 0, 0, 0);

            switch (map.getLevel()) {
                case 2:
                    button.setLayoutParams(params);
                    break;
                case 3:
                    button.setLayoutParams(params);
                    break;
                default:
                    break;
            }

            button.setTag(map.getId());

            button.setOnClickListener(v -> {
                getHistories(map);
            });

            mapContainer.addView(button);
        }

        if (maps.size() > 0) {
            getHistories(maps.get(0));
        }
    }

    private void getHistories(Map map) {
        selectedMapId = map.getId();

        for (int i = 0; i < mapContainer.getChildCount(); i++) {
            if (((int) mapContainer.getChildAt(i).getTag()) == selectedMapId) {
                ((Button) mapContainer.getChildAt(i)).setTextColor(Color.rgb(100, 2, 38));
            } else {
                ((Button) mapContainer.getChildAt(i)).setTextColor(Color.BLACK);
            }
        }

        RealmResults<Session> sessionRealmResults = database
                .where(Session.class)
                .equalTo("map", map.getId())
                .sort("durationTime")
                .findAll();

        sessions.clear();
        sessions.addAll(sessionRealmResults);
        sessionAdapter.notifyDataSetChanged();
    }

    public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionViewHolder> {
        private final Context context;
        private final List<Session> sessions;

        public SessionAdapter(Context context, List<Session> sessions) {
            this.context = context;
            this.sessions = sessions;
        }

        @Override
        public SessionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SessionViewHolder(
                    LayoutInflater
                            .from(context)
                            .inflate(R.layout.item_session, parent, false)
            );
        }

        @Override
        public void onBindViewHolder(SessionViewHolder holder, int position) {
            holder.bind(sessions.get(position), position);

        }

        @Override
        public int getItemCount() {
            return sessions.size();
        }


        public class SessionViewHolder extends RecyclerView.ViewHolder {

            TextView duration, date;

            public SessionViewHolder(View itemView) {
                super(itemView);
                date = itemView.findViewById(R.id.date);
                duration = itemView.findViewById(R.id.duration);
            }

            public void bind(Session session, int index) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                String strDt = simpleDateFormat.format(session.getStartDate());
                date.setText(strDt);
                String stringBuilder = session.getDurationTime() +
                        " sec";
                duration.setText(stringBuilder);
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}