package com.xlk.paperlesstl.view.offline;

import androidx.appcompat.app.AppCompatActivity;
import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

import android.os.Bundle;

import com.xlk.paperlesstl.R;

public class LocalPlayActivity extends AppCompatActivity {

    private JzvdStd jz_video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_play);
        Bundle extras = getIntent().getExtras();
        String filePath = extras.getString("filePath");
        String fileName = extras.getString("fileName");
        jz_video = findViewById(R.id.jz_video);
        jz_video.setUp(filePath, fileName);
        jz_video.startVideo();
    }

    @Override
    public void onBackPressed() {
        if (Jzvd.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();
    }
}