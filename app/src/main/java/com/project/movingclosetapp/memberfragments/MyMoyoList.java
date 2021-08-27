package com.project.movingclosetapp.memberfragments;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.project.movingclosetapp.R;

public class MyMoyoList extends Fragment {


    private View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.activity_my_moyo_list, container, false);

        String[] moyo_list = {"list1", "list2"};

        Log.i("FragmentMyMoyoList", "여기는 마이모여리스트 프래그먼트입니다.");


        ArrayAdapter arrayAdapter = new ArrayAdapter(container.getContext(), android.R.layout.simple_list_item_1, moyo_list);


        ListView moyoListView = v.findViewById(R.id.moyoListView);
        moyoListView.setAdapter(arrayAdapter);

        return v;
    }
}