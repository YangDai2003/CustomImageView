package com.yangdai.customimageview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.yangdai.imageviewpro.RoundRectImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RoundRectImageView roundRectImageView = new RoundRectImageView(this);
        roundRectImageView.setImageResource(R.drawable.screenshot);
    }
}