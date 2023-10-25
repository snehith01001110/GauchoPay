package edu.ucsb.ece150.gauchopay;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class CardListActivity extends AppCompatActivity {

    private static final int RC_HANDLE_INTERNET_PERMISSION = 2;

    private ArrayList<String> cardArray;
    private ArrayAdapter adapter;

    private ListView cardList;
    private Handler handler = new Handler();
    private Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    // Launch the asynchronous process to grab the web API
                    new ReadWebServer(getApplicationContext()).execute("");
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list);

        // Ensure that we have Internet permissions
        int internetPermissionGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        if(internetPermissionGranted != PackageManager.PERMISSION_GRANTED) {
            final String[] permission = new String[] {Manifest.permission.INTERNET};
            ActivityCompat.requestPermissions(this, permission, RC_HANDLE_INTERNET_PERMISSION);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cardArray = new ArrayList<>();
        cardList = findViewById(R.id.cardList);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cardArray);
        cardList.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toAddCardActivity = new Intent(getApplicationContext(), AddCardActivity.class);
                startActivity(toAddCardActivity);
            }
        });

        cardList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int posID = (int) id;

                // If "lastAmount > 0" the last API call is a valid request (that the user must
                // respond to.
                if (ReadWebServer.getLastAmount() != 0) {
                    String cardNumber = cardArray.get(position);

                    // Send the card information to the web API
                    new WriteWebServer(getApplicationContext(), cardNumber).execute("");

                    // Reset the stored information from the last API call
                    ReadWebServer.resetLastAmount();
                }
            }
        });

        // Start the timer to poll the webserver every 5000 ms
        timer.schedule(task, 0, 5000);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Clear the cardArray to ensure no duplications
        cardArray.clear();

        // Retrieve saved cards from SharedPreferences (or other storage)
        SharedPreferences prefs = getSharedPreferences("CardData", MODE_PRIVATE);
        String savedCards = prefs.getString("cardArray", null);

        if (savedCards != null) {
            cardArray.addAll(Arrays.asList(savedCards.split(",")));
        }

        // Notify the adapter about data changes
        adapter.notifyDataSetChanged();
    }

}
