package com.example.peter.basic_app;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class UpdateUserInfoActivity extends AppCompatActivity {

    EditText userName;
    ImageView userAvatar;
    FloatingActionButton saveBtn;
    TextView usernameInTop;
    Button backBtn;

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

        //customize fonts
        userName.setTypeface(typeface);
        usernameInTop.setTypeface(typeface);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        mChildKey = intent.getStringExtra("childKey");
        mUserName = intent.getStringExtra("userName");
        mUserAvatarURI = intent.getStringExtra("userAvatar");

        if (mUserAvatarURI == null){
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/test-project-798ce.appspot.com/o/Unknown_avatar.png?alt=media&token=263bc75c-cffe-4a48-8339-b4dcf6250054").fit().transform(new CircleTransform()).centerCrop().into(userAvatar);
        }else{
            Picasso.get().load(mUserAvatarURI).fit().transform(new CircleTransform()).centerCrop().into(userAvatar);
        }

        userName.setText(mUserName);
        usernameInTop.setText(mUserName);

        //Back Button
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });


    }



}
