package com.yangdai.customimageview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.yangdai.imageviewpro.ImageViewPro;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageViewPro imageViewPro = findViewById(R.id.image_view);
    }
}