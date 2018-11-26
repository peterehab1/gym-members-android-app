package com.example.peter.basic_app;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.net.NoRouteToHostException;
import java.security.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.opencensus.tags.Tag;


public class HomeActivity extends AppCompatActivity {

    private EditText searchWord;
    private RecyclerView searchList;
    private DatabaseReference mDatabaseRef;
    private TextView notificationCount;
    private List<Users> list;
    private FloatingActionButton fab;
    private List<String> oneWeekLeftMembers;
    String TAG = "getDate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        // initialise your views
        searchWord = (EditText) findViewById(R.id.search_word);
        searchList = (RecyclerView) findViewById(R.id.search_list);
        notificationCount = (TextView) findViewById(R.id.notf_count);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        fab = (FloatingActionButton) findViewById(R.id.fab);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, AddActivity.class));
            }
        });


        getMyUsers();


        searchWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchForUser(searchWord.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    public void getMyUsers(){
        mDatabaseRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list = new ArrayList<>();
                oneWeekLeftMembers = new ArrayList<>();

                StringBuffer stringBuffer = new StringBuffer();

                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    Users users = dataSnapshot1.getValue(Users.class);
                    Users usersList = new Users();
                    Users oneWeekMembers = new Users();

                    String name = users.getName();
                    String membership = users.getMembership();
                    String startdate = users.getStartdate();
                    String key = dataSnapshot1.getKey();


                    String startDate = users.getStartdate();
                    String todayDate = dateFormatter(System.currentTimeMillis(), "MM/dd/yyyy");

                    Date date1 = new Date(startDate); //  Month/Day/Year
                    Date date2 = new Date(todayDate);

                    long diff = getDateDiff(date1, date2);

                   if(diff >= 22 && diff < 30){
                       oneWeekLeftMembers.add(dataSnapshot1.getKey());
                    }

                    usersList.setName(name);
                    usersList.setMembership(membership);
                    usersList.setStartdate(startdate);
                    usersList.setKey(key);


                    //If date difference change image beside the name

                    list.add(usersList);

                    //Toast.makeText(getContext(),"This is : "+name,Toast.LENGTH_LONG).show();
                }

                if (oneWeekLeftMembers.size() > 0){
                    notificationCount.setText(String.valueOf(oneWeekLeftMembers.size()));
                }else{
                    notificationCount.setText("");
                }


                RecyclerviewAdapter recyclerviewAdapter = new RecyclerviewAdapter(list);
                RecyclerView.LayoutManager layoutmanager = new LinearLayoutManager(getApplicationContext());
                recyclerviewAdapter.notifyDataSetChanged();

                searchList.setHasFixedSize(true);
                searchList.setLayoutManager(layoutmanager);
                searchList.setItemAnimator( new DefaultItemAnimator());
                searchList.setAdapter(recyclerviewAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void searchForUser(String searchWord){
        Query firebaseQuery = mDatabaseRef.orderByChild("name").startAt(searchWord).endAt(searchWord + "\uf8ff");
        firebaseQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                list = new ArrayList<>();


                StringBuffer stringBuffer = new StringBuffer();

                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    Users users = dataSnapshot1.getValue(Users.class);
                    Users usersList = new Users();


                    String name = users.getName();
                    String membership = users.getMembership();
                    String startdate = users.getStartdate();
                    String key = dataSnapshot1.getKey();

                    usersList.setName(name);
                    usersList.setMembership(membership);
                    usersList.setStartdate(startdate);
                    usersList.setKey(key);

                    list.add(usersList);

                    //Toast.makeText(getContext(),"This is : "+name,Toast.LENGTH_LONG).show();
                }



                RecyclerviewAdapter recyclerviewAdapter = new RecyclerviewAdapter(list);
                RecyclerView.LayoutManager layoutmanager = new LinearLayoutManager(getApplicationContext());
                recyclerviewAdapter.notifyDataSetChanged();

                searchList.setHasFixedSize(true);
                searchList.setLayoutManager(layoutmanager);
                searchList.setItemAnimator( new DefaultItemAnimator());
                searchList.setAdapter(recyclerviewAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static long getDateDiff(Date startDate, Date endDate) {

        long diff = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long elapsedDays = diff / daysInMilli;
        return elapsedDays;


    }

    public static String dateFormatter(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }


}
