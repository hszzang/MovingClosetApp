package com.project.movingclosetapp.memberfragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.project.movingclosetapp.R;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class MyMoyoInfo extends Fragment {

    private View v;

    private MapView mapView;
    private RelativeLayout mapViewContainer;
    private MapPoint mapPoint;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.member_my_moyo_info, container, false);
        if(getArguments() != null) {
//            userid = getArguments().getString("userid");
        }

        mapView = new MapView(container.getContext());
        mapView.setDaumMapApiKey("35d7f6ad978712e086f425ae8ed5753f");

        mapViewContainer = v.findViewById(R.id.mymoyo_map_view);
        mapViewContainer.addView(mapView);

//        mapPoint = MapPoint.mapPointWithGeoCoord(nowLatitude, nowLongitude);
        mapView.setMapCenterPoint(mapPoint, true);




        return v;
    }
}
