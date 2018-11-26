package com.example.peter.basic_app;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.peter.basic_app.HomeActivity.dateFormatter;

public class AddActivity extends AppCompatActivity {

    Spinner spn;
    CalendarView startDate;
    CalendarView endDate;
    Button saveBtn;
    EditText theName;
    DatabaseReference mDatabaseRef;
    String thestartdate;
    String theenddate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Button backBtn = (Button) findViewById(R.id.back_btn);
        String[] membershipTypes = new String[] {
                "نصف شهر","1 شهر", "3 أشهر", "6 أشهر", "1 عام"
        };

        spn = (Spinner) findViewById(R.id.spn);
        saveBtn = (Button) findViewById(R.id.save_btn);
        startDate = (CalendarView) findViewById(R.id.start_date);
        theName = (EditText) findViewById(R.id.the_name);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, membershipTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn.setAdapter(adapter);
        spn.setSelection(adapter.getPosition("1 شهر"));

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {

                final Users user = new Users();
                if (theName.getText().toString().isEmpty()){

                    Toast.makeText(getApplicationContext(), "لا يمكن ترك حقل الأسم فارغ", Toast.LENGTH_LONG).show();

                }else{

                    user.setName(theName.getText().toString());
                    user.setMembership(spn.getSelectedItem().toString());
                    user.setStartdate(dateFormatter(startDate.getDate(), "MM/dd/yyyy"));
                    DatabaseReference newRef = mDatabaseRef.child("Users").push();
                    newRef.setValue(user);
                    Toast.makeText(getApplicationContext(), "تم الحفظ", Toast.LENGTH_LONG).show();
                    finish();
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
