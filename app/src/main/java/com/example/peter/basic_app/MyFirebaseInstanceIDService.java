package com.example.peter.basic_app;

import android.app.Service;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "TOOKEN";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        Log.d(TAG, "This is the token: " + refreshedToken);
        sendRegistrationToServer(refreshedToken);


    }

    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
    }
}
