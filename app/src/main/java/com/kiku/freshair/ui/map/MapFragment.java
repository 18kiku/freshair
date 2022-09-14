package com.kiku.freshair.ui.map;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.kiku.freshair.GetDust.GetDustContract;
import com.kiku.freshair.R;
import com.kiku.freshair.data.GetDustRepository;
import com.kiku.freshair.databinding.FragmentMapBinding;
import com.kiku.freshair.dustMaterial.GetDust;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;

public class MapFragment extends Fragment implements GetDustContract.View, OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private View view;
    private FragmentMapBinding binding;
    private GoogleMap googleMap;
    private Geocoder geocoder;
    private ViewGroup mapViewContainer;
    private MapView mapView = null;
    private TextView markerText;
    private String stationName[];

    private String[] tmX = {"201898.204134","201548.159746","201540.790208","201698.719358","201756.98915","201579.335288","201400.223011","200571.99834","200660.354941"};
    private String[] tmY = {"456270.224048","455052.136519","454444.885195","454207.87877","454624.173888","454769.436623","454889.251993","454192.226907","454045.874462"};

    LatXLonY xy;
    LatLng[] POINT;
    LatLng MAINPOINT;


    private GetDustRepository getDustRepository;
    private String[] lat, lon;
    private double x, y;
    private double mainLat, mainLon;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private String mainStation = "성북구";
    private String transLocation;
    private String pm10Grade;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stationName = new String[600];
        lat = new String[200];
        lon = new String[200];
        POINT = new LatLng[600];
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = preferences.edit();
        initializeValue();
        findStation();

        if(transLocation==null) addrToXy(getActivity(), mainStation);
        else if(transLocation!=null) addrToXy(getActivity(), transLocation);



    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMapBinding.inflate(inflater, container, false);
        view = binding.getRoot();



        mapView = (MapView)view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);



        return view;
    }

    public void initializeValue(){
        mainStation = preferences.getString("station","성북구");
    }

    public void addrToNxy(Context context, String stationName){

        Geocoder geocoder = new Geocoder(context);
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocationName(stationName,3);
        }catch (Exception e){

        }

        Address address = addresses.get(0);

        x = address.getLatitude();
        y = address.getLongitude();

    }

    public void addrToXy(Context context, String stationName){

        Geocoder geocoder = new Geocoder(context);
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocationName(stationName,3);
        }catch (Exception e){

        }

        Address address = addresses.get(0);

        mainLat = address.getLatitude();
        mainLon = address.getLongitude();

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        MAINPOINT = new LatLng(mainLat, mainLon);

        markerText = (TextView)view.findViewById(R.id.markertext);

        LatLng POINT1 = new LatLng(37.498485090333986, 126.89020472873915);
        LatLng POINT2 = new LatLng(37.47514305949898, 126.89854608085656);
        LatLng POINT3 = new LatLng(37.47872663467583, 126.864641382708);


/*
        for(int i=0;i<POINT.length;i++) {
            MarkerOptions marker = new MarkerOptions();
            marker.position(POINT[i]);
            googleMap.addMarker(marker);
        }
*/
        //MarkerOptions marker = new MarkerOptions();
        //marker.position(MAINPOINT);
        //googleMap.addMarker(marker);
        MarkerOptions marker = new MarkerOptions();
        for(int i=0; i<158; i++){

            marker.title(stationName[i]);
            marker.position(new LatLng(Double.parseDouble(lat[i]),Double.parseDouble(lon[i])));
            marker.icon(BitmapDescriptorFactory.defaultMarker(200f));
            googleMap.addMarker(marker);
        }


        //인포윈도우 클릭
        //googleMap.setOnInfoWindowClickListener(this);

        //맵뷰 카메라위치, 줌 설정
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MAINPOINT, 13));
        googleMap.setOnMarkerClickListener(markerClickListener);
    }
    GoogleMap.OnMarkerClickListener markerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(@NonNull @NotNull Marker marker) {
            Toast.makeText(getActivity(),marker.getTitle(),Toast.LENGTH_SHORT).show();
            return false;
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onLowMemory();
    }

    @Override
    public void onInfoWindowClick(@NonNull @NotNull Marker marker) {

    }


    @Override
    public void showGetDustResult(GetDust getDust) {
        try {
            pm10Grade = getDust.getResponse().getBody().getItems().get(0).getPm10Grade();
            Log.d("태그1",pm10Grade);

        }catch (Exception e){

        }
    }

    @Override
    public void showLoadError(String message) {

    }

    private void readExcel(){
        try {
            InputStream is = getActivity().getResources().getAssets().open("station_list.xls");

            Workbook wb = Workbook.getWorkbook(is);

            if(wb != null){
                Sheet sheet = wb.getSheet(0);
                if(sheet != null){
                    int colTotal = sheet.getColumns();
                    int rowIndexStart = 4;
                    int i=0;
                    int rowTotal = sheet.getColumn(colTotal-1).length;
                    StringBuilder sb;
                    for(int row=rowIndexStart; row<rowTotal;row++){
                        sb = new StringBuilder();
                        for(int col=0; col<colTotal; col++){
                            String contents = sheet.getCell(col, row).getContents();
                            if(contents.equals(stationName)) {
                                transLocation=sheet.getCell(2,row).getContents();
                            }
                            i++;
                            Log.d("mmm", col+"번째"+contents);
                        }
                    }
                }
            }
            is.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void findStation(){
        try {
            InputStream is = getActivity().getResources().getAssets().open("station_list.xls");

            Workbook wb = Workbook.getWorkbook(is);

            if(wb != null){
                Sheet sheet = wb.getSheet(0);
                if(sheet != null){
                    int colTotal = sheet.getColumns();
                    int rowIndexStart = 4;

                    //int rowTotal = sheet.getColumn(colTotal-1).length;
                    int rowTotal = 162;
                    StringBuilder sb;
                    for(int row=rowIndexStart; row<rowTotal;row++){
                        for(int col=0; col<colTotal; col++){
                            String contents = sheet.getCell(col, row).getContents();
                            if(contents.equals(stationName)) {
                                transLocation=sheet.getCell(2,row).getContents();
                            }
                            Log.d("mmm", col+"번째"+contents);
                        }
                        stationName[row-4]=sheet.getCell(1,row).getContents();

                        Log.d("엑셀확인",(row-4)+"번째"+stationName[row-4]);
                        Log.d("엑셀확인",(row-4)+"번째"+sheet.getCell(2,row).getContents());

                        lat[row-4]=sheet.getCell(5,row).getContents();
                        lon[row-4]=sheet.getCell(6,row).getContents();
                        Log.d("엑셀확인d",(row-4)+"번째"+lat[row-4]);
                        Log.d("엑셀확인d",(row-4)+"번째"+lon[row-4]);



                    }
                }
            }
            is.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}