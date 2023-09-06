package com.example.sudoku_juanpereira.Game;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.sudoku_juanpereira.R;

public class ThemeActivity extends AppCompatActivity {

    ConstraintLayout cl_classic;
    ConstraintLayout cl_dark;
    ConstraintLayout cl_light;
    ConstraintLayout cl_neon;


    ImageView border_classic;
    ImageView border_dark;
    ImageView border_light;
    ImageView border_neon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);

        getSupportActionBar().hide();

        SharedPreferences preferences = getSharedPreferences(getString(R.string.preference_game_theme), Context.MODE_PRIVATE);
        String theme = preferences.getString("theme","classic");


        cl_classic = findViewById(R.id.cl_classic);
        cl_dark = findViewById(R.id.cl_dark);
        cl_light = findViewById(R.id.cl_light);
        cl_neon = findViewById(R.id.cl_neon);
        border_classic = findViewById(R.id.border_classic);
        border_dark = findViewById(R.id.border_dark);
        border_light = findViewById(R.id.border_light);
        border_neon = findViewById(R.id.border_neon);

        hideBorders(theme);

        cl_classic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeTheme(0);
            }
        });

        cl_dark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeTheme(1);
            }
        });

        cl_light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeTheme(2);
            }
        });

        cl_neon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeTheme(3);
            }
        });
    }

    private void hideBorders(String theme){
        switch(theme){
            case "dark":
                border_light.setVisibility(View.INVISIBLE);
                border_neon.setVisibility(View.INVISIBLE);
                border_dark.setVisibility(View.VISIBLE);
                border_classic.setVisibility(View.INVISIBLE);
                break;
            case "light":
                border_light.setVisibility(View.VISIBLE);
                border_neon.setVisibility(View.INVISIBLE);
                border_dark.setVisibility(View.INVISIBLE);
                border_classic.setVisibility(View.INVISIBLE);
                break;
            case "neon":
                border_light.setVisibility(View.INVISIBLE);
                border_neon.setVisibility(View.VISIBLE);
                border_dark.setVisibility(View.INVISIBLE);
                border_classic.setVisibility(View.INVISIBLE);
                break;
            case "classic":
                border_light.setVisibility(View.INVISIBLE);
                border_neon.setVisibility(View.INVISIBLE);
                border_dark.setVisibility(View.INVISIBLE);
                border_classic.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void changeTheme(int t){
        String theme;
        switch(t){
            case 1:
                theme = "dark";
                break;
            case 2:
                theme = "light";
                break;
            case 3:
                theme = "neon";
                break;
            default:
                theme = "classic";
        }
        hideBorders(theme);

        SharedPreferences preferences = getSharedPreferences(getString(R.string.preference_game_theme), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor  =preferences.edit();
        editor.putString("theme",theme);
        editor.apply();
    }

}