package com.kiku.freshair.GetDust;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kiku.freshair.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherViewHolder> {
    ArrayList<Weather> items = new ArrayList<Weather>();

    @NonNull
    @NotNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.weather_item_list, parent, false);
        WeatherViewHolder viewHolder = new WeatherViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull WeatherViewHolder holder, int position) {
        Weather item = items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Weather item){
        items.add(item);
    }

    public ArrayList<Weather> getItems() {
        return items;
    }

    public void setItems(ArrayList<Weather> items) {
        this.items = items;
    }
}
