package com.example.moviezpoint.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.moviezpoint.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tuyenmonkey.mkloader.MKLoader;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class FeedbackActivity extends AppCompatActivity {

    EditText editTextFeedback;

    DatabaseReference reference;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    MKLoader loader;

    String userID = "";
    String feedback="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        loader = findViewById(R.id.loader);

        editTextFeedback = findViewById(R.id.etFeedback);

        reference = FirebaseDatabase.getInstance().getReference().child("UserFeedback");
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        userID = firebaseUser.getUid();
    }

    public void imageBack(View view) {
        onBackPressed();
    }

    public void submitFeedback(View view) {

        feedback = editTextFeedback.getText().toString();

        if (TextUtils.isEmpty(feedback)) {
            Toast.makeText(this, "Please write a feedback before submitting", Toast.LENGTH_SHORT).show();
        }
        else {
            loader.setVisibility(View.VISIBLE);
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
            String currentTime = new SimpleDateFormat("HHmmss", Locale.getDefault()).format(new Date());
            String formattedDate = df.format(c.getTime());

            String passValue = formattedDate+currentTime;

            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("feedback",feedback);

            reference.child(userID).child(passValue).setValue(hashMap);
            Toast.makeText(this, "Feedback Submitted Successfully", Toast.LENGTH_SHORT).show();
            editTextFeedback.setText("");
            loader.setVisibility(View.GONE);
        }

    }
}
