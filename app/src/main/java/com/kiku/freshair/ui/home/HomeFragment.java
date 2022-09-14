package com.kiku.freshair.ui.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.RequestQueue;
import com.kiku.freshair.GetDust.GetDustFragment;
import com.kiku.freshair.R;
import com.kiku.freshair.WindowActivity;
import com.kiku.freshair.data.GetStationRepository;
import com.kiku.freshair.data.GetTMRepository;
import com.kiku.freshair.data.StationRepository;
import com.kiku.freshair.data.TMRepository;
import com.kiku.freshair.databinding.FragmentHomeBinding;
import com.kiku.freshair.db.StationNameRealmObject;
import com.kiku.freshair.stationMaterial.GetStation;
import com.kiku.freshair.tmMaterial.GetTM;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import jxl.Sheet;
import jxl.Workbook;
import retrofit2.Call;
import retrofit2.Callback;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    public static final int REQUEST_CODE_FINE_COARSE_PERMISSION = 1000;
    private FusedLocationProviderClient mFusedLocationClient;
    private Realm realm;
    private NavController navController;

    RequestQueue queue1;
    RequestQueue queue2;

    private GetStationRepository getStationRepository;
    private GetTMRepository getTMRepository;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private String mainStation = "성북구";


    private LocationManager lm;
    private double latitude;   //위도
    private double longitude; //경도
    private String tmX,tmY;
    private String newTmX, newTmY;
    private String stationName;
    private String umdName;
    private TextView tv;
    private String url = "https://dapi.kakao.com/v2/local/geo/transcoord.json?input_coord=WGS84&output_coord=TM";
    private String getStationName = "getNearbyMsrstnList?";
    private String servicekey = "serviceKey=5RgBcNGSw2M0ezM0Q4WQVCnvtza%2FYoxTViaTrnagFqAfQgOPLV6C0AS5mxWMUwMJWU%2BKdV9OevGejyHzqUS8Hw%3D%3D";
    private String getTM = "getTMStdrCrdnt?";
    private String returnType = "returnType=json&numOfRows=10&pageNo=1&";

    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab_contatiner, fab_add, fab_delete, fab_delete_all;

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private View view;
    private DustPagerAdapter adapter;
    private ArrayList<Pair<Fragment, String>> fragmentList;

    private LinearLayout bottomSheetHeader;
    private LinearLayout bottomSheetLayout;
    private BottomSheetBehavior sheetBehavior;
    private ImageView arrow_image;

    int i = 1;

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_nav_home_to_nav_stationnamesetting);
                break;
            case R.id.action_map:
                navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_nav_home_to_nav_map);
                break;
            case R.id.action_main:
                AddLocationDialogFragment.newInstance(new AddLocationDialogFragment.OnClickListener() {
                    @Override
                    public void onOkClicked(String stationName) {
                        if(stationNameCheck(stationName)){
                            mainStation = stationName;
                            editor.remove("station");
                            editor.putString("station",mainStation);
                            editor.apply();
                            initializeValue();
                            setUpViewPager();
                        }else transCoord2(stationName);

                    }
                }).show(getParentFragmentManager(),"dialog");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fragmentManager = getChildFragmentManager();
        HomeBottomSheetFragment fragment = new HomeBottomSheetFragment(getActivity());

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = preferences.edit();

        initializeValue();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        setHasOptionsMenu(true);

        realm = Realm.getDefaultInstance();


        setBottomSheet();
        setFAB();
        //update();
        setUpViewPager();
        return view;
    }


    public void setFAB(){
        fab_open = AnimationUtils.loadAnimation(getActivity(),R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity(),R.anim.fab_close);

        fab_contatiner = (FloatingActionButton)view.findViewById(R.id.fab_contatiner);
        fab_add = (FloatingActionButton)view.findViewById(R.id.fab_add);
        fab_delete = (FloatingActionButton)view.findViewById(R.id.fab_delete);
        fab_delete_all = (FloatingActionButton)view.findViewById(R.id.fab_delete_all);


        fab_contatiner.setOnClickListener(this);
        fab_add.setOnClickListener(this);
        fab_delete.setOnClickListener(this);
        fab_delete_all.setOnClickListener(this);
    }

    public void setBottomSheet(){
        bottomSheetHeader = view.findViewById(R.id.bottom_sheet_header);
        bottomSheetLayout = view.findViewById(R.id.bottom_sheet_layout);
        sheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        arrow_image = view.findViewById(R.id.bottom_sheet_arrow);

        arrow_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WindowActivity.class);
                startActivity(intent);
                /*
                if(sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                */
            }
        });
        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull @NotNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull @NotNull View bottomSheet, float slideOffset) {
                arrow_image.setRotation(slideOffset * 180);
            }
        });
        bottomSheetHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WindowActivity.class);
                startActivity(intent);
                /*
                if(sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                */

            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab_contatiner:
                anim();

                break;
            case R.id.fab_add:
                AddLocationDialogFragment.newInstance(new AddLocationDialogFragment.OnClickListener() {
                    @Override
                    public void onOkClicked(String umdName) {
                        if(stationNameCheck(umdName)){
                            saveStationName(umdName);
                            addNewFragment(umdName);
                        }else transCoord(umdName);

                    }
                }).show(getParentFragmentManager(),"dialog");

                break;
            case R.id.fab_delete:
                if(viewPager2.getCurrentItem()==0){
                    Toast.makeText(getActivity(),"메인탭은 삭제할수없습니다",Toast.LENGTH_SHORT).show();
                }else{
                    realm.beginTransaction();
                    realm.where(StationNameRealmObject.class).findAll().get(viewPager2.getCurrentItem()-1).deleteFromRealm();
                    realm.commitTransaction();
                    setUpViewPager();
                }

                break;
            case R.id.fab_delete_all:
                realm.beginTransaction();
                realm.where(StationNameRealmObject.class).findAll().deleteAllFromRealm();
                realm.commitTransaction();
                setUpViewPager();


        }


    }
    public void initializeValue(){
        mainStation = preferences.getString("station","성북구");
    }

    public void anim() {

        if (isFabOpen) {
            fab_add.startAnimation(fab_close);
            fab_delete.startAnimation(fab_close);
            fab_delete_all.startAnimation(fab_close);
            fab_add.setClickable(false);
            fab_delete.setClickable(false);
            fab_delete_all.setClickable(false);
            isFabOpen = false;
        } else {
            fab_add.startAnimation(fab_open);
            fab_delete.startAnimation(fab_open);
            fab_delete_all.startAnimation(fab_open);
            fab_add.setClickable(true);
            fab_delete.setClickable(true);
            fab_delete_all.setClickable(true);
            isFabOpen = true;
        }
    }

    public void saveStationName(String stationName){
        realm.beginTransaction();
        StationNameRealmObject stationNameRealmObject = realm.createObject(StationNameRealmObject.class);
        stationNameRealmObject.setStationName(stationName);

        realm.commitTransaction();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpViewPager();


    }
    public void transCoord(String umdName){
        getTMRepository = new TMRepository(umdName);
        if(getTMRepository.isAvailable()){
            getTMRepository.getTMData(new Callback<GetTM>() {
                @Override
                public void onResponse(Call<GetTM> call, retrofit2.Response<GetTM> response) {
                    Log.d("sss", "성공1"+response.body().getResponse().getBody().getTotalCount());


                    if(!response.body().getResponse().getBody().getTotalCount().equals("0")) {
                        tmX = response.body().getResponse().getBody().getItems().get(0).getTmX();
                        tmY = response.body().getResponse().getBody().getItems().get(0).getTmY();
                        Log.d("sss", "x는"+tmX+"y는"+tmY);
                        getNewStation(tmX, tmY);
                    }else Toast.makeText(getActivity(),"다시 입력해주세요", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onFailure(Call<GetTM> call, Throwable t) {
                    Log.d("sss", "gettm실패"+t);

                }
            });
        }
    }


    private void getNewStation(String tmX, String tmY) {
        getStationRepository = new StationRepository(tmX, tmY);
        if(getStationRepository.isAvailable()){
            getStationRepository.getStationData(new Callback<GetStation>() {
                @Override
                public void onResponse(Call<GetStation> call, retrofit2.Response<GetStation> response) {
                    Log.d("sss", "stationname"+response.body().getResponse().getBody().getItems().get(0).getStationName());
                    saveStationName(response.body().getResponse().getBody().getItems().get(0).getStationName());
                    addNewFragment(response.body().getResponse().getBody().getItems().get(0).getStationName());
                }

                @Override
                public void onFailure(Call<GetStation> call, Throwable t) {
                    Log.d("sss", t.toString());

                }
            });
        }
    }

    public void transCoord2(String umdName){
        getTMRepository = new TMRepository(umdName);
        if(getTMRepository.isAvailable()){
            getTMRepository.getTMData(new Callback<GetTM>() {
                @Override
                public void onResponse(Call<GetTM> call, retrofit2.Response<GetTM> response) {
                    Log.d("sss", "성공1"+response.body().getResponse().getBody().getTotalCount());


                    if(!response.body().getResponse().getBody().getTotalCount().equals("0")) {
                        tmX = response.body().getResponse().getBody().getItems().get(0).getTmX();
                        tmY = response.body().getResponse().getBody().getItems().get(0).getTmY();
                        Log.d("sss", "x는"+tmX+"y는"+tmY);
                        getNewStation2(tmX, tmY);
                    }else Toast.makeText(getActivity(),"다시 입력해주세요", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onFailure(Call<GetTM> call, Throwable t) {
                    Log.d("sss", "gettm실패"+t);

                }
            });
        }
    }


    private void getNewStation2(String tmX, String tmY) {
        getStationRepository = new StationRepository(tmX, tmY);
        if(getStationRepository.isAvailable()){
            getStationRepository.getStationData(new Callback<GetStation>() {
                @Override
                public void onResponse(Call<GetStation> call, retrofit2.Response<GetStation> response) {
                    Log.d("sss", "stationname"+response.body().getResponse().getBody().getItems().get(0).getStationName());
                    mainStation = response.body().getResponse().getBody().getItems().get(0).getStationName();
                    Log.d("측정소",mainStation);
                    editor.remove("station");
                    editor.putString("station",mainStation);
                    editor.apply();
                    initializeValue();
                    setUpViewPager();
                }

                @Override
                public void onFailure(Call<GetStation> call, Throwable t) {
                    Log.d("sss", t.toString());

                }
            });
        }
    }



    private void setUpViewPager(){
        loadDbData();
        adapter = new DustPagerAdapter(getActivity(), fragmentList);
        viewPager2 = (ViewPager2)view.findViewById(R.id.viewpager2);
        viewPager2.setAdapter(adapter);
    }
    private void loadDbData() {
        fragmentList = new ArrayList<>();
        fragmentList.add(new Pair<Fragment, String>(GetDustFragment.newInstance(mainStation),mainStation));

        RealmResults<StationNameRealmObject> results = realm.where(StationNameRealmObject.class).findAll();

        for (StationNameRealmObject stationNameRealmObject : results){
            fragmentList.add(new Pair<Fragment, String>(GetDustFragment.
                    newInstance(stationNameRealmObject.getStationName()),
                    stationNameRealmObject.getStationName()));
        }
    }

    private void addNewFragment(String stationName){
        fragmentList.add(new Pair<Fragment, String>(GetDustFragment.newInstance(stationName),stationName));
        viewPager2.getAdapter().notifyDataSetChanged();
        Toast.makeText(getActivity(),"페이지가 추가되었습니다.("+stationName+")",Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        realm.close();
    }
    private Boolean stationNameCheck(String umdName){
        try {
            InputStream is = getActivity().getResources().getAssets().open("station_list.xls");

            Workbook wb = Workbook.getWorkbook(is);

            if(wb != null){
                Sheet sheet = wb.getSheet(0);
                if(sheet != null){
                    int colTotal = sheet.getColumns();
                    int rowIndexStart = 4;
                    int rowTotal = sheet.getColumn(colTotal-1).length;

                    for(int row=rowIndexStart; row<rowTotal;row++){

                        for(int col=0; col<colTotal; col++){
                            String contents = sheet.getCell(col, row).getContents();
                            if(contents.equals(umdName)) {
                                return true;
                            }

                        }
                    }
                }
            }
            is.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}