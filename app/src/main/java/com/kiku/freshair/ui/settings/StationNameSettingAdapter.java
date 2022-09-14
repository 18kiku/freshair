package com.kiku.freshair.ui.settings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kiku.freshair.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class StationNameSettingAdapter extends RecyclerView.Adapter<StationNameSettingViewHolder> {
    ArrayList<StationName> items = new ArrayList<StationName>();

    @NonNull
    @NotNull
    @Override
    public StationNameSettingViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.stationnamesetting_item_list, parent, false);
        StationNameSettingViewHolder viewHolder = new StationNameSettingViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull StationNameSettingViewHolder holder, int position) {
        StationName item = items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(StationName item){
        items.add(item);
    }

    public void removeItem(int position){
        items.remove(position);
    }

    public ArrayList<StationName> getItems(){
        return items;
    }

    public void setItems(ArrayList<StationName> items){
        this.items = items;
    }
}
