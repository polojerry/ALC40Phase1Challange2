package com.polotechnologies.travelmantics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
        if(!FirebaseUtil.isAdmin){
            hideFab();
        }


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

        FirebaseUtil.InstantiateFirebaseReference("travelDeals",this);
        final DealsRecyclerAdapter mAdapter = new DealsRecyclerAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        FirebaseUtil.addAuthStateListener();
        if(!FirebaseUtil.isAdmin){
            hideFab();
        }

    }

    public  void hideFab(){
        newDealFab.setVisibility(View.GONE);
    }
    public  void showFab(){
        newDealFab.setVisibility(View.VISIBLE);
    }
}
