package com.project.movingclosetapp.memberfragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.project.movingclosetapp.MemberMainActivity;
import com.project.movingclosetapp.R;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class MemberMap extends Fragment {


    private MapView mapView;
    private RelativeLayout mapViewContainer;
    FloatingActionButton locationFab;

    private MapPoint mapPoint;

    private View v;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {


        v = inflater.inflate(R.layout.member_map, container, false);
        mapView = new MapView(container.getContext());
        mapView.setDaumMapApiKey("35d7f6ad978712e086f425ae8ed5753f");

        mapViewContainer = v.findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);

        Log.i("FragmentMemberMap", "멤버 맵 프래그먼트입니다.");


        final Handler my_location_handler = new Handler();
        final LocationManager lm = (LocationManager) container.getContext().getSystemService(Context.LOCATION_SERVICE);

        my_location_handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(ContextCompat.checkSelfPermission(container.getContext().getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, 0);
                }
                else {
                    Log.i("FindLocation", "현재위치 찾기 시작");
                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if(location == null) {
                        location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                    if(location != null) {
                        double nowLatitude = location.getLatitude();
                        double nowLongitude = location.getLongitude();
                        mapPoint = MapPoint.mapPointWithGeoCoord(nowLatitude, nowLongitude);
                        mapView.setMapCenterPoint(mapPoint, true);

                        MapPOIItem marker = new MapPOIItem();
                        marker.setItemName("현재 위치");
                        marker.setTag(0);
                        marker.setMapPoint(mapPoint);
                        marker.setMarkerType(MapPOIItem.MarkerType.YellowPin);

                        mapView.addPOIItem(marker);
                    }
                    else {
                        Log.i("FindLocation", "현재위치 찾기 실패, 재검색합니다");
                        my_location_handler.postDelayed(this, 500);
                    }
                }
            }
        }, 500);






        return v;
    }


}