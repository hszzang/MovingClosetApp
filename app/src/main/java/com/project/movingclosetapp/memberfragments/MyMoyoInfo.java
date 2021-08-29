package com.project.movingclosetapp.memberfragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.project.movingclosetapp.R;
import com.project.movingclosetapp.models.MoyoDTO;
import com.project.movingclosetapp.models.MoyoUseDTO;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class MyMoyoInfo extends Fragment {

    private View v;

    private MapView mapView;
    private RelativeLayout mapViewContainer;
    private MapPoint mapPoint;

    private MoyoDTO moyoDTO;
    private MoyoUseDTO moyoUseDTO;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.member_my_moyo_info, container, false);
        if(getArguments() != null) {
            moyoDTO = (MoyoDTO)getArguments().getSerializable("moyoList");
            moyoUseDTO = (MoyoUseDTO)getArguments().getSerializable("moyoUseList");
        }

        mapView = new MapView(container.getContext());
        mapView.setDaumMapApiKey("35d7f6ad978712e086f425ae8ed5753f");

        mapViewContainer = v.findViewById(R.id.mymoyo_map_view);
        mapViewContainer.addView(mapView);

        double moyoLat = Double.parseDouble(moyoDTO.getM_lat());
        double moyoLon = Double.parseDouble(moyoDTO.getM_lon());

        mapPoint = MapPoint.mapPointWithGeoCoord(moyoLat, moyoLon);
        mapView.setMapCenterPoint(mapPoint, true);

        MapPOIItem marker = new MapPOIItem();
        marker.setItemName(moyoDTO.getM_name());
        marker.setTag(0);
        marker.setMapPoint(mapPoint);
        marker.setMarkerType(MapPOIItem.MarkerType.YellowPin);

        mapView.addPOIItem(marker);

        TextView moyoName = v.findViewById(R.id.mymoyoinfo_moyoName);
        TextView moyoAddr = v.findViewById(R.id.mymoyoinfo_moyoAddr);
        TextView moyoStart = v.findViewById(R.id.mymoyoinfo_moyoStart);
        TextView moyoEnd = v.findViewById(R.id.mymoyoinfo_moyoEnd);
        TextView moyoDday = v.findViewById(R.id.mymoyoinfo_moyoDday);
        TextView moyoDesc = v.findViewById(R.id.mymoyoinfo_moyoDesc);
        TextView moyoUseName = v.findViewById(R.id.mymoyoinfo_moyoUseName);
        TextView moyoUsePhone = v.findViewById(R.id.mymoyoinfo_moyoUsePhone);
        TextView moyoUseEmail = v.findViewById(R.id.mymoyoinfo_moyoUseEmail);
        TextView moyoUseTime = v.findViewById(R.id.mymoyoinfo_moyoUseTime);

        moyoName.setText(moyoDTO.getM_name());
        moyoAddr.setText(moyoDTO.getM_addr());
        moyoStart.setText(moyoDTO.getM_start().substring(0, 10));
        moyoEnd.setText(moyoDTO.getM_end().substring(0, 10));
        moyoDday.setText(moyoDTO.getM_dday().substring(0, 10));
        moyoDesc.setText(moyoDTO.getM_desc());
        moyoUseName.setText(moyoUseDTO.getMu_name());
        moyoUsePhone.setText(moyoUseDTO.getMu_phone());
        moyoUseEmail.setText(moyoUseDTO.getMu_email());
        moyoUseTime.setText(moyoUseDTO.getMu_time());

        return v;
    }
}
