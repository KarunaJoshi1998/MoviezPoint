package com.example.moviezpoint.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.moviezpoint.Models.StoreFirebaseUser;
import com.example.moviezpoint.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuyenmonkey.mkloader.MKLoader;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    CircleImageView imageProfile;
    TextInputEditText editTextEmail, editTextMobile, editTextDate, editTextState, editTextCity, editTextAddress;
    Button buttonSaveChanges;
    TextView textViewName;
    FloatingActionButton fabEdit;

    MKLoader loader;
    ScrollView scrollView;

    DatabaseReference reference;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    String uID="";
    String profileImage;

    String userName, userEmail, userImage, userID, userMobile, userDate, userState, userCity, userAddress;

    public ProfileFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_profile,container,false);

        reference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        uID = firebaseUser.getUid();

        imageProfile = rootView.findViewById(R.id.profile_image);

        editTextEmail = rootView.findViewById(R.id.profile_email);
        editTextMobile = rootView.findViewById(R.id.profile_mobile);
        editTextDate = rootView.findViewById(R.id.profile_date);
        editTextState = rootView.findViewById(R.id.profile_state);
        editTextCity = rootView.findViewById(R.id.profile_city);
        editTextAddress = rootView.findViewById(R.id.profile_address);

        textViewName = rootView.findViewById(R.id.profile_name);

        fabEdit = rootView.findViewById(R.id.edit_changes_fab);

        buttonSaveChanges = rootView.findViewById(R.id.btn_save_profile);

        loader = rootView.findViewById(R.id.loader);
        scrollView = rootView.findViewById(R.id.scrollView);
        scrollView.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);

        getUserData(rootView);

        fabEdit.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                setAllClickable();
                buttonSaveChanges.setVisibility(View.VISIBLE);
                fabEdit.setVisibility(View.GONE);
            }
        });

        if (buttonSaveChanges.isClickable()) {
            buttonSaveChanges.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getAllText();
                }
            });
        }

        return rootView;
    }

    private void getAllText() {
        String name = textViewName.getText().toString();
        String email = editTextEmail.getText().toString();
        String mobile = editTextMobile.getText().toString();
        String image = userImage;
        String date = editTextDate.getText().toString();
        String state = editTextState.getText().toString();
        String city = editTextCity.getText().toString();
        String address = editTextAddress.getText().toString();
        String userId = userID;

        UploadChangesToFirebase(name,email,mobile,image,date,state,city,address,userId);

    }

    @SuppressLint("RestrictedApi")
    private void UploadChangesToFirebase(String name, String email, String mobile, String image, String date, String state, String city, String address, String userId) {

        loader.setVisibility(View.VISIBLE);

        StoreFirebaseUser storeFirebaseUser = new StoreFirebaseUser(name,email,image,userId,mobile,date,state,city,address);

        reference.child("Users").child(userId).setValue(storeFirebaseUser);
        buttonSaveChanges.setVisibility(View.GONE);
        setAllNonClickable();
        loader.setVisibility(View.GONE);
        fabEdit.setVisibility(View.VISIBLE);
    }

    private void setAllNonClickable() {
        editTextAddress.setEnabled(false);
        editTextCity.setEnabled(false);
        editTextState.setEnabled(false);
        editTextDate.setEnabled(false);
        editTextMobile.setEnabled(false);
    }

    private void setAllClickable() {
        editTextAddress.setEnabled(true);
        editTextCity.setEnabled(true);
        editTextState.setEnabled(true);
        editTextDate.setEnabled(true);
        editTextMobile.setEnabled(true);
    }

    private void getUserData(final View v) {
        reference.child("Users").child(uID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StoreFirebaseUser getData = dataSnapshot.getValue(StoreFirebaseUser.class);
                Glide.with(v)
                        .load(getData.getUserImage())
                        .into(imageProfile);

                textViewName.setText(getData.getUserName());

                editTextEmail.setText(getData.getUserEmail());
                editTextMobile.setText(getData.getUserMobile());
                editTextDate.setText(getData.getUserDate());
                editTextState.setText(getData.getUserState());
                editTextCity.setText(getData.getUserCity());
                editTextAddress.setText(getData.getUserAddress());

                profileImage = getData.getUserImage();

                saveDataToPage(getData);
                loader.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load data... Please try again", Toast.LENGTH_SHORT).show();
                loader.setVisibility(View.GONE);
            }
        });
    }

    private void saveDataToPage(StoreFirebaseUser getData) {
        userName = getData.getUserName();
        userEmail = getData.getUserEmail();
        userImage = getData.getUserImage();
        userID = getData.getUserId();
        userMobile = getData.getUserMobile();
        userDate = getData.getUserDate();
        userState = getData.getUserState();
        userCity = getData.getUserCity();
        userAddress = getData.getUserAddress();
    }
}
