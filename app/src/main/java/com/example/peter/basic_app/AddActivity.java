package com.example.peter.basic_app;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
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
import com.google.android.gms.tasks.OnFailureListener;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.example.peter.basic_app.HomeActivity.dateFormatter;
import static com.example.peter.basic_app.HomeActivity.setMembershipForFirebaseDatabase;
import static com.example.peter.basic_app.RecyclerviewAdapter.arrList;

public class AddActivity extends AppCompatActivity {

    CalendarView startDate;
    Button saveBtn;
    EditText theName;
    DatabaseReference mRef;
    RadioGroup radioGroup;
    RadioButton radioButton1;
    RadioButton radioButton2;
    RadioButton radioButton3;
    RadioButton radioButton4;
    RadioButton radioButton5;
    EditText notes;
    TextView dateStartTextview;
    DatePickerDialog.OnDateSetListener chooseDate;
    String theFinalDate;
    StorageReference mStorageRef;
    byte[] mData;
    UploadTask uploadTask;
    ProgressDialog mProgressDialog;
    ImageView imageView;
    TextView ttextView;
    String defaultAvatar;

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

        //Font
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Questv1-Bold.otf");

        saveBtn = findViewById(R.id.save_btn);
        theName = findViewById(R.id.the_name);
        notes = findViewById(R.id.notes);
        radioGroup = findViewById(R.id.radio_group);
        radioButton1 = findViewById(R.id.radioButton_1);
        radioButton2 = findViewById(R.id.radioButton_2);
        radioButton3 = findViewById(R.id.radioButton_3);
        radioButton4 = findViewById(R.id.radioButton_4);
        radioButton5 = findViewById(R.id.radioButton_5);
        dateStartTextview = findViewById(R.id.date_start_textview);
        mRef = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mProgressDialog = new ProgressDialog(this);
        imageView = findViewById(R.id.image_preview);
        ttextView = findViewById(R.id.ttextview);

        theName.setTypeface(typeface);
        notes.setTypeface(typeface);
        radioButton1.setTypeface(typeface);
        radioButton2.setTypeface(typeface);
        radioButton3.setTypeface(typeface);
        radioButton4.setTypeface(typeface);
        radioButton5.setTypeface(typeface);
        dateStartTextview.setTypeface(typeface);
        ttextView.setTypeface(typeface);

        defaultAvatar = "https://firebasestorage.googleapis.com/v0/b/test-project-798ce.appspot.com/o/Unknown_avatar.png?alt=media&token=53afd6d0-3a65-474a-9e90-27b208a2602a";
        Picasso.get().load(defaultAvatar).fit().transform(new CircleTransform()).centerCrop().into(imageView);


        Calendar cal = Calendar.getInstance();
        final int year = cal.get(Calendar.YEAR);
        final int month = cal.get(Calendar.MONTH) + 1;
        final int day = cal.get(Calendar.DAY_OF_MONTH);

        theFinalDate = month + "/" + day + "/" + year;
        dateStartTextview.setText(theFinalDate);

        imageView.setOnClickListener(new View.OnClickListener() {
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


        //*********************************************************************

        //When save button is clicked
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Users user = new Users();

                // get selected radio button from radioGroup
                int selectedId = radioGroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                radioButton1 = (RadioButton) findViewById(selectedId);

                //Check to see if the name user trying to add already exist
                int found = 0;
                for (String string : arrList) {
                    if(string.matches(theName.getText().toString())){
                        found = found + 1;
                    }
                }

                // Check if name or image is empty
                if (!theName.getText().toString().isEmpty()){

                    //Check to see if the name user trying to add already exist
                    if(found > 0){
                        Toast.makeText(getApplicationContext(), "الأسم الذي تحاول تعديلة موجود مسبقاً", Toast.LENGTH_SHORT).show();
                    }else {

                        //If the user didn't add a picture
                        if (mData == null){

                            mRef = FirebaseDatabase.getInstance().getReference().child("Users").push();
                            user.setName(theName.getText().toString());
                            user.setImage(defaultAvatar);
                            user.setStartdate(theFinalDate);
                            user.setMembership(setMembershipForFirebaseDatabase(String.valueOf(radioButton1.getText())));

                            if (notes.getText().toString().isEmpty()){
                                user.setNotes("0");
                            }else{
                                user.setNotes(notes.getText().toString());
                            }

                            mRef.setValue(user);

                            Toast.makeText(getApplicationContext(), "تم الحفظ", Toast.LENGTH_LONG).show();
                            finish();

                        //If the user did add a picture
                        }else{

                            mRef = FirebaseDatabase.getInstance().getReference().child("Users").push();
                            final StorageReference pathname = mStorageRef.child("Pictures").child(String.valueOf(UUID.randomUUID()));

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

                                        final Uri downloadUri = task.getResult();
                                        user.setImage(downloadUri.toString());
                                        mRef.setValue(user);

                                    }
                                }
                            });

                            user.setName(theName.getText().toString());
                            user.setStartdate(theFinalDate);
                            user.setMembership(setMembershipForFirebaseDatabase(String.valueOf(radioButton1.getText())));

                            if (notes.getText().toString().isEmpty()){
                                user.setNotes("0");
                            }else{
                                user.setNotes(notes.getText().toString());
                            }

                            mRef.setValue(user);

                            Toast.makeText(AddActivity.this, "تم أضافة العضو", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    }
                }else{
                    Toast.makeText(getApplicationContext(), "لا يمكن ترك الأسم فارغ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //*********************************************************************


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

            imageView.setImageBitmap(imageBitmap);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            mData = baos.toByteArray();

        }
    }

}





