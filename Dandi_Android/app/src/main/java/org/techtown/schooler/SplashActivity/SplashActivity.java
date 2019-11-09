package org.techtown.schooler.SplashActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import org.techtown.schooler.MainActivity;
import org.techtown.schooler.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Handler 클래스를 사용해서 handler 인스턴스를 생성하였습니다.
        Handler handler = new Handler();

        // handler 인스턴스의 속성인 postDelayed 속성을 사용하여 splashHandler() 객체를 전달하고 있으며 딜레이 시간을 3초로 지정하고있습니다.
        handler.postDelayed(new splashHandler(), 650);
    }

    // splashHandler 클래스입니다. implements 속성을 사용하여 Runnable 를 참조합니다. Runnable 속성은 run() 매서드를 필수로 필요합니다.
    private class splashHandler implements Runnable{

        @Override
        public void run() {

            // run() 매서드가 실행될 시 화면이 현재 SplashActivity 에서 MainActivity 로 전환을 합니다.
            startActivity(new Intent(getApplication(), MainActivity.class));

            // 그 후 splashActivity 화면이 종료됩니다.
            SplashActivity.this.finish();

            overridePendingTransition(R.anim.loadfadein, R.anim.loadfadeout);
        }
    }

    // 뒤로가기 버튼을 제어해두었습니다.
    public void onBackPressed(){

        }
    }

