package com.project.movingclosetapp.memberfragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.project.movingclosetapp.MemberMainActivity;
import com.project.movingclosetapp.R;
import com.project.movingclosetapp.models.MemberDTO;
import com.project.movingclosetapp.models.MoyoDTO;
import com.project.movingclosetapp.models.MoyoUseDTO;

import net.daum.android.map.MapEngineManager;
import net.daum.mf.map.api.CalloutBalloonAdapter;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.project.movingclosetapp.MainActivity.IP_ADDRESS;

public class MemberMap extends Fragment {


    private MapView mapView;
    private RelativeLayout mapViewContainer;

    private MapPoint mapPoint;

    private View v;
    private MemberDTO memberDTO;
    private ArrayList<MoyoDTO> moyoDTOList;
    private ListView moyoListView;
    private MoyoListViewAdapter adapter;

    LocationManager lm;
    Handler my_location_handler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.member_map, container, false);

        if(getArguments() != null) {
            memberDTO = (MemberDTO) getArguments().getSerializable("loginMemberInfo");
        }

        mapView = new MapView(container.getContext());
        mapView.setDaumMapApiKey("35d7f6ad978712e086f425ae8ed5753f");

        mapViewContainer = v.findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);

        Log.i("FragmentMemberMap", "멤버 맵 프래그먼트입니다.");

        moyoListView = v.findViewById(R.id.member_moyoListView);
//        Log.i("CountMoyoTest", countMoyo.getText().toString());
        moyoDTOList = new ArrayList<>();

        my_location_handler = new Handler();
        lm = (LocationManager) container.getContext().getSystemService(Context.LOCATION_SERVICE);

        my_location_handler.postDelayed(nowLocationRunnable, 500);

        FloatingActionButton member_homeLoca = v.findViewById(R.id.member_homeLoca);
        FloatingActionButton member_nowLoca = v.findViewById(R.id.member_nowLoca);

        final Geocoder geocoder = new Geocoder(container.getContext());
        member_homeLoca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    List<Address> list = geocoder.getFromLocationName(memberDTO.getAddr(), 10);
                    if(list == null) {
                        Toast.makeText(container.getContext(), "주소를 불러오는 데 실패했습니다.", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Address address = list.get(0);
                        setMapCenterLocation(address.getLatitude(), address.getLongitude());
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        member_nowLoca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                my_location_handler.postDelayed(nowLocationRunnable, 500);
            }
        });

        //모여 리스트뷰 부분을 모여인포로 변경
//        FragmentManager fm = getActivity().getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fm.beginTransaction();
//        fragmentTransaction.add(R.id.member_moyoList, new MoyoInfo());
//        fragmentTransaction.addToBackStack(null);
//        fragmentTransaction.commit();

        return v;
    }

    public void setMapCenterLocation (double latitude, double longitude) {

        mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude);
        mapView.setMapCenterPoint(mapPoint, true);

        mapView.removeAllPOIItems();
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName("중심 위치");
        marker.setTag(0);
        marker.setMapPoint(mapPoint);
        marker.setMarkerType(MapPOIItem.MarkerType.RedPin);

        mapView.setCalloutBalloonAdapter(new CustomCallouBalloonAdapter());
        mapView.setPOIItemEventListener(poiItemEventListener);
        mapView.addPOIItem(marker);

        moyoDTOList.clear();
        new AsyncHttpServer().execute(
                //현재 접속된 wifi의 ipv4 주소로 변경해줘야 합니다.
                "http://"+ IP_ADDRESS +":8081/movingcloset/android/AndMoyoList.do",
                "latitude=" + latitude,
                "longitude=" + longitude
        );

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

    class CustomCallouBalloonAdapter implements CalloutBalloonAdapter {

        private final View mCalloutBalloon = null;

        @Override
        public View getCalloutBalloon(MapPOIItem mapPOIItem) {
            return null;
        }

        @Override
        public View getPressedCalloutBalloon(MapPOIItem mapPOIItem) {
            return null;
        }
    }

    MapView.POIItemEventListener poiItemEventListener = new MapView.POIItemEventListener() {

        @Override
        public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
            Log.i("MapBalloonTest0",  mapView.getMapCenterPoint() + "이 클릭되었습니다");
            Log.i("MapBalloonTest0", mapPOIItem.getItemName() + "이 클릭되었습니다");
        }

        @Override
        public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
            Log.i("MapBalloonTest1",  mapView.getMapCenterPoint() + "이 클릭되었습니다");
            Log.i("MapBalloonTest1", mapPOIItem.getItemName() + "이 클릭되었습니다");
        }

        @Override
        public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
            Log.i("MapBalloonTest2",  mapView.getMapCenterPoint() + "이 클릭되었습니다");
            Log.i("MapBalloonTest2", mapPOIItem.getItemName() + "이 클릭되었습니다");
            Log.i("MapBalloonTest2", calloutBalloonButtonType + "벌룬타입");
        }

        @Override
        public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

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
                int success = Integer.parseInt(jsonObject.getString("isMoyo"));
                Log.i("MoyoList", "success: " + success);

                Log.i("MoyoListSuccess", "불러온 모여 개수 : " + success);

                if(success >= 1) {
                    Log.i("MyMoyoList", "내 모여목록 불러오기 성공");

                    JSONArray moyoListArray = jsonObject.getJSONArray("moyoList");

                    for(int i = 0; i < moyoListArray.length(); i++) {

                        JSONObject moyoListObject = moyoListArray.getJSONObject(i);

                        Log.i("CheckMoyoListParse", moyoListObject.getString("m_idx"));
                        MoyoDTO moyoDTO = new MoyoDTO(
                                moyoListObject.getString("m_idx"),
                                moyoListObject.getString("m_name"),
                                moyoListObject.getString("m_addr"),
                                moyoListObject.getString("m_lat"),
                                moyoListObject.getString("m_lon"),
                                moyoListObject.getString("m_goal"),
                                moyoListObject.getString("m_dday"),
                                moyoListObject.getString("m_desc"),
                                moyoListObject.getString("m_start"),
                                moyoListObject.getString("m_end"),
                                moyoListObject.getString("m_status"),
                                moyoListObject.getString("m_ofile"),
                                moyoListObject.getString("m_sfile")
                        );

                        moyoDTOList.add(moyoDTO);

                    }
                }
                else {
                    Log.i("MyMoyoList", "내 모여목록 불러오기 실패");
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                Log.i("MyMoyoList", "예외 발생");
            }
            adapter = new MoyoListViewAdapter();

            for(MoyoDTO m : moyoDTOList) {

                String startdate = m.getM_start().substring(0, 10);
                String enddate = m.getM_end().substring(0, 10);
                String ddaydate = m.getM_dday().substring(0, 10);

                long end_d_day = 0;
//                try {
//                    SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
//
//                    Date endDate = format.parse(enddate);
//                    Date nowDate = format.parse(new Date().toString());
//
//                    long calDate = endDate.getTime() - nowDate.getTime();
//
//                    end_d_day = calDate / (24 * 60 * 60 * 1000);
//                    end_d_day = Math.abs(end_d_day);
//                }
//                catch (Exception e) {
//                    e.printStackTrace();
//                }
                MapPOIItem markerItem = new MapPOIItem();
                markerItem.setItemName(m.getM_name());
                markerItem.setTag(0);
                markerItem.setMapPoint(
                        MapPoint.mapPointWithGeoCoord(
                                Double.parseDouble(m.getM_lat()), Double.parseDouble(m.getM_lon())));
                markerItem.setMarkerType(MapPOIItem.MarkerType.YellowPin);

                mapView.setCalloutBalloonAdapter(new CustomCallouBalloonAdapter());
                mapView.setPOIItemEventListener(poiItemEventListener);
                mapView.addPOIItem(markerItem);

                adapter.addItem(m.getM_name(), m.getM_addr(),
                        startdate + " 부터 " + enddate + " 까지 모여!",
                        "모여DAY : " + ddaydate, "D - " + end_d_day);
            }

            if(moyoDTOList.size() == 0 || moyoDTOList.isEmpty()) {
                adapter.addItem("신청한 모여가 없습니다.", "모여 신청 후 우리동네에서 쇼핑을 즐기세요!",
                        " ",
                        " " , " ");
            }
            else {
//                countMoyo.setText("신청한 모여가 " + moyoDTOList.size() + "개 있습니다.");
            }

            moyoListView.setAdapter(adapter);
        }
    }

}