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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

import com.project.movingclosetapp.memberfragments.MemberMap;
import com.project.movingclosetapp.memberfragments.MyMoyoList;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class MemberMainActivity extends AppCompatActivity {

    FloatingActionButton drawerFab;
    DrawerLayout membermainDrawerLayout;
    NavigationView nav_view;

    FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_main);

        Intent intent = getIntent();
        MemberDTO memberDTO = (MemberDTO) intent.getSerializableExtra("UserInfo");

        fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.memberMainFragment, new MemberMap());
        fragmentTransaction.commit();

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
                    break;

                case R.id.nav_mymoyo:
                    membermainDrawerLayout.closeDrawer(GravityCompat.START);
                    fm = getSupportFragmentManager();
                    FragmentTransaction ft_mymoyo = fm.beginTransaction();
                    ft_mymoyo.replace(R.id.memberMainFragment, new MyMoyoList());
                    ft_mymoyo.commit();
                    break;

                case R.id.nav_viewMap:
                    membermainDrawerLayout.closeDrawer(GravityCompat.START);
                    fm = getSupportFragmentManager();
                    FragmentTransaction ft_viewmap = fm.beginTransaction();
                    ft_viewmap.replace(R.id.memberMainFragment, new MemberMap());
                    ft_viewmap.commit();
                    break;
            }
            return false;
        }
    };

    @Override
    public void onBackPressed() {

        if(membermainDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            membermainDrawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MemberMainActivity.this);

            builder.setTitle("Moving Closet").setMessage("앱을 종료하시겠어요?");

            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id)
                {
                    ActivityCompat.finishAffinity(MemberMainActivity.this);
                    System.exit(0);
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


//        super.onBackPressed();
    }
}