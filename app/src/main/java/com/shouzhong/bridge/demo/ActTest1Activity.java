package com.shouzhong.bridge.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.shouzhong.bridge.ActivityUtils;

public class ActTest1Activity extends AppCompatActivity {
    TextView tv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_test1);
        tv = findViewById(R.id.tv);
    }

    public void onClickNext(View v) {
        Intent intent = new Intent(this, ActTest2Activity.class);
        startActivity(intent);
    }

    public void onClickSize(View v) {
        tv.setText("size=" + ActivityUtils.size());
    }

    public void onClickAllSize(View v) {
        tv.setText("allSize=" + ActivityUtils.allSize());
    }

    public void onClickFinish(View v) {
        ActivityUtils.finish(ActTest2Activity.class);
    }

    public void onClickExit(View v) {
        ActivityUtils.exit(getPackageName() + ":test");
    }

    public void onClickAllExit(View v) {
        ActivityUtils.exit();
    }
}
