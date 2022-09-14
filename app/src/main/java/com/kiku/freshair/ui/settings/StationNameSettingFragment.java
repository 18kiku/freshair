package com.kiku.freshair.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kiku.freshair.R;
import com.kiku.freshair.databinding.FragmentStationNameSettingBinding;
import com.kiku.freshair.db.StationNameRealmObject;

import org.jetbrains.annotations.NotNull;

import io.realm.Realm;
import io.realm.RealmResults;

public class StationNameSettingFragment extends Fragment {

    private FragmentStationNameSettingBinding binding;
    private Realm realm;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private String mainStation;
    View view;

    private TextView tMainStation;

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //inflater.inflate(R.menu.main,menu);
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = preferences.edit();


    }

    private void initializeValue() {
        mainStation = preferences.getString("station","정릉로");
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStationNameSettingBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        setHasOptionsMenu(true);
        initializeValue();
        tMainStation = (TextView)view.findViewById(R.id.main);
        tMainStation.setText(mainStation);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview_stationnamesetting);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        StationNameSettingAdapter adapter = new StationNameSettingAdapter();

        realm = Realm.getDefaultInstance();

        RealmResults<StationNameRealmObject> results = realm.where(StationNameRealmObject.class).findAll();
        for (StationNameRealmObject stationNameRealmObject : results){
            adapter.addItem(new StationName(stationNameRealmObject.getStationName()));
        }
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                Log.d("aaa","p"+position);
                realm.beginTransaction();
                realm.where(StationNameRealmObject.class).findAll().get(position).deleteFromRealm();
                realm.commitTransaction();
                adapter.removeItem(position);
                adapter.notifyItemRemoved(position);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        return view;
    }

    public void saveStationName(String stationName){
        realm.beginTransaction();
        StationNameRealmObject stationNameRealmObject = realm.createObject(StationNameRealmObject.class);
        stationNameRealmObject.setStationName(stationName);

        realm.commitTransaction();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        realm.close();
    }
}