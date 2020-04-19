package com.example.moviezpoint.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.moviezpoint.Models.ApiInterface;
import com.example.moviezpoint.Models.GetAndSetBookedSeats;
import com.example.moviezpoint.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nex3z.togglebuttongroup.MultiSelectToggleGroup;
import com.nex3z.togglebuttongroup.button.CircularToggle;
import com.tuyenmonkey.mkloader.MKLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class BookingActivity extends AppCompatActivity {

    String cust_id;

    CircularToggle btnBooked,btnSelected,btnAvailable;

    MultiSelectToggleGroup groupFirstFour, groupSecondFour, groupThirdFour,groupFourthFour,groupFifthTwo,groupSixthTwo;

    String movieName, showDate, showTime, theatre;

    TextView textSeats, textAmount;

    Long movieId;

    DatabaseReference databaseReference,reference;

    String id;

    Button btnProceed;

    ScrollView scrollView;

    MKLoader loader;

    List<String> seatList,totalSeatsList,showSeatsList;
    List<Integer> seatsIdsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        seatList = new ArrayList<>();
        totalSeatsList = new ArrayList<>();
        seatsIdsList = new ArrayList<>();

        textSeats = findViewById(R.id.selectedSeat);
        textAmount = findViewById(R.id.amountShow);

        scrollView = findViewById(R.id.scrollView);
        loader = findViewById(R.id.loader);
        loader.setVisibility(View.VISIBLE);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        id = firebaseUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("MovieSeats");
        reference = FirebaseDatabase.getInstance().getReference().child("UserBookingData");
        btnProceed = findViewById(R.id.btnproceed_pay);

        cust_id = id;

        getIntentData();

        groupFirstFour = findViewById(R.id.group_first_five);
        groupSecondFour = findViewById(R.id.group_second_five);
        groupThirdFour = findViewById(R.id.group_third_five);
        groupFourthFour = findViewById(R.id.group_fourth_five);
        groupFifthTwo = findViewById(R.id.group_fifth_five);
        groupSixthTwo = findViewById(R.id.group_sixth_five);

        btnBooked = findViewById(R.id.booked_radio);
        btnSelected = findViewById(R.id.selected_radio);
        btnAvailable = findViewById(R.id.available_radio);

        btnAvailable.setClickable(false);
        setBooked(R.id.booked_radio);
        setSelect(btnSelected);

        getFirebaseSeatData();

        groupFirstFour.setOnCheckedChangeListener(new MultiSelectToggleGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedStateChanged(MultiSelectToggleGroup group, int checkedId, boolean isChecked) {
                onRadioClicked(checkedId);
                getPriceAndSeat();
            }
        });

        groupSecondFour.setOnCheckedChangeListener(new MultiSelectToggleGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedStateChanged(MultiSelectToggleGroup group, int checkedId, boolean isChecked) {
                onRadioClicked(checkedId);
                getPriceAndSeat();
            }
        });

        groupThirdFour.setOnCheckedChangeListener(new MultiSelectToggleGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedStateChanged(MultiSelectToggleGroup group, int checkedId, boolean isChecked) {
                onRadioClicked(checkedId);
                getPriceAndSeat();
            }
        });

        groupFourthFour.setOnCheckedChangeListener(new MultiSelectToggleGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedStateChanged(MultiSelectToggleGroup group, int checkedId, boolean isChecked) {
                onRadioClicked(checkedId);
                getPriceAndSeat();
            }
        });

        groupFifthTwo.setOnCheckedChangeListener(new MultiSelectToggleGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedStateChanged(MultiSelectToggleGroup group, int checkedId, boolean isChecked) {
                onRadioClicked(checkedId);
                getPriceAndSeat();
            }
        });

        groupSixthTwo.setOnCheckedChangeListener(new MultiSelectToggleGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedStateChanged(MultiSelectToggleGroup group, int checkedId, boolean isChecked) {
                onRadioClicked(checkedId);
                getPriceAndSeat();
            }
        });

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loader.setVisibility(View.VISIBLE);
                createOrderId();
            }
        });
    }

    private void getPriceAndSeat() {
        seatsIdsList.clear();

        seatsIdsList.addAll(groupFirstFour.getCheckedIds());
        seatsIdsList.addAll(groupSecondFour.getCheckedIds());
        seatsIdsList.addAll(groupThirdFour.getCheckedIds());
        seatsIdsList.addAll(groupFourthFour.getCheckedIds());
        seatsIdsList.addAll(groupFifthTwo.getCheckedIds());
        seatsIdsList.addAll(groupSixthTwo.getCheckedIds());

        showSeatsList = getSeatsToList(seatsIdsList);
        showSeatsList.removeAll(totalSeatsList);
        String text="";
        int num;
        if (showSeatsList.isEmpty()) {
            textSeats.setText("");
            textAmount.setText("");
        }
        else {
            for (num=0;num<showSeatsList.size();num++) {
                if (num==showSeatsList.size()-1) {
                    text = text+showSeatsList.get(num);
                }
                else {
                    text = text+showSeatsList.get(num)+", ";
                }
            }
            textSeats.setText(text);
            int amount = showSeatsList.size()*100;
            textAmount.setText(String.valueOf(amount));
        }
    }

    private List getSeatsToList(List<Integer> list) {
        List<String> stringList;
        stringList = new ArrayList<>();
        int i;
        if (list.isEmpty()) {
            Toast.makeText(this, "List Empty", Toast.LENGTH_SHORT).show();
        }
        else {
            for (i=0;i<list.size();i++) {
                CircularToggle button = findViewById(list.get(i));
                String text = button.getText().toString();
                stringList.add(text);
            }
        }
        return stringList;
    }

    private void getTransactionAmount(int size,String order_id) {
        int amount = size*100;
        String txn_amount = String.valueOf(amount);
        if (amount == 0) {
            Toast.makeText(this, "Please select your seats", Toast.LENGTH_SHORT).show();
        }
        else {
            Intent intent = new Intent(BookingActivity.this,PaymentActivity.class);
            intent.putExtra("amt",txn_amount);
            intent.putExtra("orderId",order_id);
            intent.putExtra("custId",cust_id);
            intent.putStringArrayListExtra("seatList",(ArrayList<String>)seatList);
            intent.putStringArrayListExtra("totalSeats",(ArrayList<String>)totalSeatsList);
            intent.putExtra("movieId",movieId);
            intent.putExtra("movieName",movieName);
            intent.putExtra("showDate",showDate);
            intent.putExtra("showTime",showTime);
            intent.putExtra("theatre",theatre);
            startActivity(intent);
            finish();
        }
        loader.setVisibility(View.GONE);

    }

    private void getIntentData() {
        Intent intent = getIntent();
        movieId = intent.getLongExtra("movie_id",0);
        movieName = intent.getStringExtra("movie_name");
        showDate = intent.getStringExtra("show_date");
        showTime = intent.getStringExtra("show_time");
        theatre = intent.getStringExtra("theatre");
    }

    public void setBooked(int id) {
        CircularToggle button = findViewById(id);
        button.setChecked(true);
        button.setBackground(getResources().getDrawable(R.drawable.ic_seats_booked));
        button.setClickable(false);
    }

    public void setSelect(CircularToggle button) {
        button.setChecked(true);
        button.setBackground(getResources().getDrawable(R.drawable.ic_seats_selected));
        button.setClickable(false);
    }

    public void onRadioClicked(int id) {
        CircularToggle button = findViewById(id);
        if (!button.isChecked()) {
            if (button.isClickable()) {
                button.setBackground(getResources().getDrawable(R.drawable.ic_seat));
                button.setChecked(false);
            }
        }
        else {
            if (button.isClickable()) {
                button.setChecked(true);
                button.setBackground(getResources().getDrawable(R.drawable.ic_seats_selected));
            }
        }
    }

    public void getFirebaseSeatData() {

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loader.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                if (dataSnapshot.hasChild(String.valueOf(movieId))) {
                    if (dataSnapshot.child(String.valueOf(movieId)).hasChild(theatre)) {
                        if (dataSnapshot.child(String.valueOf(movieId)).child(theatre).hasChild(showDate)) {
                            if (dataSnapshot.child(String.valueOf(movieId)).child(theatre).child(showDate).hasChild(showTime)) {
                                databaseReference.child(String.valueOf(movieId)).child(theatre).child(showDate).child(showTime).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        GetAndSetBookedSeats getBookedSeats = dataSnapshot.getValue(GetAndSetBookedSeats.class);

                                        if (!getBookedSeats.isSeat1()) {
                                            setBooked(R.id.one);
                                        }
                                        if (!getBookedSeats.isSeat2()) {
                                            setBooked(R.id.two);
                                        }
                                        if (!getBookedSeats.isSeat3()) {
                                            setBooked(R.id.three);
                                        }
                                        if (!getBookedSeats.isSeat4()) {
                                            setBooked(R.id.four);
                                        }
                                        if (!getBookedSeats.isSeat5()) {
                                            setBooked(R.id.five);
                                        }
                                        if (!getBookedSeats.isSeat6()) {
                                            setBooked(R.id.six);
                                        }
                                        if (!getBookedSeats.isSeat7()) {
                                            setBooked(R.id.seven);
                                        }
                                        if (!getBookedSeats.isSeat8()) {
                                            setBooked(R.id.eight);
                                        }
                                        if (!getBookedSeats.isSeat9()) {
                                            setBooked(R.id.nine);
                                        }
                                        if (!getBookedSeats.isSeat10()) {
                                            setBooked(R.id.ten);
                                        }
                                        if (!getBookedSeats.isSeat11()) {
                                            setBooked(R.id.eleven);
                                        }
                                        if (!getBookedSeats.isSeat12()) {
                                            setBooked(R.id.twelve);
                                        }
                                        if (!getBookedSeats.isSeat13()) {
                                            setBooked(R.id.thirteen);
                                        }
                                        if (!getBookedSeats.isSeat14()) {
                                            setBooked(R.id.fourteen);
                                        }
                                        if (!getBookedSeats.isSeat15()) {
                                            setBooked(R.id.fifteen);
                                        }
                                        if (!getBookedSeats.isSeat16()) {
                                            setBooked(R.id.sixteen);
                                        }
                                        if (!getBookedSeats.isSeat17()) {
                                            setBooked(R.id.seventeen);
                                        }
                                        if (!getBookedSeats.isSeat18()) {
                                            setBooked(R.id.eighteen);
                                        }
                                        if (!getBookedSeats.isSeat19()) {
                                            setBooked(R.id.nineteen);
                                        }
                                        if (!getBookedSeats.isSeat20()) {
                                            setBooked(R.id.twenty);
                                        }
                                        getSeatsIdsToList();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(BookingActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                return;
                            }
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                }
                else {
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BookingActivity.this, "Gone Wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getSeatsIdsToList() {
        seatsIdsList.addAll(groupFirstFour.getCheckedIds());
        seatsIdsList.addAll(groupSecondFour.getCheckedIds());
        seatsIdsList.addAll(groupThirdFour.getCheckedIds());
        seatsIdsList.addAll(groupFourthFour.getCheckedIds());
        seatsIdsList.addAll(groupFifthTwo.getCheckedIds());
        seatsIdsList.addAll(groupSixthTwo.getCheckedIds());

        totalSeatsList = getSeatsToList(seatsIdsList);
    }

    private void createOrderId() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String uid = user.getUid();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
        String currentTime = new SimpleDateFormat("HHmmss", Locale.getDefault()).format(new Date());
        String formattedDate = df.format(c.getTime());
        String orderId = uid+formattedDate+currentTime;
        getseatsSelected(orderId);
    }

    private void getseatsSelected(String orderId) {
        seatsIdsList.clear();

        seatsIdsList.addAll(groupFirstFour.getCheckedIds());
        seatsIdsList.addAll(groupSecondFour.getCheckedIds());
        seatsIdsList.addAll(groupThirdFour.getCheckedIds());
        seatsIdsList.addAll(groupFourthFour.getCheckedIds());
        seatsIdsList.addAll(groupFifthTwo.getCheckedIds());
        seatsIdsList.addAll(groupSixthTwo.getCheckedIds());

        seatList = getSeatsToList(seatsIdsList);
        seatList.removeAll(totalSeatsList);
        totalSeatsList.addAll(seatList);

        getTransactionAmount(seatList.size(),orderId);
    }

    public void imageBack(View view) {
        onBackPressed();
    }

}
