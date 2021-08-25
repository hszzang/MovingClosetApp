package com.project.movingclosetapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class MemberMainActivity extends AppCompatActivity {

    MapView mapView;
    RelativeLayout mapViewContainer;
    FloatingActionButton drawerFab, locationFab;
    DrawerLayout membermainDrawerLayout;
    NavigationView nav_view;

    MapPoint mapPoint;
    LocationManager lm;

    Location nowLocation;
    public double nowLatitude;
    public double nowLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_main);

        Intent intent = getIntent();
        MemberDTO memberDTO = (MemberDTO) intent.getSerializableExtra("UserInfo");

        Toast.makeText(getApplicationContext(), "액티비티 이동 후 " + memberDTO.getName(), Toast.LENGTH_LONG);

        mapView = new MapView(this);
        mapView.setDaumMapApiKey("35d7f6ad978712e086f425ae8ed5753f");

        mapViewContainer = (RelativeLayout) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);

//        mapView.removeAllPOIItems();

//        MapMarker("마커에 찍을 내용", location_detail, 경도, 위도);
//        MapMarker("마커에 찍을 내용", location_detail, 경도, 위도);
//        Handler mHandler = new Handler();
//        mHandler.postDelayed( new Runnable()
//            {
//                public void run() {
//                    //3초 후에 현재위치를 받아오도록 설정 , 바로 시작 시 에러납니다.
//                    mapView.setCurrentLocationTrackingMode(
//                    MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading );
//                }
//            }, 4000 );
//        // 1000 = 1초
        lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        if(PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
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
            Toast.makeText(this, "위치 권한을 허용해주세요.",
                    Toast.LENGTH_LONG).show();
        }



        drawerFab = findViewById(R.id.drawerFab);
        membermainDrawerLayout = findViewById(R.id.membermainDrawerLayout);

        nav_view = findViewById(R.id.nav_view);
        View headerView = nav_view.getHeaderView(0);

        TextView navHeaderWelcome = headerView.findViewById(R.id.navHeaderWelcome);
        navHeaderWelcome.setText(memberDTO.getName() + "님 반갑습니다.");

        Log.i("CheckMemberData", navHeaderWelcome.getText().toString());

        //drawerFAB를 클릭하면 내비게이션뷰가 보이도록하는 클릭리스너
        drawerFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                membermainDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        nav_view.setNavigationItemSelectedListener(navListener);


    }

//    public void Marker(String MakerName, double startX, double startY) {
//        mapPoint = MapPoint.mapPointWithGeoCoord( startY, startX );
//        mapView.setMapCenterPoint( mapPoint, true );
//        //true면 앱 실행 시 애니메이션 효과가 나오고 false면 애니메이션이 나오지않음.
//        MapPOIItem marker = new MapPOIItem(); marker.setItemName(MakerName);
//        // 마커 클릭 시 컨테이너에 담길 내용
//        marker.setMapPoint( mapPoint ); // 기본으로 제공하는 BluePin 마커 모양.
//        marker.setMarkerType( MapPOIItem.MarkerType.RedPin );
//        // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
//        marker.setSelectedMarkerType( MapPOIItem.MarkerType.BluePin );
//        mapView.addPOIItem( marker );
//     }
//
//    public void MapMarker(String MakerName, String detail, double startX, double startY) {
//        mapPoint = MapPoint.mapPointWithGeoCoord( startY, startX );
//        mapView.setMapCenterPoint( mapPoint, true );
//        // true면 앱 실행 시 애니메이션 효과가 나오고 false면 애니메이션이 나오지않음.
//        MapPOIItem marker = new MapPOIItem();
//        marker.setItemName(MakerName+"("+detail+")");
//        // 마커 클릭 시 컨테이너에 담길 내용
//        marker.setMapPoint( mapPoint );
//        // 기본으로 제공하는 BluePin 마커 모양.
//        marker.setMarkerType( MapPOIItem.MarkerType.RedPin );
//        // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
//        marker.setSelectedMarkerType( MapPOIItem.MarkerType.BluePin );
//        mapView.addPOIItem( marker );
//    }


    NavigationView.OnNavigationItemSelectedListener navListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            switch (menuItem.getItemId()) {
                case R.id.nav_logout:

                    AlertDialog.Builder builder = new AlertDialog.Builder(MemberMainActivity.this);

                    builder.setMessage("정말 로그아웃하시겠습니까?");
//                    builder.setTitle("인사말").setMessage("반갑습니다");

                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        {
                            Toast.makeText(getApplicationContext(), "로그아웃되었습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });

                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        { }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
            }
            return false;
        }
    };




}