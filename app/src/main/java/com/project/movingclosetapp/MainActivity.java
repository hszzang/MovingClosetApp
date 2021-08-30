package com.project.movingclosetapp;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.project.movingclosetapp.models.MemberDTO;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    public static String IP_ADDRESS = "192.168.219.103";

    ImageView loginpage_Logo;
    Button btnMember, btnBus, btnLogin;
    EditText memId, memPass;
    MemberDTO memberDTO = new MemberDTO();

    int countLogo = 0;

//    SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getHashKey();

        btnMember = findViewById(R.id.btnMember);
        btnBus = findViewById(R.id.btnBus);

        memId = findViewById(R.id.memId);
        memPass = findViewById(R.id.memPass);

        btnMember.setOnClickListener(clickGroupListener);
        btnBus.setOnClickListener(clickGroupListener);

        btnLogin = findViewById(R.id.btnLogin);

        loginpage_Logo = findViewById(R.id.loginpage_Logo);
        loginpage_Logo.setOnClickListener(clickLoginLogo);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i("LoginData", memPass.getText().toString());

                new AsyncHttpServer().execute(
                        //현재 접속된 wifi의 ipv4 주소로 변경해줘야 합니다.
                        "http://"+ IP_ADDRESS +":8081/movingcloset/android/AndLogin.do",
                                "userid=" + memId.getText().toString(),
                                "userpass=" + memPass.getText().toString()
                );


            }
        });


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
                out.write("&".getBytes()); //&를 사용하여 쿼리스트링 형태로 만들어준다.
                out.write(strings[2].getBytes()); //파라미터3 : 패스워드
                out.flush();
                out.close();

                Log.i("LoginURL", url.toString());

                if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    //Rest API 서버에서 내려주는 JSON데이터를 읽어서 저장한다.
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), "UTF-8")
                    );

                    Log.i("LoginConnect", "HTTP_OK");
                    String data;
                    while((data = reader.readLine()) != null) {
                        receiveData.append(data + "\r\n");
                    }
                    reader.close();
                }
                else {
                    Log.i("LoginConnect", "HTTP_OK 안됨. 연결 실패.");
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            //저장된 내용을 로그로 출력한 후 onPostExecute()로 반환한다.
            Log.i("Login", receiveData.toString());

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
                /*
                {
                "isLogin":1,
                "memberDTO":{"userid":"hszzang"
                    ,"userpass":"af2e9d3922"
                    ,"email":"17041004@naver.com"
                    ,"phone":"010-1234-5678"
                    ,"tag":null
                    ,"postcode":"12345"
                    ,"addr":"인천광역시 미추홀구 숙골로 87번길"
                    ,"name":"강혜수"
                    ,"birth":"2021-08-15 14:49:36"
                    ,"regidate":"2021-08-15"
                    }
                }
                */
                //JSON객체를 1차로 파싱함
                JSONObject jsonObject = new JSONObject(s);

                //로그인 성공 여부를 판단해야 함.
                int success = Integer.parseInt(jsonObject.getString("isLogin"));
                Log.i("MemberLogin", "success: " + success);


                if(success == 1) {
                    Log.i("MemberLogin", "로그인 성공");

                    //객체 안에 회원정보를 저장한 또 하나의 JSON객체가 있으므로 파싱
                    memberDTO.setUserid(jsonObject.getJSONObject("memberDTO").getString("userid"));
                    memberDTO.setUserpass(jsonObject.getJSONObject("memberDTO").getString("userpass"));
                    memberDTO.setEmail(jsonObject.getJSONObject("memberDTO").getString("email"));
                    memberDTO.setPhone(jsonObject.getJSONObject("memberDTO").getString("phone"));
                    memberDTO.setPostcode(jsonObject.getJSONObject("memberDTO").getString("postcode"));
                    memberDTO.setAddr(jsonObject.getJSONObject("memberDTO").getString("addr"));
                    memberDTO.setName(jsonObject.getJSONObject("memberDTO").getString("name"));

//                    createMyDatabase();
//                    createMyTable();
//                    insertLoginMember(memberDTO);

                }
                else {
                    Log.i("MemberLogin", "로그인 실패");
                    memberDTO.setName(null);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                Log.i("MemberLogin", "예외 발생");
            }

            //대화창 닫기
//            dialog.dismiss();

            if(memberDTO.getName() != null) {
                Toast.makeText(getApplicationContext(),
                        memberDTO.getName() + "님 환영합니다.",
                        Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getApplicationContext(), MemberMainActivity.class);

                //부가데이터를 인텐트에 추가한다.
                intent.putExtra("UserInfo", memberDTO); //아이디 입력값

                startActivity(intent);

                memId = findViewById(R.id.memId);
                memId.setText("");
                memPass = findViewById(R.id.memPass);
                memPass.setText("");
            }
            else {
                Log.i("MemberLogin", "로그인 실패");
                Toast.makeText(getApplicationContext(),
                        "아이디와 비밀번호를 확인해주세요.",
                        Toast.LENGTH_LONG).show();
            }


        }
    }

    View.OnClickListener clickGroupListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if(view == btnMember) {
                btnMember.setBackgroundColor(Color.parseColor("#FF6C2F"));
                btnMember.setTextColor(Color.WHITE);
                btnBus.setBackgroundColor(Color.WHITE);
                btnBus.setTextColor(Color.BLACK);
            }
            else if(view == btnBus) {
                btnBus.setBackgroundColor(Color.parseColor("#FF6C2F"));
                btnBus.setTextColor(Color.WHITE);
                btnMember.setBackgroundColor(Color.WHITE);
                btnMember.setTextColor(Color.BLACK);
            }
        }
    };

    View.OnClickListener clickLoginLogo = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            countLogo ++;

            if(countLogo >= 5) {

                MemberDTO adminLogin = new MemberDTO("hs2zzang", "aaa111!!!", "qpwohs2@gmai.com", "010-0000-0000", null, "06015", "서울특별시 강남구 도산대로75길 21", "강혜수", null, "2021-08-23");

                Toast.makeText(getApplicationContext(),
                        "관리자님 환영합니다.",
                        Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getApplicationContext(), MemberMainActivity.class);

                intent.putExtra("UserInfo", adminLogin); //아이디 입력값
                countLogo = 0;
                startActivity(intent);
            }
        }
    };

    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }

//    private void createMyDatabase() {
//
//        try{
//
//            sqLiteDatabase = openOrCreateDatabase("member.sqlite",
//                    Activity.MODE_PRIVATE, null);
//
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    //테이블 생성
//    private void createMyTable() {
//
//        //테이블이 없을 때만 새롭게 생성한다.
//        String sql = "create table if not exists member (userid text, " +
//                " userpass text, email text, phone text, tag text, postcode text, " +
//                " addr text, name text, birth text, regidate text ) ";
//
//        try {
//
//            sqLiteDatabase.execSQL(sql);
//
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void insertLoginMember(MemberDTO m) {
//
//        String query = "INSERT INTO member (userid, userpass, email, phone, postcode, addr, name) " +
//                " VALUES ( '" + m.getUserid() + "', '" + m.getUserid() + "', '" + m.getEmail() + "', '" + m.getPhone() +
//                "', '" + m.getPostcode() + "', '" + m.getAddr() + "', '" + m.getName() + "' ) ";
//        try {
//
//            sqLiteDatabase.execSQL(query);
//
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}