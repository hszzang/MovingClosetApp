package com.project.movingclosetapp.memberfragments;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.project.movingclosetapp.R;
import com.project.movingclosetapp.models.MoyoListViewItem;

import java.util.ArrayList;

public class MoyoListViewAdapter extends BaseAdapter {

    private ArrayList<MoyoListViewItem> moyoListViewItems = new ArrayList<MoyoListViewItem>();

    public MoyoListViewAdapter() {}

    @Override
    public int getCount() {
        return moyoListViewItems.size();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final int pos = i;
        final Context context = viewGroup.getContext();

        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.moyolistview, viewGroup, false);
        }

        TextView moyoName = view.findViewById(R.id.listview_moyoName);
        TextView moyoAddr = view.findViewById(R.id.listview_moyoAddr);
        TextView moyoStartEnd = view.findViewById(R.id.listview_moyoStartEnd);
        TextView moyoDday = view.findViewById(R.id.listview_moyoDday);
        Button moyoStatus = view.findViewById(R.id.listview_moyoStatus);

        MoyoListViewItem moyoListViewItem = moyoListViewItems.get(i);

        moyoName.setText(moyoListViewItem.getMoyoName());
        moyoAddr.setText(moyoListViewItem.getMoyoAddr());
        moyoStartEnd.setText(moyoListViewItem.getMoyoStartEnd());
        moyoDday.setText(moyoListViewItem.getMoyoDday());
        moyoStatus.setText(moyoListViewItem.getMoyoStatus());

        if(moyoDday.getText().equals(" ")) {
            moyoName.setGravity(Gravity.CENTER);
            moyoAddr.setGravity(Gravity.CENTER);
            moyoStatus.setVisibility(View.INVISIBLE);
        }

        if(moyoStatus.getText().equals("membermap")) {
            moyoStatus.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        return moyoListViewItems.get(i);
    }

    public void addItem(String moyoName, String moyoAddr, String moyoStartEnd, String moyoDday, String moyoStatus) {

        MoyoListViewItem item = new MoyoListViewItem();

        Log.i("CheckMoyoListOnAdapter", moyoName+moyoAddr+moyoStartEnd+moyoDday+moyoStatus);

        item.setMoyoName(moyoName);
        item.setMoyoAddr(moyoAddr);
        item.setMoyoStartEnd(moyoStartEnd);
        item.setMoyoDday(moyoDday);
        item.setMoyoStatus(moyoStatus);


        moyoListViewItems.add(item);
    }



}
