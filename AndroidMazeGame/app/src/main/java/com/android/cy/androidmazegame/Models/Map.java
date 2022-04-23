package com.android.cy.androidmazegame.Models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Map extends RealmObject {
    @PrimaryKey
    private int id;

    private String name;
    private int level;

    public Map() {

    }

    public Map(int id, String name, int level) {
        this.id = id;
        this.name = name;
        this.level = level;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
