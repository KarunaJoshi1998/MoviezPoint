package com.example.moviezpoint.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moviezpoint.Models.CustomListAdapter;
import com.example.moviezpoint.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TheatreActivity extends AppCompatActivity {

    ListView listTheatres;
    BottomSheetBehavior behavior;
    Button select,dateOK;
    RadioGroup groupDate;
    RadioButton day1, day2, day3;
    int daySelected;
    TextView textViewDate;
    String textShowTime="",theatre;

    String[] halls = {"Inox","PVR"};
    String[] locations = {"Latur","Latur"};
    int[] image_logo = {R.drawable.inox,R.drawable.pvr};

    RadioGroup radioGroup1, radioGroup2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theatre);

        listTheatres = findViewById(R.id.listhalls);
        select = findViewById(R.id.btnSelect);

        View view = findViewById(R.id.relativeLayout_bottomsheet);
        behavior = BottomSheetBehavior.from(view);

        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        CustomListAdapter adapter = new CustomListAdapter(this,halls,locations,image_logo);
        listTheatres.setAdapter(adapter);

        listTheatres.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                theatre = halls[position];
                showDialogForDates();
            }
        });

    }

    private void showDialogForDates() {
        ViewGroup viewGroup = (ViewGroup)findViewById(android.R.id.content);

        View dialogView = LayoutInflater.from(TheatreActivity.this).inflate(R.layout.custom_date_selection_dialog_box,viewGroup,false);

        dateOK = dialogView.findViewById(R.id.btnOKDate);
        groupDate = dialogView.findViewById(R.id.dateSelectGroup);
        day1 = dialogView.findViewById(R.id.day1);
        day2 = dialogView.findViewById(R.id.day2);
        day3 = dialogView.findViewById(R.id.day3);
        textViewDate = dialogView.findViewById(R.id.selectDateTitle);

        AlertDialog.Builder builder = new AlertDialog.Builder(TheatreActivity.this);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        getDatesToRadioButtons();
        day1.setChecked(true);
        day1.setTextColor(getResources().getColor(android.R.color.white));
        day1.setBackground(getResources().getDrawable(R.drawable.radio_selected_background));
        dialog.show();

        getSelectedDate();

        dateOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dateText;
                int finalDate = getSelectedDate();
                if (finalDate == 0) {
                    dateText = day1.getText().toString();
                }
                else if (finalDate == 1) {
                    dateText = day2.getText().toString();
                }
                else if (finalDate == 2) {
                    dateText = day3.getText().toString();
                }
                else {
                    dateText = "Not Selected";
                }
                dialog.hide();
                showBottomSheet(dateText);
            }
        });
    }

    private void getDatesToRadioButtons() {

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String date1 = dateFormat.format(today);
        day1.setText(date1);

        calendar.add(Calendar.DATE,1);
        Date dateday2 = calendar.getTime();
        DateFormat dateFormat2 = new SimpleDateFormat("dd-MM-yyyy");
        String date2 = dateFormat2.format(dateday2);
        day2.setText(date2);

        calendar.add(Calendar.DATE,1);
        Date dateday3 = calendar.getTime();
        DateFormat dateFormat3 = new SimpleDateFormat("dd-MM-yyyy");
        String date3 = dateFormat3.format(dateday3);
        day3.setText(date3);
    }

    public int getSelectedDate() {
        groupDate.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.day1 :
                        day1.setTextColor(getResources().getColor(android.R.color.white));
                        day1.setBackground(getResources().getDrawable(R.drawable.radio_selected_background));
                        clearPreviousSelectedDate(day2);
                        clearPreviousSelectedDate(day3);
                        daySelected = 0;
                        break;

                    case R.id.day2 :
                        day2.setTextColor(getResources().getColor(android.R.color.white));
                        day2.setBackground(getResources().getDrawable(R.drawable.radio_selected_background));
                        clearPreviousSelectedDate(day1);
                        clearPreviousSelectedDate(day3);
                        daySelected = 1;
                        break;

                    case R.id.day3 :
                        day3.setTextColor(getResources().getColor(android.R.color.white));
                        day3.setBackground(getResources().getDrawable(R.drawable.radio_selected_background));
                        clearPreviousSelectedDate(day1);
                        clearPreviousSelectedDate(day2);
                        daySelected = 2;
                        break;
                }
            }
        });

        return daySelected;
    }

    public void clearPreviousSelectedDate(RadioButton button) {
        button.setBackground(getResources().getDrawable(R.drawable.radio_background));
        button.setTextColor(getResources().getColor(android.R.color.black));
    }

    public void clearBackground(int id1, int id2) {
        RadioButton button1 = findViewById(id1);
        RadioButton button2 = findViewById(id2);

        button1.setBackground(getResources().getDrawable(R.drawable.radio_background));
        button2.setBackground(getResources().getDrawable(R.drawable.radio_background));
        button1.setTextColor(getResources().getColor(android.R.color.black));
        button2.setTextColor(getResources().getColor(android.R.color.black));
    }

    public void showBottomSheet(final String finalSelectedDate) {

        Intent intent = getIntent();
        final Long movieID = intent.getLongExtra("movie_id",0);
        final String movieName = intent.getStringExtra("movie_name");
        if (behavior.getState()!=BottomSheetBehavior.STATE_EXPANDED) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            radioGroup1 = findViewById(R.id.group_choices);
            radioGroup2 = findViewById(R.id.group_choices2);

            radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                    RadioButton button1 = findViewById(R.id.nine);
                    RadioButton button2 = findViewById(R.id.twelve);

                    switch (checkedId) {
                        case R.id.nine :
                            radioGroup2.clearCheck();
                            radioGroup1.setSelected(true);
                            radioGroup2.setSelected(false);
                            clearBackground(R.id.three,R.id.six);
                            textShowTime = button1.getText().toString();

                            button2.setBackground(getResources().getDrawable(R.drawable.radio_background));
                            button2.setTextColor(getResources().getColor(android.R.color.black));
                            button2.setChecked(false);

                            button1.setBackground(getResources().getDrawable(R.drawable.radio_selected_background));
                            button1.setTextColor(getResources().getColor(android.R.color.white));
                            break;

                        case R.id.twelve :
                            radioGroup2.clearCheck();
                            radioGroup1.setSelected(true);
                            radioGroup2.setSelected(false);
                            clearBackground(R.id.three,R.id.six);
                            textShowTime = button2.getText().toString();

                            button1.setBackground(getResources().getDrawable(R.drawable.radio_background));
                            button1.setTextColor(getResources().getColor(android.R.color.black));
                            button1.setChecked(false);

                            button2.setBackground(getResources().getDrawable(R.drawable.radio_selected_background));
                            button2.setTextColor(getResources().getColor(android.R.color.white));
                            break;
                    }
                }
            });

            radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                    RadioButton button1 = findViewById(R.id.three);
                    RadioButton button2 = findViewById(R.id.six);

                    switch (checkedId) {
                        case R.id.three :
                            radioGroup1.clearCheck();
                            radioGroup1.setSelected(false);
                            radioGroup2.setSelected(true);
                            textShowTime = button1.getText().toString();
                            clearBackground(R.id.nine,R.id.twelve);
                            button2.setBackground(getResources().getDrawable(R.drawable.radio_background));
                            button2.setTextColor(getResources().getColor(android.R.color.black));
                            button2.setChecked(false);
                            button1.setBackground(getResources().getDrawable(R.drawable.radio_selected_background));
                            button1.setTextColor(getResources().getColor(android.R.color.white));

                            break;

                        case R.id.six :
                            radioGroup1.clearCheck();
                            radioGroup1.setSelected(false);
                            radioGroup2.setSelected(true);
                            clearBackground(R.id.nine,R.id.twelve);
                            button2.setBackground(getResources().getDrawable(R.drawable.radio_selected_background));
                            button2.setTextColor(getResources().getColor(android.R.color.white));
                            button1.setChecked(false);
                            textShowTime = button2.getText().toString();
                            button1.setBackground(getResources().getDrawable(R.drawable.radio_background));
                            button1.setTextColor(getResources().getColor(android.R.color.black));

                            break;
                    }
                }
            });

        }
        else {
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                radioGroup1.clearCheck();
                radioGroup2.clearCheck();
                clearBackground(R.id.three,R.id.six);
                clearBackground(R.id.nine,R.id.twelve);
                radioGroup1.setSelected(false);
                radioGroup2.setSelected(false);
                behavior.setState(BottomSheetBehavior.STATE_HIDDEN);

                if(!textShowTime.equals("")) {
                    Intent intent = new Intent(TheatreActivity.this,BookingActivity.class);
                    intent.putExtra("movie_id",movieID);
                    intent.putExtra("movie_name",movieName);
                    intent.putExtra("show_date",finalSelectedDate);
                    intent.putExtra("show_time",textShowTime);
                    intent.putExtra("theatre",theatre);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(TheatreActivity.this, "Please Select Time", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void imageBack(View view) {
        onBackPressed();
    }
}

