package com.example.myapplication.Activity;

import android.os.Bundle;

import com.example.myapplication.R;

public class AnnouncementActivity extends BasicActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);
        setToolbarTitle("공지사항");
    }
}
