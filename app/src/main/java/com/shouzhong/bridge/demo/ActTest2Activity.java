package com.shouzhong.bridge.demo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ProcessUtils;
import com.shouzhong.bridge.ActivityUtils;

public class ActTest2Activity extends AppCompatActivity {
    TextView tv;
    String previousUniqueId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_test2);
        tv = findViewById(R.id.tv);
        previousUniqueId = getIntent().getStringExtra("unique_id");
    }

    public void onClickNext(View v) {
        Intent intent = new Intent(this, ActTest1Activity.class);
        intent.putExtra("unique_id", ActivityUtils.getUniqueId(this));
        startActivity(intent);
    }

    public void onClickSize(View v) {
        StringBuffer sb = new StringBuffer();
        sb.append("currentProcessName:").append(ProcessUtils.getCurrentProcessName()).append("\n");
        sb.append("all=").append(ActivityUtils.size()).append("\n");
        sb.append("current=").append(ActivityUtils.size(Process.myPid())).append("\n");
        sb.append("main=").append(ActivityUtils.size(getPackageName())).append("\n");
        sb.append("test=").append(ActivityUtils.size(getPackageName() + ":test")).append("\n");
        tv.setText(sb.toString());
    }

    public void onClickFinishTest1(View v) {
        ActivityUtils.finish(ActTest1Activity.class);
    }

    public void onClickFinishTest2(View v) {
        ActivityUtils.finish(ActTest2Activity.class);
    }

    public void onClickFinishPrevious(View v) {
        ActivityUtils.finish(previousUniqueId);
    }

    public void onClickExitMain(View v) {
        ActivityUtils.exit(getPackageName());
    }

    public void onClickExitTest(View v) {
        ActivityUtils.exit(getPackageName() + ":test");
    }

    public void onClickExitAll(View v) {
        ActivityUtils.exit();
    }
}
