package com.ryane.teach_circlebanner;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MyCircleBanner mBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBanner = (MyCircleBanner) findViewById(R.id.mBanner);

        List<String> mInfos = new ArrayList<>();
        mInfos.add("http://onq81n53u.bkt.clouddn.com/photo1.jpg");
        mInfos.add("http://onq81n53u.bkt.clouddn.com/photo2.jpg");

        mBanner.play(mInfos);
    }

}
