package com.example.moviezpoint.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.moviezpoint.Models.StoreFirebaseUser;
import com.example.moviezpoint.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shobhitpuri.custombuttons.GoogleSignInButton;
import com.tuyenmonkey.mkloader.MKLoader;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 1;
    private GoogleSignInClient mGoogleSignInClient;

    FirebaseAuth mAuth;
    DatabaseReference reference;

    MKLoader loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loader = findViewById(R.id.loader);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser presentUser = mAuth.getCurrentUser();
        if(presentUser!=null) {
            startActivity(new Intent(MainActivity.this,HomeActivity.class));
            finish();
        }

    }

    public void googleLogin(View view) {
        signIn();
    }

    @Override

    public void onStart() {
        super.onStart();

    }

    @Override

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);

                if (account != null) {
                    loader.setVisibility(View.VISIBLE);
                    firebaseAuthWithGoogle(account);
                }

            } catch (ApiException e) {

                Log.w(TAG, "Google sign in failed", e);

            }

        }

    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {

        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)

                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override

                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = mAuth.getCurrentUser();

                            reference = FirebaseDatabase.getInstance().getReference("Users");

                            if (user != null) {
                                String userName = acct.getDisplayName();
                                String userEmail = acct.getEmail();
                                String userImage = String.valueOf(acct.getPhotoUrl());
                                String userId = user.getUid();
                                checkUser(userName,userEmail,userImage,userId);

                            }


                        } else {

                            // If sign in fails, display a message to the user.

                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                            Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();

                            loader.setVisibility(View.GONE);
                        }
                    }

                });

    }

    private void checkUser(final String userName, final String userEmail, final String userImage, final String userId) {
        DatabaseReference checkReference = FirebaseDatabase.getInstance().getReference("Users");

        checkReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userId)) {
                    startActivity(new Intent(MainActivity.this,HomeActivity.class));
                    finish();
                }
                else {
                    StoreFirebaseUser storeFirebaseUser = new StoreFirebaseUser(userName,userEmail,userImage,userId,"","","","","");
                    reference.child(userId).setValue(storeFirebaseUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        loader.setVisibility(View.GONE);
    }


    private void signIn() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();

        startActivityForResult(signInIntent, RC_SIGN_IN);

    }
}
