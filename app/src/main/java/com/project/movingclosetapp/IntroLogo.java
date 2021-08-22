package com.project.movingclosetapp;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class IntroLogo extends AppCompatActivity {

    //일정시간 이후에 실행하기 위해 Handler객체를 생성한다.
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            //인트로 화면 이후에 메인액티비티를 실행하기 위해 인텐트 객체 생성
            Intent intent = new Intent(getApplicationContext(),
                    MainActivity.class);

            //액티비티 실행
            startActivity(intent);

            /*
            액티비티가 실행되거나 종료될 때 애니메이션 효과를 부여한다.
            인자1 : 새롭게 실행되는 액티비티의 애니메이션
            인자2 : 종료되는 액티비티에 적용되는 애니메이션
             */
            overridePendingTransition(R.anim.hold,
                    R.anim.fade_out);

            //인트로 액티비티를 종료한다.
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_logo);

        //Java코드에서 액션바 숨김처리하기
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.hide();
    }

    //액티비티 실행 시 3번째로 호출되는 수명주기 메소드
    @Override
    protected void onResume() {
        super.onResume();

        //Intro액티비티에 진입한 후 2초 후에 runnable 객체를 실행한다.
        handler.postDelayed(runnable, 2000);
    }

    @Override
    protected void onPause() {
        super.onPause();

        //Intro가 종료될 때 handler에 예약해놓은 작업을 취소한다.
        handler.removeCallbacks(runnable);
    }

}