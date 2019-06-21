package com.example.ruslan.dungeonmaster;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.XmlResourceParser;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Path;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class MainActivity extends AppCompatActivity {

    final String LOADPATH = new String("myFile.xml");

    final int NO_EXIT = -1;
    final int ROOMS = 30;

    int previosRoom;

    Spinner roomsItems;
    ArrayAdapter roomAdapter;

    Spinner myItems;
    ArrayAdapter myAdapter;

    TextView currentRoomTextView;

    TextView hpButton;
    TextView freeSpaceButton;
    TextView attackButton;
    TextView expButton;

    TextView boss_hp;

    Button north;
    Button west;
    Button east;
    Button south;

    Button pickup;
    Button drop;
    Button fight;
    Button useButton;
    Button save;


    Player player;
    Room[] dungeon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupFields();

        String playerSrartType = getIntent().getStringExtra("NEWGAME_OR_LOAD");


        if (playerSrartType == null || playerSrartType.equals("0")) {
            if (playerSrartType != null)
                Log.w("TYPE", "NEW GAME");
            XmlResourceParser xpp = getResources().getXml(R.xml.map);
            readXMLFile("new");
        } else {
            Log.w("TYPE", "OLD GAME");
            readSave();
            //readXMLFile(LOAD_GAME_XML);
        }


        goToNewRoom(player.getCurrentRoom());
        previosRoom = 0;
    }

    public void setupFields() {

        save = findViewById(R.id.button_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUsingParser();
            }
        });


        useButton = findViewById(R.id.button_use);
        useButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Item item = (Item) myItems.getSelectedItem();
                if (item != null) {
                    player.use(item);
                    refresh();
                }

            }
        });

        boss_hp = findViewById(R.id.txt_boss_hp);

        fight = findViewById(R.id.button_fight);
        fight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dungeon[player.getCurrentRoom()].getMonster() != null) {
                    Monster m = dungeon[player.getCurrentRoom()].getMonster();
                    m.setHp(m.getHp() - player.getAttack());
                    player.setHp(player.getHp() - m.getAttack());
                    boss_hp.setText( "HP : "+ m.getHp() +"   Attack : "+ m.getAttack() );
                    if (m.getHp() <= 0) {
                        player.gettingExperience(m.getExp());
                        dungeon[player.getCurrentRoom()].setMonster(null);
                        showToast("You win the fight");
                        boss_hp.setText( "Room cleaned");
                    }
                    if (player.getHp() <= 0) {
                        youDie();
                    }
                    refresh();

                }
            }
        });

        drop = findViewById(R.id.button_drop);
        drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Item item = (Item) myItems.getSelectedItem();
                if (item != null) {
                    player.getItems().remove(item);
                    player.setFilled(player.getFilled() - item.getWeight());
                    dungeon[player.getCurrentRoom()].getInventory().add(item);
                    refresh();
                }
            }
        });

        pickup = findViewById(R.id.button_pickup);
        pickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Item item = (Item) roomsItems.getSelectedItem();
                if (item != null && player.getFilled() + item.getWeight() <= player.getSpace()) {
                    player.getItems().add(item);
                    player.setFilled(player.getFilled() + item.getWeight());
                    dungeon[player.getCurrentRoom()].getInventory().remove(item);
                    refresh();
                }
            }
        });

        north = findViewById(R.id.button_north);
        north.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dungeon[player.getCurrentRoom()].getNorth() != NO_EXIT) {
                    if (dungeon[player.getCurrentRoom()].getMonster() == null
                            || dungeon[player.getCurrentRoom()].getNorth() == previosRoom) {
                        previosRoom = player.getCurrentRoom();
                        goToNewRoom(dungeon[player.getCurrentRoom()].getNorth());
                    } else {
                        showToast("Kill monster first");
                    }
                }
            }
        });

        south = findViewById(R.id.button_south);
        south.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dungeon[player.getCurrentRoom()].getSouth() != NO_EXIT) {
                    if (dungeon[player.getCurrentRoom()].getMonster() == null
                            || dungeon[player.getCurrentRoom()].getSouth() == previosRoom) {
                        previosRoom = player.getCurrentRoom();
                        goToNewRoom(dungeon[player.getCurrentRoom()].getSouth());
                    } else {
                        showToast("Kill monster first");
                    }
                }
            }
        });

        west = findViewById(R.id.button_west);
        west.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dungeon[player.getCurrentRoom()].getWest() != NO_EXIT) {
                    if (dungeon[player.getCurrentRoom()].getMonster() == null
                            || dungeon[player.getCurrentRoom()].getWest() == previosRoom) {
                        previosRoom = player.getCurrentRoom();
                        goToNewRoom(dungeon[player.getCurrentRoom()].getWest());
                    } else {
                        showToast("Kill monster first");
                    }
                }
            }
        });

        east = findViewById(R.id.button_east);
        east.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dungeon[player.getCurrentRoom()].getEast() != NO_EXIT) {
                    if (dungeon[player.getCurrentRoom()].getMonster() == null
                            || dungeon[player.getCurrentRoom()].getEast() == previosRoom) {
                        previosRoom = player.getCurrentRoom();
                        goToNewRoom(dungeon[player.getCurrentRoom()].getEast());
                    } else {
                        showToast("Kill monster first");
                    }
                }
            }
        });


        roomsItems = findViewById(R.id.spinner);
        myItems = findViewById(R.id.spinner2);


        hpButton = findViewById(R.id.txt_hp);
        freeSpaceButton = findViewById(R.id.txt_weight);
        attackButton = findViewById(R.id.txt_attack);
        currentRoomTextView = findViewById(R.id.textView_currentRoom);
        expButton = findViewById(R.id.txt_exp);

    }

    private void youDie() {
        showToast("You died");
        Intent intent = new Intent(getBaseContext(), MainMenu.class);
        startActivity(intent);
    }

    private void goToNewRoom(int roomId) {

        player.setCurrentRoom(roomId);

        roomAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, dungeon[roomId].getInventory());
        roomsItems.setAdapter(roomAdapter);

        myAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, player.getItems());
        myItems.setAdapter(myAdapter);

        freeSpaceButton.setText(player.getFilled() + "/" + player.getSpace());
        hpButton.setText("HP " + player.getHp() + "/" + player.getMaxHp());
        attackButton.setText("ATK " + player.getAttack());
        currentRoomTextView.setText("Room : " + (player.getCurrentRoom() + 1));
        expButton.setText(player.getExp() + "/" + (player.getLevel() * 2 + 10));

        if(dungeon[roomId].monster != null){
            boss_hp.setText( "HP : "+ dungeon[roomId].monster.getHp() +"   Attack : "+dungeon[roomId].monster.getAttack() );
        }else{
            boss_hp.setText("Room cleaned");
        }
    }

    public void refresh() {
        goToNewRoom(player.getCurrentRoom());
    }

    public void readXMLFile(String fileName) {


        player = new Player();
        XmlPullParserFactory factory;
        FileInputStream fis = null;

        dungeon = new Room[ROOMS];
        for (int i = 0; i < dungeon.length; i++) {
            dungeon[i] = new Room();
        }
        int pos = 0; // May be use this variable, to keep track of what position of the array of Room Objects.
        try {

            StringBuilder sb = new StringBuilder();
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            fis = openFileInput("cities.xml");
            xpp.setInput(fis, null);

            if(fileName.equals("new")){
                xpp = getResources().getXml(R.xml.map);
            }

            xpp.next();
            int eventType = xpp.getEventType();
            int room_count = 0;
            String elemtext = null;

            String itemName = null;
            String itemWeight = null;
            String itemPower = null;

            String mobHp = null;
            String mobAtk = null;
            String mobExp = null;

            boolean itm = false;

            Log.w("Event type", eventType + "");
            Log.w("Event type", "" + (+eventType != XmlPullParser.END_DOCUMENT));
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String elemName = xpp.getName();

                    if (elemName.equals("dungeon")) {
                        String authorAttr = xpp.getAttributeValue(null, "author");
                    } // if (elemName.equals("dungeon"))
                    if (elemName.equals("health")) {
                        elemtext = "health";
                    }
                    if (elemName.equals("maxHealth")) {
                        elemtext = "maxHealth";
                    }
                    if (elemName.equals("maxWeight")) {
                        elemtext = "maxWeight";
                    }
                    if (elemName.equals("level")) {
                        elemtext = "level";
                    }
                    if (elemName.equals("experience")) {
                        elemtext = "experience";
                    }
                    if (elemName.equals("currentRoom")) {
                        elemtext = "currentRoom";
                    }
                    if (elemName.equals("mySwordAttack")) {
                        elemtext = "mySwordAttack";
                    }
                    if (elemName.equals("myArmorProtection")) {
                        elemtext = "myArmorProtection";
                    }
                    if (elemName.equals("inventorySwordAttack")) {
                        elemtext = "inventorySwordAttack";
                    }
                    if (elemName.equals("inventoryProtection")) {
                        elemtext = "inventoryProtection";
                    }
                    if (elemName.equals("inventoryHeal")) {
                        elemtext = "inventoryHeal";
                    }
                    if (elemName.equals("room")) {
                        room_count = room_count + 1;
                    }
                    if (elemName.equals("north")) {
                        elemtext = "north";
                    }
                    if (elemName.equals("east")) {
                        elemtext = "east";
                    }
                    if (elemName.equals("south")) {
                        elemtext = "south";
                    }
                    if (elemName.equals("west")) {
                        elemtext = "west";
                    }
                    if (elemName.equals("description")) {
                        elemtext = "description";
                    }
                    if (elemName.equals("name")) {
                        elemtext = "name";
                    }
                    if (elemName.equals("weight")) {
                        elemtext = "weight";
                    }
                    if (elemName.equals("swordAttack")) {
                        elemtext = "swordAttack";
                    }
                    if (elemName.equals("protection")) {
                        elemtext = "protection";
                    }
                    if (elemName.equals("heal")) {
                        elemtext = "heal";
                    }
                    if (elemName.equals("hp")) {
                        elemtext = "hp";
                    }
                    if (elemName.equals("exp")) {
                        elemtext = "exp";
                    }
                    if (elemName.equals("attack")) {
                        elemtext = "attack";
                    }

                } else if (eventType == XmlPullParser.TEXT) {
                    if (elemtext.equals("health")) {
                        player.setHp(Integer.parseInt(xpp.getText()));
                    }
                    if (elemtext.equals("maxHealth")) {
                        player.setMaxHp(Integer.parseInt(xpp.getText()));
                        Log.w("CheckMHP", "hp = " + player.getHp() + " / " + player.getMaxHp());
                    }
                    if (elemtext.equals("maxWeight")) {
                        player.setSpace(Integer.parseInt(xpp.getText()));
                    }
                    if (elemtext.equals("level")) {
                        player.setLevel(Integer.parseInt(xpp.getText()));
                    }
                    if (elemtext.equals("experience")) {
                        player.setExp(Integer.parseInt(xpp.getText()));
                    }
                    if (elemtext.equals("currentRoom")) {
                        player.setCurrentRoom(Integer.parseInt(xpp.getText()));
                    }
                    if (elemtext.equals("mySwordAttack")) {
                        itemPower = xpp.getText();
                        player.setSword(new Sword(itemName, Integer.parseInt(itemWeight), Integer.parseInt(itemPower)));
                    }
                    if (elemtext.equals("myArmorProtection")) {
                        itemPower = xpp.getText();
                        player.setArmor(new Armor(itemName, Integer.parseInt(itemWeight), Integer.parseInt(itemPower)));
                    }
                    if (elemtext.equals("inventorySwordAttack")) {
                        itemPower = xpp.getText();
                        player.getItems().add(new Sword(itemName, Integer.parseInt(itemWeight), Integer.parseInt(itemPower)));
                    }
                    if (elemtext.equals("inventoryProtection")) {
                        itemPower = xpp.getText();
                        player.getItems().add(new Armor(itemName, Integer.parseInt(itemWeight), Integer.parseInt(itemPower)));
                    }
                    if (elemtext.equals("inventoryHeal")) {
                        itemPower = xpp.getText();
                        player.getItems().add(new Cake(itemName, Integer.parseInt(itemWeight), Integer.parseInt(itemPower)));
                    }


                    if (elemtext.equals("name")) {
                        itemName = xpp.getText();
                    }
                    if (elemtext.equals("weight")) {
                        itemWeight = xpp.getText();
                    }
                    if (elemtext.equals("swordAttack")) {
                        itemPower = xpp.getText();
                        Sword sword = new Sword(itemName, Integer.parseInt(itemWeight), Integer.parseInt(itemPower));
                        dungeon[room_count - 1].getInventory().add(sword);
                    }
                    if (elemtext.equals("protection")) {
                        itemPower = xpp.getText();
                        Armor armor = new Armor(itemName, Integer.parseInt(itemWeight), Integer.parseInt(itemPower));
                        dungeon[room_count - 1].getInventory().add(armor);
                    }
                    if (elemtext.equals("heal")) {
                        itemPower = xpp.getText();
                        Cake cake = new Cake(itemName, Integer.parseInt(itemWeight), Integer.parseInt(itemPower));
                        dungeon[room_count - 1].getInventory().add(cake);
                    }

                    if (elemtext.equals("exp")) {
                        mobExp = xpp.getText();
                    }
                    if (elemtext.equals("hp")) {
                        mobHp = xpp.getText();
                    }
                    if (elemtext.equals("attack")) {
                        mobAtk = xpp.getText();
                        Monster m = new Monster(Integer.parseInt(mobHp), Integer.parseInt(mobAtk), Integer.parseInt(mobExp));
                        dungeon[room_count - 1].setMonster(m);
                    }

                    if (elemtext.equals("north")) {
                        Log.w("ROOM", "north = " + xpp.getText());
                        dungeon[room_count - 1].setNorth(Integer.valueOf(xpp.getText()));
                    } else if (elemtext.equals("east")) {
                        Log.w("ROOM", "east = " + xpp.getText());
                        dungeon[room_count - 1].setEast(Integer.valueOf(xpp.getText()));
                    } else if (elemtext.equals("south")) {
                        Log.w("ROOM", "south = " + xpp.getText());
                        dungeon[room_count - 1].setSouth(Integer.valueOf(xpp.getText()));
                    } else if (elemtext.equals("west")) {
                        Log.w("ROOM", "west = " + xpp.getText());
                        dungeon[room_count - 1].setWest(Integer.valueOf(xpp.getText()));
                    } else if (elemtext.equals("description")) {
                        Log.w("ROOM", "description = " + xpp.getText());
                        dungeon[room_count - 1].setDescription(xpp.getText());
                    }
                    elemtext = "";
                } // else if (eventType == XmlPullParser.TEXT)
                eventType = xpp.next();
            } // while (eventType != XmlPullParser.END_DOCUMENT)
        } // try
        catch (XmlPullParserException e) {
        } catch (IOException e) {
        }
        player.calculateWeight();

    }

    public void showToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();

    }

    private void saveUsingParser() {

        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "dungeon");
            serializer.startTag("", "player");

            addTag("health",player.getHp()+"",serializer);

            addTag("maxHealth",player.getMaxHp()+"",serializer);

            addTag("atk",player.getAttack()+"",serializer);

            addTag("maxWeight",player.getSpace()+"",serializer);

            addTag("level",player.getLevel()+"",serializer);

            addTag("experience",player.getExp()+"",serializer);

            addTag("currentRoom",player.getCurrentRoom()+"",serializer);

            serializer.startTag("", "used");

            if(player.getSword() != null) {
                serializer.startTag("", "sword");
                addTag("name",player.getSword().getName()+"",serializer);
                addTag("weight",player.getSword().getWeight()+"",serializer);
                addTag("mySwordAttack",player.getSword().getDamage()+"",serializer);
                serializer.endTag("", "sword");
            }

            //HERE
            if(player.getArmor() != null) {
                serializer.startTag("", "armor");
                addTag("name",player.getArmor().getName()+"",serializer);
                addTag("weight",player.getArmor().getWeight()+"",serializer);
                addTag("myArmorProtection",player.getArmor().getProtection()+"",serializer);
                serializer.endTag("", "armor");
            }
            serializer.endTag("","used");
            serializer.startTag("", "inventory");
            for(Item item : player.getItems()){
                if(item.getClass() == Sword.class){
                    addInventorySword((Sword)item,serializer);
                }
                if(item.getClass() == Armor.class){
                    addInventoryArmor((Armor)item,serializer);
                }
                if(item.getClass() == Cake.class){
                    addInventoryCake((Cake)item,serializer);
                }
            }
            serializer.endTag("", "inventory");
            serializer.endTag("", "player");
            for (Room room : dungeon) {
                if(!room.getDescription().equals(Room.NOTHING)) {
                    serializer.startTag("", "room");
                    if (room.getEast() != room.NO_EXIT) {
                        addTag("east", room.getEast() + "", serializer);

                    }
                    if (room.getWest() != room.NO_EXIT) {
                        addTag("west", room.getWest() + "", serializer);

                    }
                    if (room.getSouth() != room.NO_EXIT) {
                        addTag("south", room.getSouth() + "", serializer);

                    }
                    if (room.getNorth() != room.NO_EXIT) {
                        addTag("north", room.getNorth() + "", serializer);
                    }
                    addTag("description", room.getDescription(), serializer);

                    serializer.startTag("", "items");
                    for (Item item : room.getInventory()) {
                        if (item.getClass() == Sword.class) {
                            addSword((Sword) item, serializer);
                        }
                        if (item.getClass() == Armor.class) {
                            addArmor((Armor) item, serializer);
                        }
                        if (item.getClass() == Cake.class) {
                            addCake((Cake) item, serializer);
                        }
                    }
                    serializer.endTag("", "items");

                    if (room.getMonster() != null) {
                        serializer.startTag("", "monster");
                        addTag("hp", room.getMonster().getHp() + "", serializer);
                        addTag("exp", room.getMonster().getExp() + "", serializer);
                        addTag("attack", room.getMonster().getAttack() + "", serializer);
                        serializer.endTag("", "monster");
                    }
                    serializer.endTag("", "room");
                }
            }
            serializer.endTag("", "dungeon");
            serializer.endDocument();
            String result = writer.toString();

            FileOutputStream fos = openFileOutput("cities.xml",Context.MODE_PRIVATE);
            fos.write(result.getBytes(),0,result.length());
            fos.close();
            showToast("SAVED");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void addSword(Sword item, XmlSerializer serializer) throws Exception{
        serializer.startTag("", "sword");
        addTag("name",item.getName()+"",serializer);
        addTag("weight",item.getWeight()+"",serializer);
        addTag("swordAttack",item.getDamage()+"",serializer);
        serializer.endTag("", "sword");
    }
    private void addArmor(Armor item, XmlSerializer serializer) throws Exception{
        serializer.startTag("", "armor");
        addTag("name",item.getName()+"",serializer);
        addTag("weight",item.getWeight()+"",serializer);
        addTag("protection",item.getProtection()+"",serializer);
        serializer.endTag("", "armor");
    }
    private void addCake(Cake item, XmlSerializer serializer) throws Exception{
        serializer.startTag("", "cake");
        addTag("name",item.getName()+"",serializer);
        addTag("weight",item.getWeight()+"",serializer);
        addTag("heal",item.getHeal()+"",serializer);
        serializer.endTag("", "cake");
    }
    private void addTag(String tagName,String value,XmlSerializer serializer) throws Exception{
        serializer.startTag("", tagName);
        serializer.text(value);
        serializer.endTag("", tagName);
    }
    private void addInventorySword(Sword item, XmlSerializer serializer) throws Exception{
        serializer.startTag("", "sword");
        addTag("name",item.getName()+"",serializer);
        addTag("weight",item.getWeight()+"",serializer);
        addTag("inventorySwordAttack",item.getDamage()+"",serializer);
        serializer.endTag("", "sword");
    }
    private void addInventoryArmor(Armor item, XmlSerializer serializer) throws Exception{
        serializer.startTag("", "armor");
        addTag("name",item.getName()+"",serializer);
        addTag("weight",item.getWeight()+"",serializer);
        addTag("inventoryArmorProtection",item.getProtection()+"",serializer);
        serializer.endTag("", "armor");
    }
    private void addInventoryCake(Cake item, XmlSerializer serializer) throws Exception{
        {
            serializer.startTag("", "cake");
            addTag("name",item.getName()+"",serializer);
            addTag("weight",item.getWeight()+"",serializer);
            addTag("inventoryHeal",item.getHeal()+"",serializer);
            serializer.endTag("", "cake");
        }
    }

    public void readSave(){
        try {
            //String xmlFile = new String("saved.xml");


            FileInputStream fis = getApplicationContext().openFileInput("cities.xml");

            BufferedReader r = new BufferedReader(new InputStreamReader(fis));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line+"\n");
            }

            Log.d("File", "File contents: " + total);


            showToast("Loaded");

            XmlPullParserFactory xppf = XmlPullParserFactory.newInstance();
            xppf.setNamespaceAware(true);
            XmlPullParser xpp = xppf.newPullParser();

           // File myXML = new File(xmlFile); // give proper path
           // File myXML = new File(xmlFile);


            xpp.setInput(r);
            readXMLFile("old");


            r.close();
            fis.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
