package com.project.movingclosetapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import net.daum.mf.map.api.MapView;

public class MemberMainActivity extends AppCompatActivity{

    MapView mapView;
    RelativeLayout mapViewContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_main);

        Intent intent = getIntent();
        MemberDTO memberDTO = (MemberDTO)intent.getSerializableExtra("UserInfo");

        Toast.makeText(getApplicationContext(), "액티비티 이동 후 " + memberDTO.getName(), Toast.LENGTH_LONG);

        mapView = new MapView(this);
        mapView.setDaumMapApiKey("35d7f6ad978712e086f425ae8ed5753f");

        mapViewContainer = (RelativeLayout) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);

    }

}