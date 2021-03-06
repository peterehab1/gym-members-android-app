package com.example.peter.basic_app;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.example.peter.basic_app.HomeActivity.setMembershipForFirebaseDatabase;

public class RenewMembershipActivity extends AppCompatActivity {

    FloatingActionButton saveBtn;
    RadioGroup radioGroup;
    RadioButton radioButton1;
    RadioButton radioButton2;
    RadioButton radioButton3;
    RadioButton radioButton4;
    RadioButton radioButton5;
    TextView datePicker;
    DatePickerDialog.OnDateSetListener chooseDate;
    String theFinalDate;
    ProgressDialog mProgressDialog;
    String childKey;
    DatabaseReference mRef;
    String userName;
    TextView mUsername;
    Button backBtn;
    Typeface typeface;
    TextView ttextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renew_membership);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        childKey = intent.getStringExtra("childKey");
        userName = intent.getStringExtra("userName");

        //Font
        typeface = Typeface.createFromAsset(getAssets(), "fonts/Questv1-Bold.otf");

        saveBtn = findViewById(R.id.save_btn);
        radioGroup = findViewById(R.id.radio_group);
        radioButton1 = findViewById(R.id.radioButton_1);
        radioButton2 = findViewById(R.id.radioButton_2);
        radioButton3 = findViewById(R.id.radioButton_3);
        radioButton4 = findViewById(R.id.radioButton_4);
        radioButton5 = findViewById(R.id.radioButton_5);
        datePicker = findViewById(R.id.date_picker_update);
        mProgressDialog = new ProgressDialog(this);
        mUsername = findViewById(R.id.user_name_edit_text);
        backBtn = findViewById(R.id.back_btn);
        ttextView = findViewById(R.id.ttextview);

        mUsername.setText(userName);

        mUsername.setTypeface(typeface);
        datePicker.setTypeface(typeface);
        radioButton1.setTypeface(typeface);
        radioButton2.setTypeface(typeface);
        radioButton3.setTypeface(typeface);
        radioButton4.setTypeface(typeface);
        radioButton5.setTypeface(typeface);
        ttextView.setTypeface(typeface);

        Calendar cal = Calendar.getInstance();
        final int year = cal.get(Calendar.YEAR);
        final int month = cal.get(Calendar.MONTH) + 1;
        final int day = cal.get(Calendar.DAY_OF_MONTH);

        theFinalDate = month + "/" + day + "/" + year;
        datePicker.setText(theFinalDate);

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog dialog = new DatePickerDialog(RenewMembershipActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, chooseDate, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });

        chooseDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                theFinalDate = month + "/" + dayOfMonth + "/" + year;
                datePicker.setText(theFinalDate);
            }
        };

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Users user = new Users();

                // get selected radio button from radioGroup
                int selectedId = radioGroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                radioButton1 = (RadioButton) findViewById(selectedId);

                if (!radioButton1.getText().toString().isEmpty()){
                    mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(childKey);
                    Map<String,Object> taskMap = new HashMap<String,Object>();

                    taskMap.put("membership", setMembershipForFirebaseDatabase(String.valueOf(radioButton1.getText())));
                    taskMap.put("startdate", theFinalDate);
                    mRef.updateChildren(taskMap);
                    finish();
                    Toast.makeText(RenewMembershipActivity.this,  " تم تجديد ألأشتراك " + " لمدة "+String.valueOf(radioButton1.getText()) + " للعضو " + userName, Toast.LENGTH_LONG).show();

                }else{
                    Toast.makeText(RenewMembershipActivity.this,  " يجب أختيار نظام الأشتراك ", Toast.LENGTH_SHORT).show();

                }


            }
        });


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });


    }
}
