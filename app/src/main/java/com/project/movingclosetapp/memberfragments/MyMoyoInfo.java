package com.project.movingclosetapp.memberfragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.project.movingclosetapp.R;
import com.project.movingclosetapp.models.MoyoBusDTO;
import com.project.movingclosetapp.models.MoyoDTO;
import com.project.movingclosetapp.models.MoyoUseDTO;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.project.movingclosetapp.MainActivity.IP_ADDRESS;

public class MyMoyoInfo extends Fragment {

    private View v;

    private MapView mapView;
    private RelativeLayout mapViewContainer;
    private MapPoint mapPoint;

    private MoyoDTO moyoDTO;
    private MoyoUseDTO moyoUseDTO;
    private MoyoBusDTO moyoBusDTO;

    private FloatingActionButton trackingMyMoyoBus;

    FragmentManager fm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.member_my_moyo_info, container, false);
        if(getArguments() != null) {
            moyoDTO = (MoyoDTO)getArguments().getSerializable("moyoList");
            moyoUseDTO = (MoyoUseDTO)getArguments().getSerializable("moyoUseList");
        }

        moyoBusDTO = new MoyoBusDTO();

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
        trackingMyMoyoBus = v.findViewById(R.id.trackingMyMoyoBus);
        trackingMyMoyoBus.setOnClickListener(busBtnListener);

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

        new AsyncHttpServer().execute(
                //현재 접속된 wifi의 ipv4 주소로 변경해줘야 합니다.
                "http://"+ IP_ADDRESS +":8081/movingcloset/android/AndMyBusLoca.do",
                "m_idx=" + moyoDTO.getM_idx()
        );


        return v;
    }

    class AsyncHttpServer extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            StringBuffer receiveData = new StringBuffer();

            try{

                URL url = new URL(strings[0]); //파라미터1 : 요청URL
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                OutputStream out = conn.getOutputStream();
                out.write(strings[1].getBytes()); //파라미터2 : 모여인덱스
                out.flush();
                out.close();

                Log.i("MyMoyoListURL", url.toString());

                if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    //Rest API 서버에서 내려주는 JSON데이터를 읽어서 저장한다.
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), "UTF-8")
                    );

                    Log.i("MyMoyoListConnect", "HTTP_OK");
                    String data;
                    while((data = reader.readLine()) != null) {
                        receiveData.append(data + "\r\n");
                    }
                    reader.close();
                }
                else {
                    Log.i("MyMoyoListConnect", "HTTP_OK 안됨. 연결 실패.");
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            //저장된 내용을 로그로 출력한 후 onPostExecute()로 반환한다.
            Log.i("MyMoyoList", receiveData.toString());

            return receiveData.toString();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                //JSON객체를 1차로 파싱함
                JSONObject jsonObject = new JSONObject(s);

                //로그인 성공 여부를 판단해야 함.
                int success = Integer.parseInt(jsonObject.getString("isMoyoBus"));
                Log.i("MyMoyoList", "success: " + success);

                Log.i("MyMoyoListSuccess", "불러온 모여 개수 : " + success);

                if(success >= 1) {

                    moyoBusDTO.setM_idx(jsonObject.getJSONObject("moyoBusDTO").getString("m_idx"));
                    moyoBusDTO.setBusid(jsonObject.getJSONObject("moyoBusDTO").getString("busid"));
                    moyoBusDTO.setBuspass(jsonObject.getJSONObject("moyoBusDTO").getString("buspass"));
                    moyoBusDTO.setMb_lat(jsonObject.getJSONObject("moyoBusDTO").getString("mb_lat"));
                    moyoBusDTO.setMb_lon(jsonObject.getJSONObject("moyoBusDTO").getString("mb_lon"));
                    moyoBusDTO.setMb_num(jsonObject.getJSONObject("moyoBusDTO").getString("mb_num"));
                    moyoBusDTO.setMb_status(jsonObject.getJSONObject("moyoBusDTO").getString("mb_status"));
                    moyoBusDTO.setMb_addr(jsonObject.getJSONObject("moyoBusDTO").getString("mb_addr"));
                    moyoBusDTO.setMb_lastupdate(jsonObject.getJSONObject("moyoBusDTO").getString("mb_lastupdate"));

                }
                else {
                    Log.i("MyMoyoList", "내 모여목록 불러오기 실패");
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                Log.i("MyMoyoList", "예외 발생");
            }

        }
    }

    View.OnClickListener busBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if(moyoBusDTO.getMb_lat() == null || moyoBusDTO.getMb_lat() == "") {
                Toast.makeText(getContext(), "아직 버스가 배정되지 않았습니다.", Toast.LENGTH_LONG).show();
            }
            else {
                Bundle argsMoyoBus = new Bundle();
                argsMoyoBus.putSerializable("moyoBusDTO", moyoBusDTO);

                MyMoyoBusMap mmbm = new MyMoyoBusMap();
                mmbm.setArguments(argsMoyoBus);

                fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft_mymoyobus = fm.beginTransaction();
                ft_mymoyobus.replace(R.id.memberMainFragment, mmbm);
                ft_mymoyobus.addToBackStack(null);
                ft_mymoyobus.commit();

            }
        }
    };
}
