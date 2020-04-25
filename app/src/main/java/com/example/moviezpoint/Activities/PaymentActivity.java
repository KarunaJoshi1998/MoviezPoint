package com.example.moviezpoint.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.moviezpoint.Models.ApiInterface;
import com.example.moviezpoint.Models.GetAndSetBookedSeats;
import com.example.moviezpoint.Models.GetNotificationsModel;
import com.example.moviezpoint.Models.JavaMailAPI;
import com.example.moviezpoint.Models.MovieDetailsModel;
import com.example.moviezpoint.Models.StoreFirebaseUser;
import com.example.moviezpoint.Models.URLs;
import com.example.moviezpoint.Models.createBookingPDF;
import com.example.moviezpoint.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.tuyenmonkey.mkloader.MKLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class PaymentActivity extends AppCompatActivity implements PaytmPaymentTransactionCallback {

    TextView textView;
    RoundedImageView imageView;
    String mid="ZzqSoL76017814651604",orderId="",custId="",amt="";
    ArrayList<String> seatList;
    DatabaseReference databaseReference,reference,notificationReference;
    Long movieId;
    String theatre, movieName, showDate, showTime,transactionID;
    List<String> totalSeatsList;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    String userMail;

    Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(URLs.BASE_URL)
            .build();



    ApiInterface apiInterface = retrofit.create(ApiInterface.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        textView = findViewById(R.id.amount);
        imageView = findViewById(R.id.paytm_image);
        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");
        custId = intent.getStringExtra("custId");
        amt = intent.getStringExtra("amt");
        movieId = intent.getLongExtra("movieId",0);
        movieName = intent.getStringExtra("movieName");
        showDate = intent.getStringExtra("showDate");
        showTime = intent.getStringExtra("showTime");
        theatre = intent.getStringExtra("theatre");
        seatList = intent.getStringArrayListExtra("seatList");
        totalSeatsList = intent.getStringArrayListExtra("totalSeats");
        textView.setText(amt);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("MovieSeats");
        reference = FirebaseDatabase.getInstance().getReference().child("UserBookingData");
        notificationReference = FirebaseDatabase.getInstance().getReference().child("Notifications");

        userMail = firebaseUser.getEmail();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(PaymentActivity.this, Manifest.permission.READ_SMS) + ContextCompat.checkSelfPermission(PaymentActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(PaymentActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS},101);
                }

                if (ContextCompat.checkSelfPermission(PaymentActivity.this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(PaymentActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    sendUserDetailTOServerdd dl = new sendUserDetailTOServerdd();
                    dl.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        });
    }

    private void getUserDetails() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.child(custId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StoreFirebaseUser storeFirebaseUser = dataSnapshot.getValue(StoreFirebaseUser.class);
                getMovieDataForPDF(storeFirebaseUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void StoreFirebaseUserData() {
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("movieId", String.valueOf(movieId));
        hashMap.put("userId",custId);
        hashMap.put("movieName",movieName);
        hashMap.put("theatre",theatre);
        hashMap.put("date",showDate);
        hashMap.put("timing",showTime);
        hashMap.put("TransactionID",transactionID);

        HashMap<String,String> seatHashMap = new HashMap<>();
        int size = seatList.size();
        int i;
        for (i=0;i<size;i++) {
            seatHashMap.put("seat"+(i+1),seatList.get(i));
        }

        reference.child(custId).child(showDate).child(orderId).setValue(hashMap);
        reference.child(custId).child(showDate).child(orderId).child("seats").setValue(seatHashMap);

        storeNotification();
    }

    private void storeNotification() {

        String title = "Movie Booked";
        String description = "Your Movie "+movieName+" at "+theatre+" on "+showDate+" at "+showTime+" is booked with transaction ID "+transactionID+".";
        String amount = amt;
        String status = "Paid";
        GetNotificationsModel notificationsModel = new GetNotificationsModel(title,description,status,amount);
        notificationReference.child(custId).child(orderId).setValue(notificationsModel);

        sendMailTickets();
    }

    private void sendMailTickets() {
        getUserDetails();
        String abc = seatList.toString();
        String cut = abc.substring(1,abc.length()-1);

        String description = "Your Movie "+movieName+" at "+theatre+" on "+showDate+" at "+showTime+" is booked with transaction ID "+transactionID+" "+"for seat number "+cut;
        JavaMailAPI javaMailAPI = new JavaMailAPI(PaymentActivity.this,userMail,"Movie Ticket Booked",description);
        javaMailAPI.execute();
    }

    private void getMovieDataForPDF(final StoreFirebaseUser firebaseUser) {
        Call<MovieDetailsModel> movieDetailsModelCall = apiInterface.MOVIE_DETAILS_MODEL_CALL(movieId);
        movieDetailsModelCall.enqueue(new Callback<MovieDetailsModel>() {
            @Override
            public void onResponse(Call<MovieDetailsModel> call, Response<MovieDetailsModel> response) {
                MovieDetailsModel movieDetailsModel = response.body();
                createBookingPDF bookingPDF = new createBookingPDF(PaymentActivity.this);
                boolean isPDF = false;
                isPDF = bookingPDF.createPDF(movieDetailsModel,theatre,showDate,showTime,amt,seatList,transactionID,firebaseUser);

                if (isPDF) {
                    Intent successIntent = new Intent(PaymentActivity.this,PaymentSuccessActivity.class);
                    startActivity(successIntent);
                    finish();
                }
                else {
                    Toast.makeText(PaymentActivity.this, "Failed to generate tickets", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MovieDetailsModel> call, Throwable t) {

            }
        });
    }

    private void UploadDataToFirebase() {

        GetAndSetBookedSeats setBookedSeats = new GetAndSetBookedSeats();

        if (totalSeatsList.contains("1")) {
            setBookedSeats.setSeat1(false);
        }
        else {
            setBookedSeats.setSeat1(true);
        }
        if (totalSeatsList.contains("2")) {
            setBookedSeats.setSeat2(false);
        }
        else {
            setBookedSeats.setSeat2(true);
        }
        if (totalSeatsList.contains("3")) {
            setBookedSeats.setSeat3(false);
        }
        else {
            setBookedSeats.setSeat3(true);
        }
        if (totalSeatsList.contains("4")) {
            setBookedSeats.setSeat4(false);
        }
        else {
            setBookedSeats.setSeat4(true);
        }
        if (totalSeatsList.contains("5")) {
            setBookedSeats.setSeat5(false);
        }
        else {
            setBookedSeats.setSeat5(true);
        }
        if (totalSeatsList.contains("6")) {
            setBookedSeats.setSeat6(false);
        }
        else {
            setBookedSeats.setSeat6(true);
        }
        if (totalSeatsList.contains("7")) {
            setBookedSeats.setSeat7(false);
        }
        else {
            setBookedSeats.setSeat7(true);
        }
        if (totalSeatsList.contains("8")) {
            setBookedSeats.setSeat8(false);
        }
        else {
            setBookedSeats.setSeat8(true);
        }
        if (totalSeatsList.contains("9")) {
            setBookedSeats.setSeat9(false);
        }
        else {
            setBookedSeats.setSeat9(true);
        }
        if (totalSeatsList.contains("10")) {
            setBookedSeats.setSeat10(false);
        }
        else {
            setBookedSeats.setSeat10(true);
        }
        if (totalSeatsList.contains("11")) {
            setBookedSeats.setSeat11(false);
        }
        else {
            setBookedSeats.setSeat11(true);
        }
        if (totalSeatsList.contains("12")) {
            setBookedSeats.setSeat12(false);
        }
        else {
            setBookedSeats.setSeat12(true);
        }
        if (totalSeatsList.contains("13")) {
            setBookedSeats.setSeat13(false);
        }
        else {
            setBookedSeats.setSeat13(true);
        }
        if (totalSeatsList.contains("14")) {
            setBookedSeats.setSeat14(false);
        }
        else {
            setBookedSeats.setSeat14(true);
        }
        if (totalSeatsList.contains("15")) {
            setBookedSeats.setSeat15(false);
        }
        else {
            setBookedSeats.setSeat15(true);
        }
        if (totalSeatsList.contains("16")) {
            setBookedSeats.setSeat16(false);
        }
        else {
            setBookedSeats.setSeat16(true);
        }
        if (totalSeatsList.contains("17")) {
            setBookedSeats.setSeat17(false);
        }
        else {
            setBookedSeats.setSeat17(true);
        }
        if (totalSeatsList.contains("18")) {
            setBookedSeats.setSeat18(false);
        }
        else {
            setBookedSeats.setSeat18(true);
        }
        if (totalSeatsList.contains("19")) {
            setBookedSeats.setSeat19(false);
        }
        else {
            setBookedSeats.setSeat19(true);
        }
        if (totalSeatsList.contains("20")) {
            setBookedSeats.setSeat20(false);
        }
        else {
            setBookedSeats.setSeat20(true);
        }

        databaseReference.child(String.valueOf(movieId)).child(theatre).child(showDate).child(showTime).setValue(setBookedSeats);
        StoreFirebaseUserData();
    }

    public void imageBack(View view) {
        onBackPressed();
    }


    public class sendUserDetailTOServerdd extends AsyncTask<ArrayList<String>, Void, String> {
        private ProgressDialog dialog = new ProgressDialog(PaymentActivity.this);

        String url ="https://mymoviezpoint.000webhostapp.com/paytm/generateChecksum.php";
        String varifyurl = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";

        String CHECKSUMHASH ="";
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait");
            this.dialog.show();
        }
        protected String doInBackground(ArrayList<String>... alldata) {
            JSONParser jsonParser = new JSONParser(PaymentActivity.this);
            String param=
                    "MID="+mid+
                            "&ORDER_ID=" + orderId+
                            "&CUST_ID="+custId+
                            "&CHANNEL_ID=WAP&TXN_AMOUNT="+amt+"&WEBSITE=WEBSTAGING"+
                            "&CALLBACK_URL="+ varifyurl+"&INDUSTRY_TYPE_ID=Retail";
            JSONObject jsonObject = jsonParser.makeHttpRequest(url,"POST",param);

            Log.e("CheckSum result >>",jsonObject.toString());
            if(jsonObject != null){
                Log.e("CheckSum result >>",jsonObject.toString());
                try {
                    CHECKSUMHASH=jsonObject.has("CHECKSUMHASH")?jsonObject.getString("CHECKSUMHASH"):"";
                    Log.e("CheckSum result >>",CHECKSUMHASH);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return CHECKSUMHASH;
        }
        @Override
        protected void onPostExecute(String result) {
            Log.e(" setup acc ","  signup result  " + result);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            PaytmPGService Service = PaytmPGService.getStagingService();

            HashMap<String, String> paramMap = new HashMap<String, String>();

            paramMap.put("MID", mid);
            paramMap.put("ORDER_ID", orderId);
            paramMap.put("CUST_ID", custId);
            paramMap.put("CHANNEL_ID", "WAP");
            paramMap.put("TXN_AMOUNT", amt);
            paramMap.put("WEBSITE", "WEBSTAGING");
            paramMap.put("CALLBACK_URL" ,varifyurl);

            paramMap.put("CHECKSUMHASH" ,CHECKSUMHASH);

            paramMap.put("INDUSTRY_TYPE_ID", "Retail");
            PaytmOrder Order = new PaytmOrder(paramMap);
            Log.e("checksum ", "param "+ paramMap.toString());
            Service.initialize(Order,null);

            Service.startPaymentTransaction(PaymentActivity.this, true, true,
                    PaymentActivity.this  );
        }
    }

    @Override
    public void onTransactionResponse(Bundle bundle) {
        Log.e("checksum ", " respon true " + bundle.toString());
        if (bundle.getString("STATUS").equals("TXN_SUCCESS")) {
            transactionID = bundle.getString("TXNID");
            UploadDataToFirebase();
        }
        else {
            Intent failureIntent = new Intent(PaymentActivity.this,PaymentFailureActivity.class);
            startActivity(failureIntent);
            finish();
        }
    }

    @Override
    public void networkNotAvailable() {
    }

    @Override
    public void clientAuthenticationFailed(String s) {
    }

    @Override
    public void someUIErrorOccurred(String s) {
        Log.e("checksum ", " ui fail respon  "+ s );
    }

    @Override
    public void onErrorLoadingWebPage(int i, String s, String s1) {
        Log.e("checksum ", " error loading pagerespon true "+ s + "  s1 " + s1);
    }

    @Override
    public void onBackPressedCancelTransaction() {
        Log.e("checksum ", " cancel call back respon  " );
    }

    @Override
    public void onTransactionCancel(String s, Bundle bundle) {
        Log.e("checksum ", "  transaction cancel " );
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
