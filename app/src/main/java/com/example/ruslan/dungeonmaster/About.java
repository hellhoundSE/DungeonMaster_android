package com.example.ruslan.dungeonmaster;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class About extends AppCompatActivity {

    Button button;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        button = findViewById(R.id.button_b);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MainMenu.class);
                startActivity(intent);
            }
        });

        textView = findViewById(R.id.txt_about);
        textView.setText("The main idea of this game it is find way for last room. " +
                "You will interfere with different monsters. Attacking monsters you deal damage to him, but he also hits you back." +
                "For any monster you kill, you get experience, After gaining enough experience you can levelup, your hp and attack power will be increased" +
                "\nEquipment : " +
                "\n\tArrmor - increase ypu max HP" +
                "\n\tSwords - increase your attack power" +
                "\n\tCake - restore health" +
                "\nSpace in your inventory is not infinity, and all equipments have weight" +
                "\nButtons :" +
                "\n\tPickup - add selected item to your inventory" +
                "\n\tDrop - drop selected item from inventory on the floor in this toom" +
                "\n\tUse - put on selected armor or sword instead of those that are already used, they will be putted to inventory" +
                "\n\tFight - attack monster" +
                "\n\tSave - save game progress, you can contine it from main menu" +
                "\n\tDirections - move to another room"
        );



    }
}
