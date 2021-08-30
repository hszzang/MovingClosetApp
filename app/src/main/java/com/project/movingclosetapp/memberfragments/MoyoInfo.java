package com.project.movingclosetapp.memberfragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.movingclosetapp.R;
import com.project.movingclosetapp.models.MoyoDTO;
import com.project.movingclosetapp.models.MoyoUseDTO;

public class MoyoInfo extends Fragment {

    private View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.member_my_moyo_info, container, false);
        if(getArguments() != null) {
//            moyoDTO = (MoyoDTO)getArguments().getSerializable("moyoList");
        }


        return v;
    }
}
