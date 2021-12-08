package com.example.riderremake;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

public class Register extends AppCompatActivity {
    RecyclerView recyclerView;
    RegisterAdapter registerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setStatusBarColor(ContextCompat.getColor(Register.this,R.color.colorPrimary));
        registerAdapter=new RegisterAdapter(Register.this);
        recyclerView=findViewById(R.id.register_recycler_view);
        recyclerView.setAdapter(registerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}