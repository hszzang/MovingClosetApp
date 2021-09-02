package com.project.movingclosetapp.busfragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.project.movingclosetapp.R;
import com.project.movingclosetapp.memberfragments.MemberMap;
import com.project.movingclosetapp.memberfragments.MoyoListViewAdapter;
import com.project.movingclosetapp.models.MemberDTO;
import com.project.movingclosetapp.models.MoyoBusDTO;
import com.project.movingclosetapp.models.MoyoDTO;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.project.movingclosetapp.MainActivity.IP_ADDRESS;

public class BusMap extends Fragment{

    private MapView mapView;
    private RelativeLayout mapViewContainer;
    private TextView bus_nowLocaText;
    private String bus_status;

    private MapPoint mapPoint;

    private View v;
    private MoyoBusDTO moyoBusDTO;
    private MoyoDTO moyoDTO;

    private LocationManager lm;
    private Handler my_location_handler;
    private MapPOIItem moyoMarker;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.bus_map, container, false);

        if(getArguments() != null) {
            moyoBusDTO = (MoyoBusDTO) getArguments().getSerializable("loginBusInfo");
            moyoDTO = (MoyoDTO) getArguments().getSerializable("moyoInfo");
        }

        moyoMarker = new MapPOIItem();
        moyoMarker.setItemName("목적지");
        moyoMarker.setTag(0);
        moyoMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(
                Double.parseDouble(moyoDTO.getM_lat()), Double.parseDouble(moyoDTO.getM_lon())));
        moyoMarker.setMarkerType(MapPOIItem.MarkerType.YellowPin);

        mapView = new MapView(container.getContext());
        mapView.setDaumMapApiKey("35d7f6ad978712e086f425ae8ed5753f");

        mapViewContainer = v.findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);

        bus_nowLocaText = v.findViewById(R.id.bus_nowLocaText);
        bus_status = "출발대기";

        my_location_handler = new Handler();
        lm = (LocationManager) container.getContext().getSystemService(Context.LOCATION_SERVICE);

        my_location_handler.postDelayed(nowLocationRunnable, 500);

        Button bus_statusBtn = v.findViewById(R.id.bus_statusBtn);
        bus_statusBtn.setOnClickListener(statusBtnlistener);


        return v;
    }

    public void setMapCenterLocation (double latitude, double longitude) {

        mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude);
        mapView.setMapCenterPoint(mapPoint, true);

        mapView.removeAllPOIItems();
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName("현재 위치");
        marker.setTag(0);
        marker.setMapPoint(mapPoint);
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin);

        mapView.addPOIItem(marker);
        mapView.addPOIItem(moyoMarker);
        final Geocoder geocoder = new Geocoder(getContext());

        try {
            List<Address> resultList = geocoder.getFromLocation(
                    latitude, longitude, 1
            );
            bus_nowLocaText.setText(resultList.get(0).getAddressLine(0).substring(5));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        new AsyncHttpServer().execute(
                //현재 접속된 wifi의 ipv4 주소로 변경해줘야 합니다.
                "http://"+ IP_ADDRESS +":8081/movingcloset/android/AndMoyoBusLocation.do",
                "latitude=" + latitude,
                "longitude=" + longitude,
                "mb_addr=" + bus_nowLocaText.getText().toString(),
                "mb_status=" + bus_status,
                "busid=" + moyoBusDTO.getBusid()

        );

        if(bus_status.equals("출발")){
            my_location_handler.postDelayed(nowLocationRunnable, 7000);
        }
    }

    Runnable nowLocationRunnable = new Runnable() {
        @Override
        public void run() {
            if(ContextCompat.checkSelfPermission(v.getContext().getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, 0);
            }
            else {
                Log.i("FindLocation", "현재위치 찾기 시작");
                Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                new LocationListener().onLocationChanged(location);

                if(location == null) {
                    location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
                if(location != null) {
                    setMapCenterLocation(location.getLatitude(), location.getLongitude());
                }
                else {
                    Log.i("FindLocation", "현재위치 찾기 실패, 재검색합니다");
                    my_location_handler.postDelayed(this, 500);
                }
            }
        }
    };

    class AsyncHttpServer extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            StringBuffer receiveData = new StringBuffer();

            try{

                URL url = new URL(strings[0]); //파라미터1 : 요청URL
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                OutputStream out = conn.getOutputStream();
                out.write(strings[1].getBytes()); //파라미터2 : 위도
                out.write("&".getBytes()); //&를 사용하여 쿼리스트링 형태로 만들어준다.
                out.write(strings[2].getBytes()); //파라미터3 : 경도
                out.write("&".getBytes()); //&를 사용하여 쿼리스트링 형태로 만들어준다.
                out.write(strings[3].getBytes()); //파라미터4 : 주소
                out.write("&".getBytes()); //&를 사용하여 쿼리스트링 형태로 만들어준다.
                out.write(strings[4].getBytes()); //파라미터5 : 버스상태
                out.write("&".getBytes()); //&를 사용하여 쿼리스트링 형태로 만들어준다.
                out.write(strings[5].getBytes()); //파라미터6 : 버스아이디
                out.flush();
                out.close();

                Log.i("MyMoyoListURL", url.toString());

                if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    //Rest API 서버에서 내려주는 JSON데이터를 읽어서 저장한다.
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), "UTF-8")
                    );

                    Log.i("MyMoyoListConnect", "HTTP_OK");
                    String data;
                    while((data = reader.readLine()) != null) {
                        receiveData.append(data + "\r\n");
                    }
                    reader.close();
                }
                else {
                    Log.i("MyMoyoListConnect", "HTTP_OK 안됨. 연결 실패.");
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            //저장된 내용을 로그로 출력한 후 onPostExecute()로 반환한다.
            Log.i("MyMoyoList", receiveData.toString());

            return receiveData.toString();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                //JSON객체를 1차로 파싱함
                JSONObject jsonObject = new JSONObject(s);

                //로그인 성공 여부를 판단해야 함.
                int success = Integer.parseInt(jsonObject.getString("isMoyoBus"));
                Log.i("MoyoList", "success: " + success);

                Log.i("MoyoListSuccess", "불러온 모여 개수 : " + success);

                if(success == 1) {
                }
                else {
                    Log.i("MyMoyoList", "모여버스 정보 전달 실패");
                    Toast.makeText(getContext(), "네트워크 연결을 확인해주세요", Toast.LENGTH_LONG).show();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                Log.i("MyMoyoList", "예외 발생");
            }
        }
    }

    View.OnClickListener statusBtnlistener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Button btn = view.findViewById(R.id.bus_statusBtn);
            if(btn.getText().toString().equals("출발하기") && bus_status.equals("출발대기")) {

                bus_status = "출발";
                my_location_handler.postDelayed(nowLocationRunnable, 500);
                Toast.makeText(getContext(), "버스 출발 처리되었습니다.", Toast.LENGTH_SHORT);
                btn.setText("목적지 도착");
                btn.setBackgroundColor(Color.parseColor("#FF6C2F"));
                btn.setTextColor(Color.WHITE);
            }
            else if(btn.getText().toString().equals("목적지 도착") && bus_status.equals("출발")) {

                bus_status = "도착";
                my_location_handler.postDelayed(nowLocationRunnable, 500);
                Toast.makeText(getContext(), "목적지에 도착했습니다.", Toast.LENGTH_SHORT);
                btn.setText("영업시작");
                btn.setBackgroundColor(Color.WHITE);
                btn.setTextColor(Color.parseColor("#FF6C2F"));
            }
            else if(btn.getText().toString().equals("영업시작") && bus_status.equals("도착")) {

                bus_status = "영업중";
                my_location_handler.postDelayed(nowLocationRunnable, 500);
                Toast.makeText(getContext(), "영업을 시작합니다.", Toast.LENGTH_SHORT);
                btn.setText("영업종료");
                btn.setBackgroundColor(Color.parseColor("#FF6C2F"));
                btn.setTextColor(Color.WHITE);
            }
            else if(btn.getText().toString().equals("영업종료") && bus_status.equals("영업중")) {

                bus_status = "영업종료";
                my_location_handler.postDelayed(nowLocationRunnable, 500);
                Toast.makeText(getContext(), "영업을 종료합니다.", Toast.LENGTH_SHORT);
                btn.setBackgroundColor(Color.parseColor("#BDBDBD"));
                btn.setTextColor(Color.WHITE);
                btn.setClickable(false);
            }
        }
    };
}
