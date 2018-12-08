package com.example.peter.basic_app;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
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
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import static com.example.peter.basic_app.HomeActivity.dateFormatter;
import static com.example.peter.basic_app.HomeActivity.setMembershipForFirebaseDatabase;

public class AddActivity extends AppCompatActivity {

    CalendarView startDate;
    Button saveBtn;
    EditText theName;
    DatabaseReference mDatabaseRef;
    RadioGroup radioGroup;
    RadioButton radioButton1;
    EditText notes;
    TextView dateStartTextview;
    DatePickerDialog.OnDateSetListener chooseDate;
    String theFinalDate;
    TextView memberAvatar;
    StorageReference mStorageRef;
    Uri uri;
    byte[] mData;
    UploadTask uploadTask;
    ProgressDialog mProgressDialog;


    static final int CAMERA_REQUEST_CODE = 1;

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
        notes = findViewById(R.id.notes);
        radioGroup = findViewById(R.id.radio_group);
        radioButton1 = findViewById(R.id.radioButton_1);
        dateStartTextview = findViewById(R.id.date_start_textview);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        memberAvatar = findViewById(R.id.member_avatar);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mProgressDialog = new ProgressDialog(this);

        Calendar cal = Calendar.getInstance();
        final int year = cal.get(Calendar.YEAR);
        final int month = cal.get(Calendar.MONTH) + 1;
        final int day = cal.get(Calendar.DAY_OF_MONTH);

        theFinalDate = month + "/" + day + "/" + year;
        dateStartTextview.setText(theFinalDate);

        memberAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
                }
            }


        });





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

                final StorageReference pathname = mStorageRef.child("Pictures").child(String.valueOf(UUID.randomUUID()));

                if (mData == null){
                    Toast.makeText(AddActivity.this, "Nothing to uplaod", Toast.LENGTH_SHORT).show();
                    //Check if name is there before
                    Query firebaseQuery = mDatabaseRef.child("Users").orderByChild("name").startAt(theName.getText().toString()).endAt(theName.getText().toString() + "\uf8ff");
                    firebaseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() <= 0){
                                if (!theName.getText().toString().isEmpty() && !radioButton1.getText().toString().isEmpty()){
                                    user.setName(theName.getText().toString());
                                    user.setImage("https://firebasestorage.googleapis.com/v0/b/test-project-798ce.appspot.com/o/Unknown_avatar.png?alt=media&token=263bc75c-cffe-4a48-8339-b4dcf6250054");
                                    user.setStartdate(theFinalDate);
                                    user.setMembership(setMembershipForFirebaseDatabase(String.valueOf(radioButton1.getText())));


                                    if (notes.getText().toString().isEmpty()){
                                        user.setNotes("0");
                                    }else{
                                        user.setNotes(notes.getText().toString());
                                    }

                                    DatabaseReference newRef = mDatabaseRef.child("Users").push();
                                    newRef.setValue(user);


                                    Toast.makeText(getApplicationContext(), "تم الحفظ", Toast.LENGTH_LONG).show();
                                    finish();
                                }else{
                                    Toast.makeText(getApplicationContext(), "لا يمكنك ترك حقول فارغه", Toast.LENGTH_LONG).show();
                                }

                            }else{

                                Toast.makeText(getApplicationContext(), "موجود مسبقاً", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }else{
                    mProgressDialog.setMessage("Uploading ...");
                    mProgressDialog.show();
                    uploadTask = pathname.putBytes(mData);
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            return pathname.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                mProgressDialog.dismiss();
                                final Uri downloadUri = task.getResult();

                                //Check if name is there before
                                Query firebaseQuery = mDatabaseRef.child("Users").orderByChild("name").startAt(theName.getText().toString()).endAt(theName.getText().toString() + "\uf8ff");
                                firebaseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getChildrenCount() <= 0){
                                            if (!theName.getText().toString().isEmpty() && !radioButton1.getText().toString().isEmpty()){
                                                user.setName(theName.getText().toString());
                                                user.setImage(downloadUri.toString());
                                                user.setStartdate(theFinalDate);
                                                user.setMembership(setMembershipForFirebaseDatabase(String.valueOf(radioButton1.getText())));


                                                if (notes.getText().toString().isEmpty()){
                                                    user.setNotes("0");
                                                }else{
                                                    user.setNotes(notes.getText().toString());
                                                }

                                                DatabaseReference newRef = mDatabaseRef.child("Users").push();
                                                newRef.setValue(user);


                                                Toast.makeText(getApplicationContext(), "تم الحفظ", Toast.LENGTH_LONG).show();
                                                finish();
                                            }else{
                                                Toast.makeText(getApplicationContext(), "لا يمكنك ترك حقول فارغه", Toast.LENGTH_LONG).show();
                                            }

                                        }else{

                                            Toast.makeText(getApplicationContext(), "موجود مسبقاً", Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                Toast.makeText(AddActivity.this, "تم الرفع بنجاح من الثاني", Toast.LENGTH_SHORT).show();

                            } else {
                                // Handle failures
                                // ...
                            }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            ImageView imageView = (ImageView) findViewById(R.id.image_preview);
            imageView.setImageBitmap(imageBitmap);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            mData = baos.toByteArray();

        }
    }



}





