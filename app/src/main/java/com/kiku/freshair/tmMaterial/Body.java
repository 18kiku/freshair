package com.kiku.freshair.tmMaterial;

import java.util.ArrayList;

public class Body {
    private String totalCount;

    private ArrayList<TM> items = new ArrayList<TM>();

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public ArrayList<TM> getItems() {
        return items;
    }

    public void setItems(ArrayList<TM> items) {
        this.items = items;
    }

}
