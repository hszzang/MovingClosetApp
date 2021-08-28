package com.project.movingclosetapp.memberfragments;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.project.movingclosetapp.R;
import com.project.movingclosetapp.models.MoyoDTO;
import com.project.movingclosetapp.models.MoyoUseDTO;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.project.movingclosetapp.MainActivity.IP_ADDRESS;

public class MyMoyoList extends Fragment {

    private View v;
    String userid;
    public ArrayList<MoyoDTO> moyoList;
    public ArrayList<MoyoUseDTO> moyoUseList;
    MoyoListViewAdapter adapter;
    ListView moyoListView;
    TextView countMyMoyo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.i("FragmentMyMoyoList", "마이모여리스트 프래그먼트입니다.");

        v = inflater.inflate(R.layout.member_my_moyo_list, container, false);

        if(getArguments() != null) {
            userid = getArguments().getString("userid");
        }

        moyoList = new ArrayList<>();
        moyoUseList = new ArrayList<>();

        adapter = new MoyoListViewAdapter();

        moyoListView = v.findViewById(R.id.moyoListView);
        countMyMoyo = v.findViewById(R.id.countMyMoyo);

        new AsyncHttpServer().execute(
                //현재 접속된 wifi의 ipv4 주소로 변경해줘야 합니다.
                "http://"+ IP_ADDRESS +":8081/movingcloset/android/AndMyMoyoList.do",
                "userid=" + userid
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
                out.write(strings[1].getBytes()); //파라미터2 : 아이디
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
                int success = Integer.parseInt(jsonObject.getString("isMoyo"));
                Log.i("MyMoyoList", "success: " + success);

                Log.i("MyMoyoListSuccess", "불러온 모여 개수 : " + success);

                if(success >= 1) {
                    Log.i("MyMoyoList", "내 모여목록 불러오기 성공");

                    JSONArray moyoListArray = jsonObject.getJSONArray("myMoyoList");
                    JSONArray moyoInfoArray = jsonObject.getJSONArray("myMoyoInfo");

                    for(int i = 0; i < moyoListArray.length(); i++) {

                        JSONObject moyoListObject = moyoListArray.getJSONObject(i);
                        JSONObject moyoInfoObject = moyoInfoArray.getJSONObject(i);

                        Log.i("CheckMoyoListParse", moyoListObject.getString("m_idx"));
                        MoyoDTO moyoDTO = new MoyoDTO(
                                moyoListObject.getString("m_idx"),
                                moyoListObject.getString("m_name"),
                                moyoListObject.getString("m_addr"),
                                moyoListObject.getString("m_lat"),
                                moyoListObject.getString("m_lon"),
                                moyoListObject.getString("m_goal"),
                                moyoListObject.getString("m_dday"),
                                moyoListObject.getString("m_desc"),
                                moyoListObject.getString("m_start"),
                                moyoListObject.getString("m_end"),
                                moyoListObject.getString("m_status"),
                                moyoListObject.getString("m_ofile"),
                                moyoListObject.getString("m_sfile")
                        );

                        moyoList.add(moyoDTO);

                        Log.i("CheckMoyoListParse", moyoDTO.toString());
                        MoyoUseDTO moyoUseDTO = new MoyoUseDTO(
                                moyoInfoObject.getString("m_idx"),
                                moyoInfoObject.getString("userid"),
                                moyoInfoObject.getString("mu_name"),
                                moyoInfoObject.getString("mu_phone"),
                                moyoInfoObject.getString("mu_email"),
                                moyoInfoObject.getString("mu_time"),
                                moyoInfoObject.getString("mu_regidate")
                        );

                        moyoUseList.add(moyoUseDTO);

                    }
                }
                else {
                    Log.i("MyMoyoList", "내 모여목록 불러오기 실패");
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                Log.i("MyMoyoList", "예외 발생");
            }

            for(MoyoDTO m : moyoList) {

                String startdate = m.getM_start().substring(0, 10);
                String enddate = m.getM_end().substring(0, 10);
                String ddaydate = m.getM_dday().substring(0,    10);


                adapter.addItem(m.getM_name(), m.getM_addr(),
                        startdate + " 부터 " + enddate + " 까지 모여!",
                        "모여DAY : " + ddaydate, m.getM_status());
            }

            if(moyoList.size() == 0) {
                adapter.addItem("신청한 모여가 없습니다.", "모여 신청 후 우리동네에서 쇼핑을 즐기세요!",
                        " ",
                        " " , " ");
            }
            countMyMoyo.setText("신청한 모여가 " + moyoList.size() + "개 있습니다.");
            moyoListView.setAdapter(adapter);
            moyoListView.setOnItemClickListener(myMoyoClickListener);
        }
    }

    AdapterView.OnItemClickListener myMoyoClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            //i는 0부터 시작 !
            Log.i("CheckMoyoListViewClick" , i + "번째 리스트뷰 클릭 - " + moyoList.get(i).toString());




        }
    };



}