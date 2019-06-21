package com.example.ruslan.dungeonmaster;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenu extends AppCompatActivity {

    String newOrLoad;
    Button newGame;
    Button loadGame;
    Button map;
    Button about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        setupButtons();
    }

    public void setupButtons(){
        newGame = findViewById(R.id.button_newgame);
        newGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newOrLoad = "0";
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.putExtra("NEWGAME_OR_LOAD", newOrLoad);
                startActivity(intent);
            }
        });

        loadGame = findViewById(R.id.button_loadgame);
        loadGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newOrLoad = "1";
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.putExtra("NEWGAME_OR_LOAD", newOrLoad);
                startActivity(intent);
            }
        });

        map = findViewById(R.id.button_map);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), mapActivity.class);
                startActivity(intent);
            }
        });

        about = findViewById(R.id.button_about);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), About.class);
                startActivity(intent);
            }
        });
    }
}
