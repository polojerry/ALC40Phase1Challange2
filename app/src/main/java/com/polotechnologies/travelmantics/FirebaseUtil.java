package com.polotechnologies.travelmantics;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.AuthUI.IdpConfig.Builder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {

    private static FirebaseUtil sFirebaseUtil;

    public static ArrayList<Deal> sDeals;

    public static FirebaseDatabase sFirebaseDatabase;
    public static DatabaseReference sDatabaseReference;

    public static FirebaseAuth sAuth;
    public static FirebaseAuth.AuthStateListener sAuthStateListener;


    public static FirebaseStorage sFirebaseStorage;
    public static StorageReference sStorageReference;

    private static Activity caller;
    private static int RC_SIGN_IN = 100;

    public static boolean isAdmin;

    private  FirebaseUtil() {
    }

    public static void InstantiateFirebaseReference(String databaseReference, Activity callerActivity){
        if(sFirebaseUtil == null){
            sFirebaseUtil = new FirebaseUtil();
            sFirebaseDatabase = FirebaseDatabase.getInstance();
            sAuth = FirebaseAuth.getInstance();
            caller = callerActivity;
            sAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    caller.setTheme(R.style.AppTheme);

                    if(firebaseAuth.getCurrentUser() == null){
                        SignIn();
                    }else {
                        String userId = firebaseAuth.getUid();
                        checkIsAdmin(userId);
                    }

                }
            };
        }
        sDeals = new ArrayList<>();
        sDatabaseReference = sFirebaseDatabase.getReference().child(databaseReference);

        InstantiateFirebaseStorage();
    }

    private static void checkIsAdmin(String userId) {
        isAdmin = false;
        DatabaseReference admin = sFirebaseDatabase.getReference().child("admin").child(userId);
        admin.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                isAdmin = true;
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private static void SignIn(){
        // authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Creating and  launching sign-in intent
        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }
    private static void InstantiateFirebaseStorage(){
        sFirebaseStorage = FirebaseStorage.getInstance();
        sStorageReference = sFirebaseStorage.getReference("travelDealsPicture");
    }

    public static void addAuthStateListener(){
        sAuth.addAuthStateListener(sAuthStateListener);
    }

    public static void removeAuthStateListener(){
        sAuth.removeAuthStateListener(sAuthStateListener);
    }

}
