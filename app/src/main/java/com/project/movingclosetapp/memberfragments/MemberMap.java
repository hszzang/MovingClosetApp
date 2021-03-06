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

        Log.i("FragmentMemberMap", "?????? ??? ????????????????????????.");

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
                        Toast.makeText(container.getContext(), "????????? ???????????? ??? ??????????????????.", Toast.LENGTH_LONG).show();
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

        //?????? ???????????? ????????? ??????????????? ??????
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
        marker.setItemName("?????? ??????");
        marker.setTag(0);
        marker.setMapPoint(mapPoint);
        marker.setMarkerType(MapPOIItem.MarkerType.RedPin);

        mapView.setCalloutBalloonAdapter(new CustomCallouBalloonAdapter());
        mapView.setPOIItemEventListener(poiItemEventListener);
        mapView.addPOIItem(marker);

        moyoDTOList.clear();
        new AsyncHttpServer().execute(
                //?????? ????????? wifi??? ipv4 ????????? ??????????????? ?????????.
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
                Log.i("FindLocation", "???????????? ?????? ??????");
                Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if(location == null) {
                    location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
                if(location != null) {
                    setMapCenterLocation(location.getLatitude(), location.getLongitude());
                }
                else {
                    Log.i("FindLocation", "???????????? ?????? ??????, ??????????????????");
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
            Log.i("MapBalloonTest0",  mapView.getMapCenterPoint() + "??? ?????????????????????");
            Log.i("MapBalloonTest0", mapPOIItem.getItemName() + "??? ?????????????????????");
        }

        @Override
        public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
            Log.i("MapBalloonTest1",  mapView.getMapCenterPoint() + "??? ?????????????????????");
            Log.i("MapBalloonTest1", mapPOIItem.getItemName() + "??? ?????????????????????");
        }

        @Override
        public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
            Log.i("MapBalloonTest2",  mapView.getMapCenterPoint() + "??? ?????????????????????");
            Log.i("MapBalloonTest2", mapPOIItem.getItemName() + "??? ?????????????????????");
            Log.i("MapBalloonTest2", calloutBalloonButtonType + "????????????");
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

                URL url = new URL(strings[0]); //????????????1 : ??????URL
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                OutputStream out = conn.getOutputStream();
                out.write(strings[1].getBytes()); //????????????2 : ??????
                out.write("&".getBytes()); //&??? ???????????? ??????????????? ????????? ???????????????.
                out.write(strings[2].getBytes()); //????????????3 : ??????
                out.flush();
                out.close();

                Log.i("MyMoyoListURL", url.toString());

                if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    //Rest API ???????????? ???????????? JSON???????????? ????????? ????????????.
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
                    Log.i("MyMoyoListConnect", "HTTP_OK ??????. ?????? ??????.");
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            //????????? ????????? ????????? ????????? ??? onPostExecute()??? ????????????.
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
                //JSON????????? 1?????? ?????????
                JSONObject jsonObject = new JSONObject(s);

                //????????? ?????? ????????? ???????????? ???.
                int success = Integer.parseInt(jsonObject.getString("isMoyo"));
                Log.i("MoyoList", "success: " + success);

                Log.i("MoyoListSuccess", "????????? ?????? ?????? : " + success);

                if(success >= 1) {
                    Log.i("MyMoyoList", "??? ???????????? ???????????? ??????");

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
                    Log.i("MyMoyoList", "??? ???????????? ???????????? ??????");
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                Log.i("MyMoyoList", "?????? ??????");
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
                        startdate + " ?????? " + enddate + " ?????? ??????!",
                        "??????DAY : " + ddaydate, "membermap" );
            }

            if(moyoDTOList.size() == 0 || moyoDTOList.isEmpty()) {
                adapter.addItem("????????? ????????? ????????????.", "Moving Closet",
                        " ",
                        " " , " ");
            }
            else {
//                countMoyo.setText("????????? ????????? " + moyoDTOList.size() + "??? ????????????.");
            }

            moyoListView.setAdapter(adapter);
        }
    }

}