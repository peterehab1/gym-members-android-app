package com.example.peter.basic_app;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

import static com.example.peter.basic_app.HomeActivity.dateFormatter;
import static com.example.peter.basic_app.HomeActivity.setMembershipForFirebaseDatabase;

public class AddActivity extends AppCompatActivity {

    CalendarView startDate;
    Button saveBtn;
    EditText theName;
    DatabaseReference mDatabaseRef;
    RadioGroup radioGroup;
    RadioButton radioButton1;
    EditText leftMoney;
    TextView dateStartTextview;
    DatePickerDialog.OnDateSetListener chooseDate;
    String theFinalDate;
    int year;
    int month;
    int day;

    public static ArrayList<String> eventsCountlist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Button backBtn = (Button) findViewById(R.id.back_btn);


        saveBtn = (Button) findViewById(R.id.save_btn);
       // startDate = (CalendarView) findViewById(R.id.start_date);
        theName = (EditText) findViewById(R.id.the_name);
        leftMoney = findViewById(R.id.left_money);
        radioGroup = findViewById(R.id.radio_group);
        radioButton1 = findViewById(R.id.radioButton_1);
        dateStartTextview = findViewById(R.id.date_start_textview);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        Calendar cal = Calendar.getInstance();
        final int year = cal.get(Calendar.YEAR);
        final int month = cal.get(Calendar.MONTH) + 1;
        final int day = cal.get(Calendar.DAY_OF_MONTH);

        theFinalDate = month + "/" + day + "/" + year;
        dateStartTextview.setText(theFinalDate);

        dateStartTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog dialog = new DatePickerDialog(AddActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, chooseDate, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });

        chooseDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                theFinalDate = month + "/" + dayOfMonth + "/" + year;
                dateStartTextview.setText(theFinalDate);
            }
        };



        saveBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {

                final Users user = new Users();
                // get selected radio button from radioGroup
                int selectedId = radioGroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                radioButton1 = (RadioButton) findViewById(selectedId);


                if (theName.getText().toString().isEmpty()){

                    Toast.makeText(getApplicationContext(), "لا يمكن ترك حقل الأسم فارغ", Toast.LENGTH_LONG).show();

                }else if(radioButton1 == null){

                    Toast.makeText(getApplicationContext(), "برجاء أختيار نظام الأشتراك", Toast.LENGTH_LONG).show();
                }else{

                    //Check if name is there before
                    Query firebaseQuery = mDatabaseRef.child("Users").orderByChild("name").startAt(theName.getText().toString()).endAt(theName.getText().toString() + "\uf8ff");
                    firebaseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() <= 0){
                                user.setName(theName.getText().toString());

                                user.setStartdate(theFinalDate);
                                user.setMembership(setMembershipForFirebaseDatabase(String.valueOf(radioButton1.getText())));

                                if (leftMoney.getText().toString().isEmpty()){
                                    user.setLeftmoney("0");
                                }else{
                                    user.setLeftmoney(leftMoney.getText().toString());
                                }

                                DatabaseReference newRef = mDatabaseRef.child("Users").push();
                                newRef.setValue(user);


                                Toast.makeText(getApplicationContext(), "تم الحفظ", Toast.LENGTH_LONG).show();
                                finish();
                            }else{

                                Toast.makeText(getApplicationContext(), "موجود مسبقاً", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


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
