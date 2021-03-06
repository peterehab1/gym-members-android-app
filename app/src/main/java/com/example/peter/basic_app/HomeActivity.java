package com.example.peter.basic_app;

import android.app.ActivityManager;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Predicates.equalTo;


public class HomeActivity extends AppCompatActivity implements ComponentCallbacks2 {

    private EditText searchWord;
    private RecyclerView searchList;
    private DatabaseReference mDatabaseRef;

    private TextView warningsCount;
    private TextView errorsCount;
    private List<Users> list;
    private com.github.clans.fab.FloatingActionButton fab;
    private com.github.clans.fab.FloatingActionButton fab2;

    private View lilView;
    private TextView connectionStatus;

    private List<String> oneWeekLeftMembers;
    private List<String> noWeekLeftMembers;

    private Button warningsBtn;
    private Button errorBtn;

    private String[] myArrayForWarnings;
    private String[] myArrayForErrors;

    private Button clearBtn;




    String TAG = "getDate";

    /**
     * Release memory when the UI becomes hidden or when system resources become low.
     * @param level the memory-related event that was raised.
     */
    public void onTrimMemory(int level) {

        // Determine which lifecycle or system event was raised.
        switch (level) {

            case ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN:

                /*
                   Release any UI objects that currently hold memory.

                   The user interface has moved to the background.
                */

                break;

            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE:
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW:
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL:

                /*
                   Release any memory that your app doesn't need to run.

                   The device is running low on memory while the app is running.
                   The event raised indicates the severity of the memory-related event.
                   If the event is TRIM_MEMORY_RUNNING_CRITICAL, then the system will
                   begin killing background processes.
                */

                break;

            case ComponentCallbacks2.TRIM_MEMORY_BACKGROUND:
            case ComponentCallbacks2.TRIM_MEMORY_MODERATE:
            case ComponentCallbacks2.TRIM_MEMORY_COMPLETE:

                /*
                   Release as much memory as the process can.

                   The app is on the LRU list and the system is running low on memory.
                   The event raised indicates where the app sits within the LRU list.
                   If the event is TRIM_MEMORY_COMPLETE, the process will be one of
                   the first to be terminated.
                */

                break;

            default:
                /*
                  Release any non-critical data structures.

                  The app received an unrecognized memory level value
                  from the system. Treat this as a generic low-memory message.
                */
                break;
        }
    }

    public void doSomethingMemoryIntensive() {

        // Before doing something that requires a lot of memory,
        // check to see whether the device is in a low memory state.
        ActivityManager.MemoryInfo memoryInfo = getAvailableMemory();

        if (!memoryInfo.lowMemory) {
            // Do memory intensive work ...
        }
    }

    // Get a MemoryInfo object for the device's current memory status.
    private ActivityManager.MemoryInfo getAvailableMemory() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);


        // initialise your views
        searchWord = findViewById(R.id.search_word);
        searchList = findViewById(R.id.search_list);
        warningsCount = findViewById(R.id.warnings_count);
        errorsCount = findViewById(R.id.errors_count);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        fab = findViewById(R.id.fab);
        fab2 = findViewById(R.id.fab2);

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), UpdateAppActivity.class);
                startActivity(i);
            }
        });


        clearBtn = findViewById(R.id.clear_btn);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchWord.setText("");
            }
        });

        lilView = findViewById(R.id.lil_view);
        connectionStatus = findViewById(R.id.connection_status);

        warningsBtn = findViewById(R.id.warnings_btn);
        errorBtn = findViewById(R.id.errors_btn);

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    lilView.setBackgroundColor(0xFF4caf50);
                    connectionStatus.setText("متصل بالأنترنت");
                } else {
                    lilView.setBackgroundColor(0xFFDF0A00);
                    connectionStatus.setText("غير متصل بالأنترنت" );
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });


        warningsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);

                builder.setTitle("سينتهي الأشتراك خلال أسبوع");
                builder.setNeutralButton("أغلاق", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


                builder.setItems(myArrayForWarnings, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();



            }
        });

        errorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);

                builder.setTitle("أنتهي أشتراك الأعضاء التاليين");
                builder.setNeutralButton("أغلاق", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });



                builder.setItems(myArrayForErrors, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();



            }
        });


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
                noWeekLeftMembers = new ArrayList<>();

                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){

                    Users users = dataSnapshot1.getValue(Users.class);
                    Users usersList = new Users();


                    String name = users.getName();
                    String membership = users.getMembership();
                    String startdate = users.getStartdate();
                    String image = users.getImage();
                    String notes = users.getNotes();
                    String key = dataSnapshot1.getKey();

                    String startDate = users.getStartdate();
                    String todayDate = dateFormatter(System.currentTimeMillis(), "MM/dd/yyyy");

                    Date date1 = new Date(startDate); //  Month/Day/Year
                    Date date2 = new Date(todayDate);

                    long diff = getDateDiff(date1, date2);

                    addToNotifications(diff, users.getMembership(),users.getName(), oneWeekLeftMembers, noWeekLeftMembers);

                    usersList.setName(name);
                    usersList.setMembership(membership);
                    usersList.setStartdate(startdate);
                    usersList.setNotes(notes);
                    usersList.setKey(key);
                    usersList.setImage(image);


                    //If date difference change image beside the name

                    list.add(usersList);

                    //Toast.makeText(getContext(),"This is : "+name,Toast.LENGTH_LONG).show();
                }

                if (oneWeekLeftMembers.size() > 0){
                    warningsCount.setText(String.valueOf(oneWeekLeftMembers.size()));
                }else{
                    warningsCount.setText("0");
                }

                if (noWeekLeftMembers.size() > 0){
                    errorsCount.setText(String.valueOf(noWeekLeftMembers.size()));
                }else{
                    errorsCount.setText("0");
                }


                RecyclerviewAdapter recyclerviewAdapter = new RecyclerviewAdapter(list, getApplicationContext());
                RecyclerView.LayoutManager layoutmanager = new LinearLayoutManager(getApplicationContext());
                recyclerviewAdapter.notifyDataSetChanged();

                searchList.setHasFixedSize(true);
                searchList.setLayoutManager(layoutmanager);
                searchList.setItemAnimator( new DefaultItemAnimator());
                searchList.setAdapter(recyclerviewAdapter);

                myArrayForWarnings = new String[oneWeekLeftMembers.size()];
                oneWeekLeftMembers.toArray(myArrayForWarnings);

                myArrayForErrors = new String[noWeekLeftMembers.size()];
                noWeekLeftMembers.toArray(myArrayForErrors);

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
                    String notes = users.getNotes();
                    String image = users.getImage();

                    usersList.setName(name);
                    usersList.setMembership(membership);
                    usersList.setStartdate(startdate);
                    usersList.setImage(image);
                    usersList.setKey(key);
                    usersList.setNotes(notes);

                    list.add(usersList);

                    //Toast.makeText(getContext(),"This is : "+name,Toast.LENGTH_LONG).show();
                }



                RecyclerviewAdapter recyclerviewAdapter = new RecyclerviewAdapter(list, getApplicationContext());
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

    public static String setMembershipForFirebaseDatabase(String startingDate){

        String i = "30";

        if (startingDate.matches("1 شهر")){
            i = "30";
            return i;
        }else if (startingDate.matches("3 أشهر")){
            i = "90";
            return i;
        }else if (startingDate.matches("6 أشهر")){
            i = "180";
            return i;
        }else if (startingDate.matches("عام واحد")){
            i = "360";
            return i;
        }else if (startingDate.matches("نصف  شهر")){
            i = "15";
            return i;
        }

        return i;

    }

    public static String getMembershipFromFirebaseDatabase(String mem){

        String i = "مكان نص";

        if (mem.matches("30")){
            i = "1 شهر";
            return i;
        }else if (mem.matches("90")){
            i = "3 أشهر";
            return i;
        }else if (mem.matches("180")){
            i = "6 أشهر";
            return i;
        }else if (mem.matches("360")){
            i = "عام واحد";
            return i;
        }else if (mem.matches("15")){
            i = "نصف  شهر";
            return i;
        }

        return i;
    }

    public static int check(long diff, String membership){

        int error = R.drawable.ic_err;
        int warning = R.drawable.ic_warning;
        int verify = R.drawable.ic_verified;

        if (membership.matches("30")){
            if (diff >= 30){
               return error;
            }else if(diff >= 22 && diff < 30){
                return warning;
            }else{
                return verify;
            }

        }else if (membership.matches("90")){
            if (diff >= 90){
                return error;
            }else if(diff >= 82 && diff < 90){
                return warning;
            }else{
                return verify;
            }
        }

        else if (membership.matches("180")){
            if (diff >= 180){
                return error;
            }else if(diff >= 172 && diff < 180){
                return warning;
            }else{
                return verify;
            }
        }

        else if (membership.matches("360")){
            if (diff >= 360){
                return error;
            }else if(diff >= 352 && diff < 360){
                return warning;
            }else{
                return verify;
            }
        }

        else if (membership.matches("15")){
            if (diff >= 15){
                return error;
            }else if(diff >= 8 && diff < 15){
                return warning;
            }else{
                return verify;
            }
        }

        return verify;
    }

    public void addToNotifications(long diff, String membership, String name, List<String> oneWeek, List<String> noWeeks){

        if (membership.matches("30")){

            if(diff >= 22 && diff < 30){
                oneWeek.add(name);
            }else if(diff >= 30){
                noWeeks.add(name);
            }

        }else if (membership.matches("90")){

            if(diff >= 82 && diff < 90){
                oneWeek.add(name);
            }else if(diff >= 90){
                noWeeks.add(name);
            }
        }

        else if (membership.matches("180")){
            if(diff >= 172 && diff < 180){
                oneWeek.add(name);
            }else if(diff >= 180){
                noWeeks.add(name);
            }
        }

        else if (membership.matches("360")){

            if(diff >= 352 && diff < 360){
                oneWeek.add(name);
            }else if(diff >= 360){
                noWeeks.add(name);
            }
        }

        else if (membership.matches("15")){

            if(diff >= 8 && diff < 15){
                oneWeek.add(name);
            }else if(diff >= 15){
                noWeeks.add(name);
            }
        }

    }

}
