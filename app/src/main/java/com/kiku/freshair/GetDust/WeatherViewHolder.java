package com.kiku.freshair.GetDust;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kiku.freshair.R;

import org.jetbrains.annotations.NotNull;

public class WeatherViewHolder extends RecyclerView.ViewHolder {
    public TextView fcstTime;
    public TextView fcstValue;
    public ImageView skyImage;
    public TextView reh;
    public WeatherViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        fcstTime = itemView.findViewById(R.id.fcsttime);
        fcstValue = itemView.findViewById(R.id.fcstvalue);
        skyImage = itemView.findViewById(R.id.skyimage);
        reh = itemView.findViewById(R.id.reh);
        fcstTime.setTextColor(Color.WHITE);
        fcstValue.setTextColor(Color.WHITE);
        reh.setTextColor(Color.WHITE);
    }
    public void setItem(Weather item){
        switch (item.getFcstTime()){
            case "0000":
                fcstTime.setText("오전0시");
                break;
            case "0100":
                fcstTime.setText("오전1시");
                break;
            case "0200":
                fcstTime.setText("오전2시");
                break;
            case "0300":
                fcstTime.setText("오전3시");
                break;
            case "0400":
                fcstTime.setText("오전4시");
                break;
            case "0500":
                fcstTime.setText("오전5시");
                break;
            case "0600":
                fcstTime.setText("오전6시");
                break;
            case "0700":
                fcstTime.setText("오전7시");
                break;
            case "0800":
                fcstTime.setText("오전8시");
                break;
            case "0900":
                fcstTime.setText("오전9시");
                break;
            case "1000":
                fcstTime.setText("오전10시");
                break;
            case "1100":
                fcstTime.setText("오전11시");
                break;
            case "1200":
                fcstTime.setText("오전12시");
                break;
            case "1300":
                fcstTime.setText("오후1시");
                break;
            case "1400":
                fcstTime.setText("오후2시");
                break;
            case "1500":
                fcstTime.setText("오후3시");
                break;
            case "1600":
                fcstTime.setText("오후4시");
                break;
            case "1700":
                fcstTime.setText("오후5시");
                break;
            case "1800":
                fcstTime.setText("오후6시");
                break;
            case "1900":
                fcstTime.setText("오후7시");
                break;
            case "2000":
                fcstTime.setText("오후8시");
                break;
            case "2100":
                fcstTime.setText("오후9시");
                break;
            case "2200":
                fcstTime.setText("오후10시");
                break;
            case "2300":
                fcstTime.setText("오후11시");
                break;
            default:
                fcstTime.setText("loading");
                break;
        }
        fcstValue.setText(item.getFcstValue()+"℃");
        switch (item.getSkyImage()){
            case "1":
                skyImage.setImageResource(R.drawable.wgrade1);
                break;
            case "2":
                skyImage.setImageResource(R.drawable.grade2);
                break;
            case "3":
                skyImage.setImageResource(R.drawable.wgrade3);
                break;
            case "4":
                skyImage.setImageResource(R.drawable.wgrade4);
                break;
            case "5":
                skyImage.setImageResource(R.drawable.rain);
                break;
            case "6":
                skyImage.setImageResource(R.drawable.snowrain);
                break;
            case "7":
                skyImage.setImageResource(R.drawable.snow);
                break;
            case "8":
                skyImage.setImageResource(R.drawable.shower);
                break;
            default:
                skyImage.setImageResource(R.drawable.ic_baseline_error_24);
                break;
        }
        reh.setText(item.getReh()+"%");
    }
}
