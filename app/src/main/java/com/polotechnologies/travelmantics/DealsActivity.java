package com.polotechnologies.travelmantics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DealsActivity extends AppCompatActivity {

    FloatingActionButton newDealFab;
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_deals);
        newDealFab = findViewById(R.id.fab_new_deal);
        mRecyclerView = findViewById(R.id.rv_deals);


        newDealFab.setOnClickListener(v -> {
            Intent intent = new Intent(DealsActivity.this, NewDealsActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.deals_menu,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_log_out){
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(task -> Toast.makeText(DealsActivity.this, "Logged Out", Toast.LENGTH_SHORT).show());
            FirebaseUtil.removeAuthStateListener();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtil.removeAuthStateListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

        startLoad();

    }

    public  void hideFab(){
        newDealFab.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void startLoad(){
        FirebaseUtil.InstantiateFirebaseReference("travelDeals",this);
        final DealsRecyclerAdapter mAdapter = new DealsRecyclerAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        FirebaseUtil.addAuthStateListener();
        if(!FirebaseUtil.isAdmin){
            hideFab();
        }
    }
}
