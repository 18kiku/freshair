package com.kiku.freshair.ui.settings;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kiku.freshair.R;

import org.jetbrains.annotations.NotNull;

public class StationNameSettingViewHolder extends RecyclerView.ViewHolder {
    TextView stationNameSetting;
    public StationNameSettingViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);
        stationNameSetting = itemView.findViewById(R.id.stationnamesetting);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(itemView.getContext(), "id:"+getItemId(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void setItem(StationName item){
        stationNameSetting.setText(item.getStationName());
    }
}
