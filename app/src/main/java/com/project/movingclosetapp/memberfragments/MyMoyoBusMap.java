package com.project.movingclosetapp.memberfragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.project.movingclosetapp.R;
import com.project.movingclosetapp.models.MoyoBusDTO;
import com.project.movingclosetapp.models.MoyoDTO;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static com.project.movingclosetapp.MainActivity.IP_ADDRESS;

public class MyMoyoBusMap extends Fragment{

    private MapView mapView;
    private RelativeLayout mapViewContainer;
    private TextView bus_number, bus_nowLocaText;

    private MapPoint mapPoint;

    private View v;
    private MoyoBusDTO moyoBusDTO;

    private Handler my_location_handler;
    private MapPOIItem moyoMarker;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.member_my_moyobus_map, container, false);

        if(getArguments() != null) {
            moyoBusDTO = (MoyoBusDTO) getArguments().getSerializable("moyoBusDTO");
        }

        moyoMarker = new MapPOIItem();
        moyoMarker.setItemName("현재 버스 위치");
        moyoMarker.setTag(0);
        moyoMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(
                Double.parseDouble(moyoBusDTO.getMb_lat()), Double.parseDouble(moyoBusDTO.getMb_lon())));
        moyoMarker.setMarkerType(MapPOIItem.MarkerType.YellowPin);

        mapView = new MapView(container.getContext());
        mapView.setDaumMapApiKey("35d7f6ad978712e086f425ae8ed5753f");

        mapViewContainer = v.findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);

        bus_number = v.findViewById(R.id.bus_number);
        bus_nowLocaText = v.findViewById(R.id.bus_nowLocaText);

        bus_number.setText(moyoBusDTO.getMb_num());
        bus_nowLocaText.setText(moyoBusDTO.getMb_addr());

        my_location_handler = new Handler();
        my_location_handler.postDelayed(nowLocationRunnable, 500);

        return v;
    }

    public void setMapCenterLocation (double latitude, double longitude) {

        mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude);
        mapView.setMapCenterPoint(mapPoint, true);

        mapView.removeAllPOIItems();
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName("현재 버스 위치");
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

        my_location_handler.postDelayed(nowLocationRunnable, 7000);
    }

    Runnable nowLocationRunnable = new Runnable() {
        @Override
        public void run() {

            new AsyncHttpServer().execute(
                    //현재 접속된 wifi의 ipv4 주소로 변경해줘야 합니다.
                    "http://"+ IP_ADDRESS +":8081/movingcloset/android/AndMyBusLoca.do",
                    "m_idx=" + moyoBusDTO.getM_idx()
            );
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
                out.write(strings[1].getBytes()); //파라미터2 : 모여인덱스
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
                Log.i("MyMoyoList", "success: " + success);

                Log.i("MyMoyoListSuccess", "불러온 모여 개수 : " + success);

                if(success >= 1) {

                    moyoBusDTO.setM_idx(jsonObject.getJSONObject("moyoBusDTO").getString("m_idx"));
                    moyoBusDTO.setBusid(jsonObject.getJSONObject("moyoBusDTO").getString("busid"));
                    moyoBusDTO.setBuspass(jsonObject.getJSONObject("moyoBusDTO").getString("buspass"));
                    moyoBusDTO.setMb_lat(jsonObject.getJSONObject("moyoBusDTO").getString("mb_lat"));
                    moyoBusDTO.setMb_lon(jsonObject.getJSONObject("moyoBusDTO").getString("mb_lon"));
                    moyoBusDTO.setMb_num(jsonObject.getJSONObject("moyoBusDTO").getString("mb_num"));
                    moyoBusDTO.setMb_status(jsonObject.getJSONObject("moyoBusDTO").getString("mb_status"));
                    moyoBusDTO.setMb_addr(jsonObject.getJSONObject("moyoBusDTO").getString("mb_addr"));
                    moyoBusDTO.setMb_lastupdate(jsonObject.getJSONObject("moyoBusDTO").getString("mb_lastupdate"));

                }
                else {
                    Log.i("MyMoyoList", "내 모여목록 불러오기 실패");
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                Log.i("MyMoyoList", "예외 발생");
            }

            bus_number.setText(moyoBusDTO.getMb_num());
            bus_nowLocaText.setText(moyoBusDTO.getMb_addr());

            setMapCenterLocation(Double.parseDouble(moyoBusDTO.getMb_lat()), Double.parseDouble(moyoBusDTO.getMb_lon()));
        }
    }

}
