package com.example.freshair.station_material;

import java.util.ArrayList;

public class Body {
    private ArrayList<StationName> items = new ArrayList<StationName>();

    public ArrayList<StationName> getItems() {
        return items;
    }

    public void setItems(ArrayList<StationName> items) {
        this.items = items;
    }
}
