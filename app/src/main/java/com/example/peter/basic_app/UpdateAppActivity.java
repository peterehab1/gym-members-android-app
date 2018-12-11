package com.example.peter.basic_app;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateAppActivity extends AppCompatActivity {

    DatabaseReference mRef;
    TextView versionText;
    TextView versionNumber;
    Button updateAppBtn;
    Button backBtn;

    static String updateLink;
    static String updateVersion;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_app);

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Versions");

        //Check for updates
        final DatabaseReference updatedAppLink = mRef.child("200").child("link");
        DatabaseReference updatedAppVersion = mRef.child("200").child("version");


        updatedAppLink.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                updateLink = dataSnapshot.getValue(String.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        updatedAppVersion.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                updateVersion = dataSnapshot.getValue(String.class);
                updateAppBtn.setText(updateVersion);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Font
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Questv1-Bold.otf");

        String version = null;
        try {
            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
            Log.i("Vesriion ", version);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        mRef = FirebaseDatabase.getInstance().getReference().child("Versions");
        versionText = findViewById(R.id.textview_vesrion_text);
        versionNumber = findViewById(R.id.textview_vesrion_number);
        updateAppBtn = findViewById(R.id.update_app_btn);
        backBtn = findViewById(R.id.back_btn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        updateAppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Update(updateLink);
            }
        });



        versionText.setTypeface(typeface);
        versionNumber.setTypeface(typeface);
        updateAppBtn.setTypeface(typeface);

        versionNumber.setText(version);

    }

    public void Update(String apkurl){

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //get destination to update file and set Uri
        //TODO: First I wanted to store my update .apk file on internal storage for my app but apparently android does not allow you to open and install
        //aplication with existing package from there. So for me, alternative solution is Download directory in external storage. If there is better
        //solution, please inform us in comment
        String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
        String fileName = "app-debug.apk";
        destination += fileName;
        final Uri uri = Uri.parse("file://" + destination);

        //Delete update file if exists
        File file = new File(destination);
        if (file.exists())
            //file.delete() - test this, I think sometimes it doesnt work
            file.delete();

        //get url of app on server
        String url = apkurl;

        //set downloadmanager
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle(getString(R.string.app_name));

        //set destination
        request.setDestinationUri(uri);

        // get download service and enqueue file
        final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        final long downloadId = manager.enqueue(request);

        Intent install = new Intent(Intent.ACTION_VIEW);
        install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        install.setDataAndType(uri,
                manager.getMimeTypeForDownloadedFile(downloadId));
        startActivity(install);
    }
}
