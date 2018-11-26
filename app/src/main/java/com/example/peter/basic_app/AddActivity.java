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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.peter.basic_app.HomeActivity.dateFormatter;

public class AddActivity extends AppCompatActivity {

    CalendarView startDate;

    Button saveBtn;
    EditText theName;
    DatabaseReference mDatabaseRef;
    RadioGroup radioGroup;
    RadioButton radioButton1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Button backBtn = (Button) findViewById(R.id.back_btn);


        saveBtn = (Button) findViewById(R.id.save_btn);
        startDate = (CalendarView) findViewById(R.id.start_date);
        theName = (EditText) findViewById(R.id.the_name);

        radioGroup = findViewById(R.id.radio_group);
        radioButton1 = findViewById(R.id.radioButton_1);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();



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

                    user.setName(theName.getText().toString());
                    user.setStartdate(dateFormatter(startDate.getDate(), "MM/dd/yyyy"));
                    user.setMembership((String) radioButton1.getText());
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
