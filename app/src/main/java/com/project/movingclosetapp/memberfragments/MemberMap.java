package com.project.movingclosetapp.memberfragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.project.movingclosetapp.R;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class MemberMap extends Fragment {


    private MapView mapView;
    private RelativeLayout mapViewContainer;
    FloatingActionButton locationFab;

    private MapPoint mapPoint;
    private LocationManager lm;

    public Location nowLocation;
    public double nowLatitude;
    public double nowLongitude;

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

        lm = (LocationManager) container.getContext().getSystemService(Context.LOCATION_SERVICE);

        if(PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(container.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            try {

                nowLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                nowLatitude = nowLocation.getLatitude();
                nowLongitude = nowLocation.getLongitude();

                mapPoint = MapPoint.mapPointWithGeoCoord(nowLatitude, nowLongitude);
                mapView.setMapCenterPoint(mapPoint, true);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            Toast.makeText(container.getContext(), "위치 권한을 허용해주세요.",
                    Toast.LENGTH_LONG).show();
        }


        return v;
    }
}