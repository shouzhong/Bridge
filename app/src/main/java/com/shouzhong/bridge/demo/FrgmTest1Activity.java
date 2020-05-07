package com.shouzhong.bridge.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ThreadUtils;
import com.shouzhong.bridge.FragmentStack;

public class FrgmTest1Activity extends AppCompatActivity {
    TextView tv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frgm_test1);
        tv = findViewById(R.id.tv);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ThreadUtils.runOnUiThreadDelayed(new Runnable() {
            @Override
            public void run() {
                tv.setText("all:" + FragmentStack.size());
                tv.append("\nmain:" + FragmentStack.size(getPackageName()));
                tv.append("\ntest:" + FragmentStack.size(getPackageName() + ":test"));
            }
        }, 1000);
    }

    public void onClickNext(View v) {
        Intent intent = new Intent(this, FrgmTest2Activity.class);
        startActivity(intent);
    }
}
