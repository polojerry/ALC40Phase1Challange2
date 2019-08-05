package com.polotechnologies.travelmantics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class NewDealsActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;
    public static String SELECTED_DEAL = "Selected Deal";
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    FirebaseStorage mFirebaseStorage;
    StorageReference mStorageReference;

    AppCompatImageView mImageView;

    ProgressBar mProgressBar;
    TextInputEditText mDealName;
    TextInputEditText mDealDescription;
    TextInputEditText mDealPrice;

    MaterialButton mButtonDealSelectImage;

    Uri selectedImageUri;

    Deal mDeal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_deals);

        FirebaseUtil.InstantiateFirebaseReference("travelDeals", this);
        mFirebaseDatabase = FirebaseUtil.sFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.sDatabaseReference;
        mFirebaseStorage = FirebaseUtil.sFirebaseStorage;
        mStorageReference = FirebaseUtil.sStorageReference;

        mImageView = findViewById(R.id.img_deal_image);
        mDealName = findViewById(R.id.tv_location_name);
        mDealDescription = findViewById(R.id.tv_location_description);
        mDealPrice = findViewById(R.id.tv_location_price);
        mButtonDealSelectImage = findViewById(R.id.btn_select_image);
        mProgressBar = findViewById(R.id.pb_new_deal_progress_bar);

        Intent intent = getIntent();
        Deal deal = (Deal)intent.getSerializableExtra(SELECTED_DEAL);

        if(deal == null){
            mDeal = new Deal();
        }else{
            mDeal = deal;
            setDealValue(deal);
        }

        mButtonDealSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

    }

    private void setDealValue(Deal deal) {

        selectedImageUri = Uri.parse(deal.getDealImageUrl());
        Picasso.get()
                .load(deal.getDealImageUrl())
                .into(mImageView);
        mDealName.setText(deal.getDealName());
        mDealDescription.setText(deal.getDealDescription());
        mDealPrice.setText(deal.getDealPrice());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            if(data!=null){
                selectedImageUri =data.getData();
                Picasso.get()
                        .load(selectedImageUri)
                        .into(mImageView);
            }
        }
    }

    private void selectImage() {
        Intent selectImageIntent = new Intent();
        selectImageIntent.setType("image/*");
        selectImageIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(selectImageIntent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(FirebaseUtil.isAdmin){
            getMenuInflater().inflate(R.menu.new_deal_menu, menu);
        }else{
            isViewEnable(false);
        }
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.action_save:
                saveDeal();
                break;
            case R.id.action_delete:
                deleteDeal();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteDeal() {

        String dealId = mDeal.getDealId();

        if(mDeal == null){
            Toast.makeText(this, "Nothing to Delete", Toast.LENGTH_SHORT).show();

        }else{
            mDatabaseReference.child(dealId).removeValue();

            String storageLocation = mDeal.getDealPictureName();

            if(!storageLocation.isEmpty()){
                StorageReference picRef = mFirebaseStorage.getReference().child(storageLocation);
                picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(NewDealsActivity.this, "Deal Deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        finish();
                    }
                });

            }

        }
    }

    private void saveDeal() {
        mProgressBar.setVisibility(View.VISIBLE);
        if(isNewDealTextEmpty()) {
            return;
        }
        mDeal.setDealName(mDealName.getText().toString().trim());
        mDeal.setDealDescription(mDealDescription.getText().toString().trim());
        mDeal.setDealPrice(mDealPrice.getText().toString().trim());

        StorageReference pictureRef = mStorageReference.child(selectedImageUri.getLastPathSegment());
        pictureRef
                .putFile(selectedImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pictureRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String pictureName = pictureRef.getPath();
                        String imageUrl = uri.toString();

                        mDeal.setDealImageUrl(imageUrl);
                        mDeal.setDealPictureName(pictureName);

                        if(mDeal.getDealId() == null){
                            mDatabaseReference.push().setValue(mDeal).addOnSuccessListener(aVoid -> {
                                mProgressBar.setVisibility(View.GONE);
                                Toast.makeText(NewDealsActivity.this, "New Deal Added", Toast.LENGTH_SHORT).show();
                                backToDeals();
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(NewDealsActivity.this, "Failed to add New Deal", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            String dealId = mDeal.getDealId();
                            mDatabaseReference.child(dealId).setValue(mDeal).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(NewDealsActivity.this, "Deal Updated", Toast.LENGTH_SHORT).show();
                                    backToDeals();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(NewDealsActivity.this, "Failed to update Deal", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    }
                });

            }
        });

    }

    private void backToDeals() {
        startActivity(new Intent(NewDealsActivity.this,  DealsActivity.class));
        finish();
    }

    private void cleanDeal() {
        mDealName.setText("");
        mDealName.requestFocus();
        mDealDescription.setText("");
        mDealPrice.setText("");
    }

    private boolean isNewDealTextEmpty() {
        if(mDealName.getText().toString().trim().isEmpty()){
            setError(mDealName);
            return true;
        }
        if(mDealDescription.getText().toString().trim().isEmpty()){
            setError(mDealDescription);
            return true;
        }
        if(mDealPrice.getText().toString().trim().isEmpty()){
            setError(mDealPrice);
            return true;
        }
        if(selectedImageUri == null){
            Toast.makeText(this, "Please select an Image", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    private void setError(TextInputEditText editText) {
        editText.setError("Required");
    }

    public void isViewEnable(boolean isEnabled){

        mImageView.setEnabled(isEnabled);
        mDealName.setEnabled(isEnabled);
        mDealDescription.setEnabled(isEnabled);
        mDealPrice.setEnabled(isEnabled);

        mButtonDealSelectImage.setEnabled(isEnabled);

    }
}
