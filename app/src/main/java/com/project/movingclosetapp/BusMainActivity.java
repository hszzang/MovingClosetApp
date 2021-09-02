package com.project.movingclosetapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.project.movingclosetapp.busfragments.BusMap;
import com.project.movingclosetapp.memberfragments.MemberMap;
import com.project.movingclosetapp.memberfragments.MyMoyoList;
import com.project.movingclosetapp.models.MemberDTO;
import com.project.movingclosetapp.models.MoyoBusDTO;
import com.project.movingclosetapp.models.MoyoDTO;

public class BusMainActivity extends AppCompatActivity {

    FloatingActionButton drawerFab;
    DrawerLayout busmainDrawerLayout;
    NavigationView bus_nav_view;

    MoyoBusDTO moyoBusDTO;
    MoyoDTO moyoDTO;

    FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_main);

        Intent intent = getIntent();
        moyoBusDTO = (MoyoBusDTO) intent.getSerializableExtra("busInfo");
        moyoDTO = (MoyoDTO) intent.getSerializableExtra("moyoInfo");

        Bundle argsBusMap = new Bundle();
        argsBusMap.putSerializable("loginBusInfo", moyoBusDTO);
        argsBusMap.putSerializable("moyoInfo", moyoDTO);
        BusMap busMap = new BusMap();
        busMap.setArguments(argsBusMap);

        fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.busMainFragment, busMap);
        fragmentTransaction.commit();

        drawerFab = findViewById(R.id.drawerFab);
        busmainDrawerLayout = findViewById(R.id.busmainDrawerLayout);

        bus_nav_view = findViewById(R.id.bus_nav_view);
        View headerView = bus_nav_view.getHeaderView(0);

        TextView navHeaderWelcome = headerView.findViewById(R.id.navHeaderWelcome);
        navHeaderWelcome.setText(moyoBusDTO.getMb_num() + " 버스입니다.");

//        Log.i("CheckMemberData", navHeaderWelcome.getText().toString());

        //drawerFAB를 클릭하면 내비게이션뷰가 보이도록하는 클릭리스너
        drawerFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                busmainDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        bus_nav_view.setNavigationItemSelectedListener(navListener);


    }

    NavigationView.OnNavigationItemSelectedListener navListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            switch (menuItem.getItemId()) {
                case R.id.nav_logout:

                    AlertDialog.Builder builder = new AlertDialog.Builder(BusMainActivity.this);

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

//                case R.id.nav_find:
//
//                    Intent intent = new Intent(Intent.ACTION_VIEW,
//                            Uri.parse("kakaomap://route?sp=37.537229,127.005515&ep=37.4979502,127.0276368&by=CAR"));
//                    startActivity(intent);
//
//                    break;

//
//                case R.id.nav_viewMap:
//                    membermainDrawerLayout.closeDrawer(GravityCompat.START);
//                    fm = getSupportFragmentManager();
//                    FragmentTransaction ft_viewmap = fm.beginTransaction();
//                    ft_viewmap.replace(R.id.memberMainFragment, new MemberMap());
//                    ft_viewmap.addToBackStack(null);
//                    ft_viewmap.commit();
//                    break;
            }
            return false;
        }
    };

    @Override
    public void onBackPressed() {

        if(busmainDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            busmainDrawerLayout.closeDrawer(GravityCompat.START);
        }
        else if(getSupportFragmentManager().getBackStackEntryCount() != 0) {
            super.onBackPressed();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(BusMainActivity.this);

            builder.setTitle("Moving Closet").setMessage("앱을 종료하시겠어요?");

            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id)
                {
                    ActivityCompat.finishAffinity(BusMainActivity.this);
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
    }
}