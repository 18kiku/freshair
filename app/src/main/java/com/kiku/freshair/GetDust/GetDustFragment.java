package com.kiku.freshair.GetDust;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kiku.freshair.R;
import com.kiku.freshair.data.DustRepository;
import com.kiku.freshair.data.GetDustRepository;
import com.kiku.freshair.data.GetWeatherRepository;
import com.kiku.freshair.data.WeatherRepository;
import com.kiku.freshair.databinding.FragmentGetDustBinding;
import com.kiku.freshair.dustMaterial.GetDust;
import com.kiku.freshair.weatherMaterial.GetWeather;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;

public class GetDustFragment extends Fragment implements GetDustContract.View, GetWeatherContract.View, OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    private FragmentGetDustBinding binding;
    private FrameLayout frameLayout;
    private TextView locationTextView;
    private TextView timeTextView;
    private TextView dustTextView;
    private View view;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageButton arrow, arrow2;
    private LinearLayout hiddenView, hiddenView2;
    private CardView cardView, cardView2, cardView3;
    private TextView tip;
    private String[][] tipComment;


    private GetDustRepository getDustRepository;
    private GetWeatherRepository getWeatherRepository;

    Date date;

    String stationName;
    String weatherName;

    private double lat, lon;
    LatXLngY nxy;
    private int nx, ny;

    private MapView mapView;

    private TextView tStationName;
    private TextView airComment, weatherComment;
    private String khaiValue, pm10Grade, pm25Grade, so2Grade, o3Grade, khaiGrade, no2Grade, coGrade, pm10Value, pm25Value, so2Value, o3Value, no2Value, coValue, dataTime;
    private TextView tkhaiValue, tpm10Grade, tpm25Grade, tso2Grade, to3Grade, tkhaiGrade, tno2Grade, tcoGrade, tpm10Value, tpm25Value, tso2Value, to3Value, tno2Value, tcoValue, tdataTime;

    private ImageView ikhaiValue, ipm10Grade, ipm25Grade, iso2Grade, io3Grade, ikhaiGrade, ino2Grade, icoGrade, ipm10Value, ipm25Value, iso2Value, io3Value, ino2Value, icoValue, idataTime;
    private  String[] POP, PTY, PCP, REH, TMP, TMN, TMX, SKY, FCSTTIME;
    private TextView tTMP, tTMN, tTMX;
    private ImageView isky;

    public GetDustFragment(){}

    public static GetDustFragment newInstance(String stationName) {
        GetDustFragment fragment = new GetDustFragment();
        Bundle args = new Bundle();
        args.putString("stationName", stationName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tipComment = new String[10][10];
        TMP = new String[100];
        SKY = new String[100];
        TMN = new String[100];
        TMX = new String[100];
        POP = new String[100];
        PTY = new String[100];
        PCP = new String[100];
        REH = new String[100];
        FCSTTIME = new String[100];

        readTip();

        Date date = new Date(System.currentTimeMillis());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("HH");

        String base_date = dateFormat.format(date);
        String base_time = setTime(timeFormat.format(date));
        Log.d("xxx","11basedate"+ base_date);

        Log.d("xxx","11basetime"+ base_time);

        String stationName = getArguments().getString("stationName");
        Log.d("sss",stationName);
        this.stationName = stationName;

        readExcel();
        Log.d("weathername","웨더네임은" +weatherName);
        if(weatherName==null) addrToXy(getActivity(), stationName);
        else if(weatherName!=null) addrToXy(getActivity(), weatherName);

        Log.d("수지", "수지는"+nx+ny);
        if(Integer.parseInt(base_time)<0200){
            base_date =String.valueOf(Integer.parseInt(base_date)-1);
            Log.d("확인", "1z"+base_date);
            base_time ="2300";
        }
        Log.d("xxx","basedate"+ base_date);
        Log.d("xxx","basetime"+ base_time);
        if(nx!=0) getWeatherRepository = new WeatherRepository(String.valueOf(nx), String.valueOf(ny), base_date, base_time);

        GetWeatherPresenter weatherPresenter = new GetWeatherPresenter(getWeatherRepository, this);

        weatherPresenter.loadGetWeatherData();



        /////////////////////////////////아래로 대기정보
        getDustRepository = new DustRepository(stationName);
        GetDustPresenter dustPresenter = new GetDustPresenter(getDustRepository, this);
        dustPresenter.loadGetDustData();




    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_get_dust, container, false);
        setCard();
        frameLayout = view.findViewById(R.id.dust_container);
        setUpView();

        if(savedInstanceState != null){
            tpm10Value.setText(savedInstanceState.getString("pm10Value"));
            tStationName.setText(savedInstanceState.getString("stationName"));
            tdataTime.setText(savedInstanceState.getString("dataTime"));

        }
        tStationName.setText(stationName);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getActivity(),"refresh",Toast.LENGTH_SHORT).show();
            }
        });
        mapView = (MapView)view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);





        return view;
    }

    public String setTime(String time){
        int t = Integer.parseInt(time);
        if(2>t&&t>=0) return "0000";
        else if(t>=2&&t<5) return "0200";
        else if(t>=5&&t<8) return "0500";
        else if(t>=8&&t<11) return "0800";
        else if(t>=11&&t<14) return "1100";
        else if(t>=14&&t<17) return "1400";
        else if(t>=17&&t<20) return "1700";
        else if(t>=20&&t<23) return "2000";
        else if(t>=23&&t<24) return "2300";
        else return "0500";
    }

    public void addrToXy(Context context, String stationName){

        Geocoder geocoder = new Geocoder(context);
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocationName(stationName,3);
        }catch (Exception e){

        }

        Address address = addresses.get(0);

        lat = address.getLatitude();
        lon = address.getLongitude();
        Log.d("sssss", "lat="+lat+"lon="+lon);

        if(lat!=0) nxy = convertGRID_GPS(0, lat, lon);
        else lat=60; lon=127;
        nx = (int)nxy.x;
        ny = (int)nxy.y;
        Log.d("sss", "nx="+nx+"ny="+ny);






    }


    private LatXLngY convertGRID_GPS(int mode, double lat_X, double lng_Y )
    {
        double RE = 6371.00877; // 지구 반경(km)
        double GRID = 5.0; // 격자 간격(km)
        double SLAT1 = 30.0; // 투영 위도1(degree)
        double SLAT2 = 60.0; // 투영 위도2(degree)
        double OLON = 126.0; // 기준점 경도(degree)
        double OLAT = 38.0; // 기준점 위도(degree)
        double XO = 43; // 기준점 X좌표(GRID)
        double YO = 136; // 기1준점 Y좌표(GRID)

        //
        // LCC DFS 좌표변환 ( code : "TO_GRID"(위경도->좌표, lat_X:위도,  lng_Y:경도), "TO_GPS"(좌표->위경도,  lat_X:x, lng_Y:y) )
        //


        double DEGRAD = Math.PI / 180.0;
        double RADDEG = 180.0 / Math.PI;

        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);
        LatXLngY rs = new LatXLngY();

        if (mode == 0) {
            rs.lat = lat_X;
            rs.lng = lng_Y;
            double ra = Math.tan(Math.PI * 0.25 + (lat_X) * DEGRAD * 0.5);
            ra = re * sf / Math.pow(ra, sn);
            double theta = lng_Y * DEGRAD - olon;
            if (theta > Math.PI) theta -= 2.0 * Math.PI;
            if (theta < -Math.PI) theta += 2.0 * Math.PI;
            theta *= sn;
            rs.x = Math.floor(ra * Math.sin(theta) + XO + 0.5);
            rs.y = Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);
        }
        else {
            rs.x = lat_X;
            rs.y = lng_Y;
            double xn = lat_X - XO;
            double yn = ro - lng_Y + YO;
            double ra = Math.sqrt(xn * xn + yn * yn);
            if (sn < 0.0) {
                ra = -ra;
            }
            double alat = Math.pow((re * sf / ra), (1.0 / sn));
            alat = 2.0 * Math.atan(alat) - Math.PI * 0.5;

            double theta = 0.0;
            if (Math.abs(xn) <= 0.0) {
                theta = 0.0;
            }
            else {
                if (Math.abs(yn) <= 0.0) {
                    theta = Math.PI * 0.5;
                    if (xn < 0.0) {
                        theta = -theta;
                    }
                }
                else theta = Math.atan2(xn, yn);
            }
            double alon = theta / sn + olon;
            rs.lat = alat * RADDEG;
            rs.lng = alon * RADDEG;
        }
        return rs;
    }

    public void setUpView(){
        tkhaiValue = view.findViewById(R.id.khaivaluetext);
        tpm10Value = view.findViewById(R.id.pm10valuetext);
        tpm25Value = view.findViewById(R.id.pm25valuetext);
        tso2Value = view.findViewById(R.id.so2valuetext);
        to3Value = view.findViewById(R.id.o3valuetext);
        tno2Value = view.findViewById(R.id.no2valuetext);
        tcoValue = view.findViewById(R.id.covaluetext);

        tpm10Grade = view.findViewById(R.id.pm10valueword);
        tpm25Grade = view.findViewById(R.id.pm25valueword);
        tso2Grade = view.findViewById(R.id.so2valueword);
        to3Grade = view.findViewById(R.id.o3valueword);
        tno2Grade = view.findViewById(R.id.no2valueword);
        tcoGrade = view.findViewById(R.id.covalueword);

        tdataTime = view.findViewById(R.id.datatime);
        tStationName = view.findViewById(R.id.stationname);

        tip = view.findViewById(R.id.tip);

        ikhaiGrade = view.findViewById(R.id.khaigradeimage);
        ipm10Grade = view.findViewById(R.id.pm10gradeimage);
        ipm25Grade = view.findViewById(R.id.pm25gradeimage);
        iso2Grade = view.findViewById(R.id.so2gradeimage);
        io3Grade = view.findViewById(R.id.o3gradeimage);
        ino2Grade = view.findViewById(R.id.no2gradeimage);
        icoGrade = view.findViewById(R.id.cogradeimage);

        ipm10Grade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "미세먼지",Toast.LENGTH_SHORT).show();
            }
        });
        ipm25Grade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        iso2Grade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        io3Grade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ino2Grade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        icoGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        tTMP = view.findViewById(R.id.tmp);
        tTMN = view.findViewById(R.id.tmn);
        tTMX = view.findViewById(R.id.tmx);
        isky = view.findViewById(R.id.sky);

        airComment = view.findViewById(R.id.air_comment);
        weatherComment = view.findViewById(R.id.weather_comment);

    }

    public void setCard(){

        cardView = view.findViewById(R.id.base_cardview);
        cardView2 = view.findViewById(R.id.base_cardview2);
        cardView3 = view.findViewById(R.id.base_cardview3);
        arrow = view.findViewById(R.id.arrow_button);
        arrow2 = view.findViewById(R.id.arrow_button2);
        hiddenView = view.findViewById(R.id.hidden_view);
        hiddenView2 = view.findViewById(R.id.hidden_view2);

        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hiddenView.getVisibility() == View.VISIBLE) {
                    TransitionManager.beginDelayedTransition(cardView,
                            new AutoTransition());
                    hiddenView.setVisibility(View.GONE);
                    arrow.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24);
                } else {
                    TransitionManager.beginDelayedTransition(cardView,
                            new AutoTransition());
                    hiddenView.setVisibility(View.VISIBLE);
                    arrow.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24);
                }
            }
        });
        arrow2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hiddenView2.getVisibility() == View.VISIBLE) {
                    TransitionManager.beginDelayedTransition(cardView2,
                            new AutoTransition());
                    hiddenView2.setVisibility(View.GONE);
                    arrow.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24);
                } else {
                    TransitionManager.beginDelayedTransition(cardView2,
                            new AutoTransition());
                    hiddenView2.setVisibility(View.VISIBLE);
                    arrow.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24);
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
        outState.putString("pm10Value",tpm10Value.getText().toString());
        outState.putString("stationName",tStationName.getText().toString());
        outState.putString("dataTime",tdataTime.getText().toString());
        //outState.putByte("pm10GradeImage",pm10GradeImage.getResources().);
    }


    @Override
    public void showGetDustResult(GetDust getDust) {
        try {
            putResultData(getDust);
            setGradeImage();
            setValueText();
            setAirComment();

            tkhaiValue.setText(getDust.getResponse().getBody().getItems().get(0).getKhaiValue());

        }catch (Exception e){
        }
    }


    void setAirComment(){
        if(500>=Integer.parseInt(khaiValue)&&Integer.parseInt(khaiValue)>250) {
            airComment.setText("하늘상태 최악");
            arrow.setBackgroundColor(Color.rgb(211,123,70));
            frameLayout.setBackgroundColor(Color.rgb(211,123,0));
            cardView.setCardBackgroundColor(Color.rgb(211,123,0));
            cardView2.setCardBackgroundColor(Color.rgb(211,123,0));
            cardView3.setCardBackgroundColor(Color.rgb(211,123,0));

        }
        else if(250>=Integer.parseInt(khaiValue)&&Integer.parseInt(khaiValue)>100) {
            airComment.setText("하늘이 안 좋네요");
            arrow.setBackgroundColor(Color.rgb(242,169,0));
            frameLayout.setBackgroundColor(Color.rgb(242,169,0));
            cardView.setCardBackgroundColor(Color.rgb(242,169,0));
            cardView2.setCardBackgroundColor(Color.rgb(242,169,0));
            cardView3.setCardBackgroundColor(Color.rgb(242,169,0));
        }
        else if(100>=Integer.parseInt(khaiValue)&&Integer.parseInt(khaiValue)>50) {
            airComment.setText("그럭저럭 괜찮아요");
            arrow.setBackgroundColor(Color.rgb(0,201,70));
            frameLayout.setBackgroundColor(Color.rgb(0,201,70));
            cardView.setCardBackgroundColor(Color.rgb(0,201,70));
            cardView2.setCardBackgroundColor(Color.rgb(0,201,70));
            cardView3.setCardBackgroundColor(Color.rgb(0,201,70));
        }
        else if(50>=Integer.parseInt(khaiValue)&&Integer.parseInt(khaiValue)>0) {
            airComment.setText("하늘이 깨끗해요");
            arrow.setBackgroundColor(Color.rgb(0,181,242));
            frameLayout.setBackgroundColor(Color.rgb(0,181,242));
            cardView.setCardBackgroundColor(Color.rgb(0,181,242));
            cardView2.setCardBackgroundColor(Color.rgb(0,181,242));
            cardView3.setCardBackgroundColor(Color.rgb(0,181,242));
       }
        else airComment.setText("측정소 점검중");

    }
    void setGradeImage(){

        switch (khaiGrade){
            case "1":
                ikhaiGrade.setImageResource(R.drawable.grade1);
                break;
            case "2":
                ikhaiGrade.setImageResource(R.drawable.grade2);
                break;
            case "3":
                ikhaiGrade.setImageResource(R.drawable.grade3);
                break;
            case "4":
                ikhaiGrade.setImageResource(R.drawable.grade4);
                break;
            default:
                ikhaiGrade.setImageResource(R.drawable.ic_baseline_error_24);
                break;
        }

        switch (pm10Grade){
            case "1":
                ipm10Grade.setImageResource(R.drawable.grade1);
                tpm10Grade.setText("좋음");
                tip.setText("오늘의 팁\n공기 걱정은 안 해도 되겠어요");
                break;
            case "2":
                ipm10Grade.setImageResource(R.drawable.grade2);
                tpm10Grade.setText("보통");
                tip.setText("오늘의 팁\n"+tipComment[4][2]);


                break;
            case "3":
                ipm10Grade.setImageResource(R.drawable.grade3);
                tpm10Grade.setText("나쁨");
                tip.setText("오늘의 팁\n"+tipComment[4][3]);
                break;
            case "4":
                ipm10Grade.setImageResource(R.drawable.grade4);
                tpm10Grade.setText("매우나쁨");
                tip.setText("오늘의 팁\n"+tipComment[4][4]);
                break;
            default:
                ipm10Grade.setImageResource(R.drawable.ic_baseline_error_24);
                break;
        }
        switch (pm25Grade){
            case "1":
                ipm25Grade.setImageResource(R.drawable.grade1);
                tpm25Grade.setText("좋음");
                break;
            case "2":
                ipm25Grade.setImageResource(R.drawable.grade2);
                tpm25Grade.setText("보통");
                break;
            case "3":
                ipm25Grade.setImageResource(R.drawable.grade3);
                tpm25Grade.setText("나쁨");
                break;
            case "4":
                ipm25Grade.setImageResource(R.drawable.grade4);
                tpm25Grade.setText("매우나쁨");
                break;
            default:
                ipm25Grade.setImageResource(R.drawable.ic_baseline_error_24);
                break;
        }
        switch (so2Grade){
            case "1":
                iso2Grade.setImageResource(R.drawable.grade1);
                tso2Grade.setText("좋음");
                break;
            case "2":
                iso2Grade.setImageResource(R.drawable.grade2);
                tso2Grade.setText("보통");
                break;
            case "3":
                iso2Grade.setImageResource(R.drawable.grade3);
                tso2Grade.setText("나쁨");
                break;
            case "4":
                iso2Grade.setImageResource(R.drawable.grade4);
                tso2Grade.setText("매우나쁨");
                break;
            default:
                iso2Grade.setImageResource(R.drawable.ic_baseline_error_24);

                break;
        }
        switch (o3Grade){
            case "1":
                io3Grade.setImageResource(R.drawable.grade1);
                to3Grade.setText("좋음");
                break;
            case "2":
                io3Grade.setImageResource(R.drawable.grade2);
                to3Grade.setText("보통");
                break;
            case "3":
                io3Grade.setImageResource(R.drawable.grade3);
                to3Grade.setText("나쁨");
                break;
            case "4":
                io3Grade.setImageResource(R.drawable.grade4);
                to3Grade.setText("매우나쁨");
                break;
            default:
                io3Grade.setImageResource(R.drawable.ic_baseline_error_24);
                break;
        }
        switch (no2Grade){
            case "1":
                ino2Grade.setImageResource(R.drawable.grade1);
                tno2Grade.setText("좋음");
                break;
            case "2":
                ino2Grade.setImageResource(R.drawable.grade2);
                tno2Grade.setText("보통");
                break;
            case "3":
                ino2Grade.setImageResource(R.drawable.grade3);
                tno2Grade.setText("나쁨");
                break;
            case "4":
                ino2Grade.setImageResource(R.drawable.grade4);
                tno2Grade.setText("매우나쁨");
                break;
            default:
                ino2Grade.setImageResource(R.drawable.ic_baseline_error_24);
                break;
        }
        switch (coGrade){
            case "1":
                icoGrade.setImageResource(R.drawable.grade1);
                tcoGrade.setText("좋음");
                break;
            case "2":
                icoGrade.setImageResource(R.drawable.grade2);
                tcoGrade.setText("보통");
                break;
            case "3":
                icoGrade.setImageResource(R.drawable.grade3);
                tcoGrade.setText("나쁨");
                break;
            case "4":
                icoGrade.setImageResource(R.drawable.grade4);
                tcoGrade.setText("매우나쁨");
                break;
            default:
                icoGrade.setImageResource(R.drawable.ic_baseline_error_24);
                break;
        }
    }
    void setValueText(){
        tStationName.setText(stationName);
        tkhaiValue.setText(khaiValue);
        tpm10Value.setText(pm10Value+"㎍/㎥");
        tpm25Value.setText(pm25Value+"㎍/㎥");
        tso2Value.setText(so2Value+"ppm");
        to3Value.setText(o3Value+"ppm");
        tno2Value.setText(no2Value+"ppm");
        tcoValue.setText(coValue+"ppm");

        tdataTime.setText(transTime(dataTime.substring(11,13)+dataTime.substring(14,16)));

    }
    void putResultData(GetDust getDust){
        pm10Grade = getDust.getResponse().getBody().getItems().get(0).getPm10Grade();
        pm25Grade = getDust.getResponse().getBody().getItems().get(0).getPm25Grade();
        so2Grade = getDust.getResponse().getBody().getItems().get(0).getSo2Grade();
        o3Grade = getDust.getResponse().getBody().getItems().get(0).getO3Grade();
        khaiGrade = getDust.getResponse().getBody().getItems().get(0).getKhaiGrade();
        no2Grade = getDust.getResponse().getBody().getItems().get(0).getNo2Grade();
        coGrade = getDust.getResponse().getBody().getItems().get(0).getCoGrade();

        if(pm10Grade==null) pm10Grade="-";
        if(pm25Grade==null) pm25Grade="-";
        if(so2Grade==null) so2Grade="-";
        if(o3Grade==null) o3Grade="-";
        if(khaiGrade==null) khaiGrade="-";
        if(no2Grade==null) no2Grade="-";
        if(coGrade==null) coGrade="-";


        dataTime = getDust.getResponse().getBody().getItems().get(0).getDataTime();

        khaiValue = getDust.getResponse().getBody().getItems().get(0).getKhaiValue();
        pm10Value = getDust.getResponse().getBody().getItems().get(0).getPm10Value();
        pm25Value = getDust.getResponse().getBody().getItems().get(0).getPm25Value();
        so2Value = getDust.getResponse().getBody().getItems().get(0).getSo2Value();
        o3Value = getDust.getResponse().getBody().getItems().get(0).getO3Value();
        no2Value = getDust.getResponse().getBody().getItems().get(0).getNo2Value();
        coValue = getDust.getResponse().getBody().getItems().get(0).getCoValue();
        Log.d("점검",pm10Grade+" "+pm25Grade+" "+so2Grade+" "+o3Grade+" "+khaiGrade+" "+no2Grade+" "+coGrade+" "+dataTime+" "+khaiValue+" "+pm10Value+" "+pm25Value+" "+so2Value+" "+o3Value+" "+no2Value+" "+coValue);

    }
    public String transTime(String time){
        switch (time){
            case "0000":
                return "오전0시";
            case "0100":
                return "오전1시";
            case "0200":
                return "오전2시";
            case "0300":
                return "오전3시";
            case "0400":
                return "오전4시";
            case "0500":
                return "오전5시";
            case "0600":
                return "오전6시";
            case "0700":
                return "오전7시";
            case "0800":
                return "오전8시";
            case "0900":
                return "오전9시";
            case "1000":
                return "오전10시";
            case "1100":
                return "오전11시";
            case "1200":
                return "오전12시";
            case "1300":
                return "오후1시";
            case "1400":
                return "오후2시";
            case "1500":
                return "오후3시";
            case "1600":
                return "오후4시";
            case "1700":
                return "오후5시";
            case "1800":
                return "오후6시";
            case "1900":
                return "오후7시";
            case "2000":
                return "오후8시";
            case "2100":
                return "오후9시";
            case "2200":
                return "오후10시";
            case "2300":
                return "오후11시";
            case "2400":
                return "오전0시";
            default:
                return "loading";
        }
    }

    @Override
    public void showGetWeatherResult(GetWeather getWeather) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

        WeatherAdapter adapter = new WeatherAdapter();
        Log.d("확인", "1");

        int tmp=0;
        int tmn=0;
        int sky=0;
        int tmx=0;
        int pop=0;
        int pty=0;
        int pcp=0;
        int reh=0;
        try {
            Log.d("sss", "행수"+getWeather.getResponse().getBody().getNumOfRows());
            for(int i = 0; i<Integer.parseInt(getWeather.getResponse().getBody().getNumOfRows()); i++){
                String category=getWeather.getResponse().getBody().getItems().getItem().get(i).getCategory();
                Log.d("ddd",category);
                Log.d("ddd","i"+i);
                switch (category){
                    case "TMP":
                        TMP[tmp] = getWeather.getResponse().getBody().getItems().getItem().get(i).getFcstValue();
                        FCSTTIME[tmp] = getWeather.getResponse().getBody().getItems().getItem().get(i).getFcstTime();
                        Log.d("sss",tmp+"tmp는"+TMP[tmp]);
                        tmp++;

                        break;
                    case "TMN":
                        TMN[tmn] = getWeather.getResponse().getBody().getItems().getItem().get(i).getFcstValue();
                        Log.d("sss",tmn+"tmn은"+TMN[tmn]);
                        tmn++;

                        break;
                    case "SKY":
                        SKY[sky] = getWeather.getResponse().getBody().getItems().getItem().get(i).getFcstValue();
                        Log.d("sss",sky+"sky는"+SKY[sky]);
                        sky++;
                        break;
                    case "TMX":
                        TMX[tmx] = getWeather.getResponse().getBody().getItems().getItem().get(i).getFcstValue();
                        Log.d("sss",tmx+"tmx는"+TMX[tmx]);
                        tmx++;
                        break;
                    case "POP":
                        POP[pop] = getWeather.getResponse().getBody().getItems().getItem().get(i).getFcstValue();
                        Log.d("sss",pop+"pop는"+POP[pop]);
                        pop++;
                        break;
                    case "PTY":
                        PTY[pty] = getWeather.getResponse().getBody().getItems().getItem().get(i).getFcstValue();
                        Log.d("sss",pty+"pty는"+PTY[pty]);
                        pty++;
                        break;
                    case "REH":
                        REH[reh] = getWeather.getResponse().getBody().getItems().getItem().get(i).getFcstValue();
                        Log.d("sss",reh+"reh는"+REH[reh]);
                        reh++;
                        break;
                    default:
                        break;

                }



                Log.d("sss","ii"+i);
            }
            for (int ii=0; ii<18;ii++) {
                if(PTY[ii].equals("0")) {
                    adapter.addItem(new Weather(FCSTTIME[ii], TMP[ii], SKY[ii], REH[ii]));
                }else {
                    adapter.addItem(new Weather(FCSTTIME[ii], TMP[ii], String.valueOf(Integer.parseInt(PTY[ii])+4), REH[ii]));
                }

            }
            Log.d("zzz","setw1");
            setWeatherText();
            setSkyImage();
            setWeatherComment();
            Log.d("zzz","setw2");


        }catch (Exception e){
            Log.d("zzz","에러메시지"+e.getMessage());
        }

        recyclerView.setAdapter(adapter);
    }
    void setSkyImage(){
        switch (SKY[0]){
            case "1":
                isky.setImageResource(R.drawable.wgrade1);
                break;
            case "2":
                isky.setImageResource(R.drawable.grade2);
                break;
            case "3":
                isky.setImageResource(R.drawable.wgrade3);
                break;
            case "4":
                isky.setImageResource(R.drawable.wgrade4);
                break;
            default:
                isky.setImageResource(R.drawable.ic_baseline_error_24);
                break;
        }
    }
    @SuppressLint("SetTextI18n")
    void setWeatherText(){
        //tmn.setText(TMN[0]+"도");
        tTMX.setText(TMX[0]+"℃ / "+TMP[0]+"℃");
        tTMP.setText(TMP[0]);
    }
    void setWeatherComment(){
        Log.d("날씨", "날씨는"+PTY[0]+SKY[0]);
        if(PTY[0].equals("0")) {
            switch (SKY[0]){
                case "1":
                    weatherComment.setText("맑고 ");
                    break;
                case "3":
                    weatherComment.setText("구름많고 ");
                    break;
                case "4":
                    weatherComment.setText("흐리고 ");
                    break;
                default:
                    weatherComment.setText("??");
                    break;
            }
        }else {
            switch (PTY[0]){
                case "1":
                    weatherComment.setText("비오는데 ");
                    break;
                case "2":
                    weatherComment.setText("비눈오는데 ");
                    break;
                case "3":
                    weatherComment.setText("눈오는데 ");
                    break;
                case "4":
                    weatherComment.setText("소나기오는데 ");
                    break;
                default:
                    weatherComment.setText(" ??");
                    break;
            }
        }
        if(Integer.parseInt(TMP[0])>=30) weatherComment.append("더워");
        else if(30>Integer.parseInt(TMP[0])&&Integer.parseInt(TMP[0])>=20) weatherComment.append("안추워");
        else if(20>Integer.parseInt(TMP[0])&&Integer.parseInt(TMP[0])>=10) weatherComment.append("쌀쌀해");
        else if(10>Integer.parseInt(TMP[0])) weatherComment.append("추워");
    }

    @Override
    public void showLoadError(String message) {

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onInfoWindowClick(@NonNull @NotNull Marker marker) {

    }
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
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
        LatLng POINT = new LatLng(lat, lon);


        MarkerOptions marker = new MarkerOptions();
        marker.position(POINT);


        googleMap.addMarker(marker);


        //googleMap.setOnInfoWindowClickListener(this);


        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(POINT, 13));
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
                                weatherName=sheet.getCell(2,row).getContents();
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
    private void readTip(){
        try {
            InputStream is = getActivity().getResources().getAssets().open("tip.xls");

            Workbook wb = Workbook.getWorkbook(is);

            if(wb != null){
                Sheet sheet = wb.getSheet(0);
                if(sheet != null){
                    int colTotal = sheet.getColumns();
                    int rowIndexStart = 1;
                    int i=0, j=0;
                    int rowTotal = sheet.getColumn(colTotal-1).length;
                    StringBuilder sb;
                    for(int row=rowIndexStart; row<rowTotal;row++){
                        sb = new StringBuilder();
                        for(int col=0; col<colTotal; col++){
                            String contents = sheet.getCell(col, row).getContents();
                            tipComment[row-1][col] = contents;
                            j++;

                            Log.d("행동강령", row+"행"+col+"열 "+tipComment[row][col]);
                        }
                        i++;
                    }

                }
                is.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}