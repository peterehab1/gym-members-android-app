package com.example.peter.basic_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.example.peter.basic_app.AddActivity.CAMERA_REQUEST_CODE;
import static com.example.peter.basic_app.HomeActivity.setMembershipForFirebaseDatabase;

public class UpdateUserInfoActivity extends AppCompatActivity {

    EditText userName;
    ImageView userAvatar;
    FloatingActionButton saveBtn;
    TextView usernameInTop;
    Button backBtn;

    StorageReference mStorageRef;
    Uri uri;
    byte[] mData;
    UploadTask uploadTask;
    ProgressDialog mProgressDialog;
    DatabaseReference mRef;

    String mUserName;
    String mUserAvatarURI;
    String mChildKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_info);

        //set fonts
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Questv1-Bold.otf");

        userName = findViewById(R.id.user_name_edit_text);
        userAvatar = findViewById(R.id.user_avatar);
        saveBtn = findViewById(R.id.save_btn_in_user_info);
        usernameInTop = findViewById(R.id.user_name_top);
        backBtn = findViewById(R.id.back_btn);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mProgressDialog = new ProgressDialog(this);

        final Map<String,Object> taskMap = new HashMap<String,Object>();


        //customize fonts
        userName.setTypeface(typeface);
        usernameInTop.setTypeface(typeface);

        // Get the Intent that started this activity and extract the string
        final Intent intent = getIntent();
        mChildKey = intent.getStringExtra("childKey");
        mUserName = intent.getStringExtra("userName");
        mUserAvatarURI = intent.getStringExtra("userAvatar");
        final ArrayList<String> listOfUsersNames = intent.getStringArrayListExtra("usersNamesArr");

        Picasso.get().load(mUserAvatarURI).fit().transform(new CircleTransform()).centerCrop().into(userAvatar);

        userName.setText(mUserName);
        usernameInTop.setText(mUserName);

        //Change image of the user
        userAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
                }

            }
        });


        //When save button is clicked
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Check to see if the name user trying to add already exist
                int found = 0;
                for (String string : listOfUsersNames) {
                    if(string.matches(userName.getText().toString()) && !userName.getText().toString().matches(mUserName)){
                        found = found + 1;
                    }
                }

                // Check if name or image is empty
                if (!userName.getText().toString().isEmpty() || mData != null){

                    //Check to see if the name user trying to add already exist
                    if(found > 0){
                        Toast.makeText(getApplicationContext(), "الأسم الذي تحاول تعديلة موجود مسبقاً", Toast.LENGTH_SHORT).show();
                    }else {
                        if (mData == null){

                            mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mChildKey);
                            final Map<String,Object> taskMap = new HashMap<String,Object>();

                            taskMap.put("name", userName.getText().toString());

                            mRef.updateChildren(taskMap);
                            Toast.makeText(UpdateUserInfoActivity.this, "تم تحديث بيانات العضو", Toast.LENGTH_SHORT).show();
                            finish();

                        }else{
                            //Toast.makeText(getApplicationContext(), "هذا اسم جديد", Toast.LENGTH_SHORT).show();

                            StorageReference oldAvatarRef = FirebaseStorage.getInstance().getReferenceFromUrl(mUserAvatarURI);
                            oldAvatarRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // File deleted successfully

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Uh-oh, an error occurred!

                                }
                            });



                            mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mChildKey);
                            final StorageReference pathname = mStorageRef.child("Pictures").child(String.valueOf(UUID.randomUUID()));


                           // mProgressDialog.setMessage("Uploading ...");
                          //  mProgressDialog.show();
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
                                        taskMap.put("image", downloadUri.toString());
                                        mRef.updateChildren(taskMap);

                                    }
                                }
                            });

                            taskMap.put("name", userName.getText().toString());
                            mRef.updateChildren(taskMap);
                            Toast.makeText(UpdateUserInfoActivity.this, "تم تحديث بيانات العضو", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    }
                }else{
                    Toast.makeText(getApplicationContext(), "لا يمكن ترك الأسم فارغ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Back Button
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

            userAvatar.setImageBitmap(imageBitmap);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            mData = baos.toByteArray();

        }
    }

}
