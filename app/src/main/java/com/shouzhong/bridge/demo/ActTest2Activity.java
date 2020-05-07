package com.shouzhong.bridge.demo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ProcessUtils;
import com.shouzhong.bridge.ActivityStack;

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
        intent.putExtra("unique_id", ActivityStack.getUniqueId(this));
        startActivity(intent);
    }

    public void onClickSize(View v) {
        StringBuffer sb = new StringBuffer();
        sb.append("currentProcessName:").append(ProcessUtils.getCurrentProcessName()).append("\n");
        sb.append("all=").append(ActivityStack.size()).append("\n");
        sb.append("current=").append(ActivityStack.size(Process.myPid())).append("\n");
        sb.append("main=").append(ActivityStack.size(getPackageName())).append("\n");
        sb.append("test=").append(ActivityStack.size(getPackageName() + ":test")).append("\n");
        tv.setText(sb.toString());
    }

    public void onClickFinishTest1(View v) {
        ActivityStack.finish(ActTest1Activity.class);
    }

    public void onClickFinishTest2(View v) {
        ActivityStack.finish(ActTest2Activity.class);
    }

    public void onClickFinishPrevious(View v) {
        ActivityStack.finish(previousUniqueId);
    }

    public void onClickExitMain(View v) {
        ActivityStack.exit(getPackageName());
    }

    public void onClickExitTest(View v) {
        ActivityStack.exit(getPackageName() + ":test");
    }

    public void onClickExitAll(View v) {
        ActivityStack.exit();
    }
}
