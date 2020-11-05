package com.gokul.sonaalbumadmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    CardView uploadImageCard, uploadVideoCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Sona Album");

        uploadImageCard = findViewById(R.id.uploadImageCard);
        uploadVideoCard = findViewById(R.id.uploadVideoCard);

        uploadImageCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddImageActivity.class));
            }
        });

        uploadVideoCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddVideoActivity.class));
            }
        });
    }
}