package com.example.sudoku_juanpereira;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import com.example.sudoku_juanpereira.Game.GameActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Home extends AppCompatActivity {


    BottomNavigationView menu;
    private int main_screen;
    private MediaPlayer mp_menuclick;
    private MediaPlayer mp_click;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_home);
        main_screen = R.id.fl_screen;
        replaceFragment(new HomeFragment(),main_screen);
        menu = findViewById(R.id.nv_menu);
        mp_menuclick = MediaPlayer.create(this,R.raw.menuclick);
        mp_menuclick.setVolume((float)0.1,(float)0.1);
        mp_click = MediaPlayer.create(this,R.raw.click);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences preferences = getSharedPreferences(getString(R.string.preference_users), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor  =preferences.edit();
        editor.putString("email",user.getEmail());
        editor.putString("uID",user.getUid());
        editor.putString("name",user.getDisplayName());
        editor.apply();

        menu.setOnItemSelectedListener(item -> {

            mp_menuclick.start();

            switch(item.getItemId()){
                case R.id.home:
                    replaceFragment(new HomeFragment(),main_screen);
                    break;
                case R.id.ranking:
                    replaceFragment(new RankingFragment(),main_screen);
                    break;
                case R.id.stats:
                    replaceFragment(new StatsFragment(),main_screen);
                    break;
            }


            return true;
        });


    }

    protected void replaceFragment(Fragment fragment,int screen){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(screen,fragment);
        fragmentTransaction.commit();
    }

    public void newGame(View view){
        mp_click.start();
        replaceFragment(new DifficultyFragment(),main_screen);
    }

    public void showGame(View view){
        mp_click.start();
        Intent game = new Intent(this,GameActivity.class);
        startActivity(game);
    }

    public void logOut(View view){
        mp_click.start();

        SharedPreferences preferences = getSharedPreferences(getString(R.string.preference_users), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor  =preferences.edit();
        editor.clear();
        editor.apply();

        SharedPreferences preferencesTheme = getSharedPreferences(getString(R.string.preference_game_theme), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2  =preferencesTheme.edit();
        editor2.clear();
        editor2.apply();

        Intent main = new Intent(this,MainActivity.class);
        startActivity(main);

    }

    public void newGameEasy(View view){
        startNewGame(1);
    }

    public void newGameMedium(View view){
        startNewGame(2);
    }

    public void newGameDifficult(View view){
        startNewGame(3);
    }

    private void startNewGame(int difficulty){
        Intent game = new Intent(this,GameActivity.class);
        game.putExtra("difficulty",difficulty);
        startActivity(game);
    }
}