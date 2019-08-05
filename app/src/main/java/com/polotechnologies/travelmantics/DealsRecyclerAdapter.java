package com.polotechnologies.travelmantics;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DealsRecyclerAdapter extends RecyclerView.Adapter<DealsRecyclerAdapter.ViewHolder> {

    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;

    private ArrayList<Deal> mDeals;


    public DealsRecyclerAdapter() {
        mDatabaseReference = FirebaseUtil.sDatabaseReference;
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Deal deal = dataSnapshot.getValue(Deal.class);
                if (deal != null) {
                    deal.setDealId(dataSnapshot.getKey());
                }
                mDeals = FirebaseUtil.sDeals;
                mDeals.add(deal);
                notifyItemInserted(mDeals.size() - 1);
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

        };

        mDatabaseReference.addChildEventListener(mChildEventListener);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_deal,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Deal deal = mDeals.get(position);

        holder.Bind(deal);

    }

    @Override
    public int getItemCount() {
        return mDeals == null ? 0 : mDeals.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView dealImage;
        AppCompatTextView dealName;
        AppCompatTextView dealDescription;
        AppCompatTextView dealPrice;


        private ViewHolder(@NonNull View itemView) {
            super(itemView);

            dealImage = itemView.findViewById(R.id.img_deal);
            dealName = itemView.findViewById(R.id.tv_deal_name);
            dealDescription = itemView.findViewById(R.id.tv_deal_description);
            dealPrice = itemView.findViewById(R.id.tv_deal_price);

            itemView.setOnClickListener(this);
        }

        private void Bind(Deal deal){
            dealName.setText(deal.getDealName());
            dealDescription.setText(deal.getDealDescription());
            dealPrice.setText(deal.getDealPrice());

            Picasso.get()
                    .load(deal.getDealImageUrl())
                    .into(dealImage);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Deal selectedDeal = mDeals.get(position);

            Intent intent = new Intent(v.getContext(),NewDealsActivity.class);
            intent.putExtra(NewDealsActivity.SELECTED_DEAL, selectedDeal);
            v.getContext().startActivity(intent);


        }
    }
}
