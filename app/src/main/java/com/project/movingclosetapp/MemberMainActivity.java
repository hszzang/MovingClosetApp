package com.project.movingclosetapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.project.movingclosetapp.memberfragments.MemberMap;
import com.project.movingclosetapp.memberfragments.MyMoyoList;
import com.project.movingclosetapp.models.MemberDTO;

public class MemberMainActivity extends AppCompatActivity {

    FloatingActionButton drawerFab;
    DrawerLayout membermainDrawerLayout;
    NavigationView nav_view;

    FragmentManager fm;

    MemberDTO memberDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_main);

        Intent intent = getIntent();
        memberDTO = (MemberDTO) intent.getSerializableExtra("UserInfo");

        Bundle argsMemberMap = new Bundle();
        argsMemberMap.putSerializable("loginMemberInfo", memberDTO);
        MemberMap memberMap = new MemberMap();
        memberMap.setArguments(argsMemberMap);

        fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.memberMainFragment, memberMap);
        fragmentTransaction.commit();

        drawerFab = findViewById(R.id.drawerFab);
        membermainDrawerLayout = findViewById(R.id.membermainDrawerLayout);

        nav_view = findViewById(R.id.nav_view);
        View headerView = nav_view.getHeaderView(0);

        TextView navHeaderWelcome = headerView.findViewById(R.id.navHeaderWelcome);
        navHeaderWelcome.setText(memberDTO.getName() + "??? ???????????????.");

        Log.i("CheckMemberData", navHeaderWelcome.getText().toString());

        //drawerFAB??? ???????????? ????????????????????? ?????????????????? ???????????????
        drawerFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                membermainDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        nav_view.setNavigationItemSelectedListener(navListener);


    }

    NavigationView.OnNavigationItemSelectedListener navListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            switch (menuItem.getItemId()) {
                case R.id.nav_logout:

                    AlertDialog.Builder builder = new AlertDialog.Builder(MemberMainActivity.this);

                    builder.setMessage("?????? ???????????????????????????????");
//                    builder.setTitle("?????????").setMessage("???????????????");

                    builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        {
                            Toast.makeText(getApplicationContext(), "???????????????????????????.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });

                    builder.setNegativeButton("??????", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        { }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    break;

                case R.id.nav_mymoyo:
                    membermainDrawerLayout.closeDrawer(GravityCompat.START);

                    Bundle argsMyMoyoList = new Bundle();
                    argsMyMoyoList.putString("userid", memberDTO.getUserid());
                    MyMoyoList mml = new MyMoyoList();
                    mml.setArguments(argsMyMoyoList);

                    fm = getSupportFragmentManager();
                    FragmentTransaction ft_mymoyo = fm.beginTransaction();
                    ft_mymoyo.replace(R.id.memberMainFragment, mml);
                    ft_mymoyo.addToBackStack(null);
                    ft_mymoyo.commit();
                    break;

                case R.id.nav_viewMap:
                    membermainDrawerLayout.closeDrawer(GravityCompat.START);
                    fm = getSupportFragmentManager();
                    FragmentTransaction ft_viewmap = fm.beginTransaction();
                    ft_viewmap.replace(R.id.memberMainFragment, new MemberMap());
                    ft_viewmap.addToBackStack(null);
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
        else if(getSupportFragmentManager().getBackStackEntryCount() != 0) {
            super.onBackPressed();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MemberMainActivity.this);

            builder.setTitle("Moving Closet").setMessage("?????? ??????????????????????");

            builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id)
                {
                    ActivityCompat.finishAffinity(MemberMainActivity.this);
                    System.exit(0);
                }
            });

            builder.setNegativeButton("??????", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int id)
                { }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }
    }
}