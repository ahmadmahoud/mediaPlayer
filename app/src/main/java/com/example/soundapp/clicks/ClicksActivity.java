package com.example.soundapp.clicks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.soundapp.R;
import com.example.soundapp.databinding.ActivityClicksBinding;
import com.example.soundapp.local.LocalActivity;

public class ClicksActivity extends AppCompatActivity {
    ActivityClicksBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_clicks);

        binding.btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClicksActivity.this , LocalActivity.class);
                intent.putExtra("name",0);
                startActivity(intent);
            }
        });

        binding.btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClicksActivity.this , LocalActivity.class);
                intent.putExtra("name",1);
                startActivity(intent);
            }
        });
    }
}