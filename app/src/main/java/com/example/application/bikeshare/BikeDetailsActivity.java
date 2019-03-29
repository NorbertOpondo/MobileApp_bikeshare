package com.example.application.bikeshare;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BikeDetailsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextView mDisplayDate, total_amount, h_cost;
    private TextView bike_type, gear_type, gear_description;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private Spinner spinner;
    private Button next_button;
    private CheckBox select_helmet, select_pump, select_light, select_lock;
    private String bike_helmet_cost, bike_pump_cost, bike_flashlight_cost, bike_lock_cost,number_of_days;
    private String startDate;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_details);

        bike_type = findViewById(R.id.bike_type);
        gear_type = findViewById(R.id.gear_type);
        gear_description = findViewById(R.id.gear_description);
        total_amount = findViewById(R.id.total_amount);

        select_helmet = findViewById(R.id.select_helmet);
        select_pump = findViewById(R.id.select_pump);
        select_light = findViewById(R.id.select_light);
        select_lock = findViewById(R.id.select_lock);

        final List<String> accessories = new ArrayList<>();

        next_button = findViewById(R.id.Next_button);

        bike_helmet_cost = bike_lock_cost = bike_pump_cost = bike_flashlight_cost = "0";

        mDisplayDate = findViewById(R.id.select_from_date);
        startDate = "";
        spinner = findViewById(R.id.select_days_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.number_of_days_picker, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        receive_data();
        datePicker();

        select_helmet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (select_helmet.isChecked()) {
                    bike_helmet_cost = "20";
                    Toast.makeText(BikeDetailsActivity.this, "Helmet selected", Toast.LENGTH_SHORT).show();
                    accessories.add("helmet");
                } else {
                    accessories.remove("helmet");
                    bike_helmet_cost = "0";
                }
            }
        });

        select_lock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(select_lock.isChecked()){
                    bike_lock_cost = "15";
                    Toast.makeText(BikeDetailsActivity.this, "Lock selected", Toast.LENGTH_SHORT).show();
                    accessories.add("bikelock");
                }else{
                    accessories.remove("bikelock");
                    bike_lock_cost = "0";
                }
            }
        });


        select_pump.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (select_pump.isChecked()){
                    bike_pump_cost = "0";
                    Toast.makeText(BikeDetailsActivity.this, "Pump selected", Toast.LENGTH_SHORT).show();
                    accessories.add("Bikepump");
                }else{
                    accessories.remove("Bikepump");
                    bike_pump_cost = "0";
                }
            }
        });

        select_light.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (select_light.isChecked()){
                    bike_flashlight_cost = "15";
                    Toast.makeText(BikeDetailsActivity.this, "Flashlight selected", Toast.LENGTH_SHORT).show();
                    accessories.add("flashlight");
                }else {
                    accessories.remove("flashlight");
                    bike_flashlight_cost = "0";
                }
            }
        });


        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (startDate == ""){
                    Toast.makeText(BikeDetailsActivity.this, "You need to pick a start date to proceed", Toast.LENGTH_SHORT).show();

                }else{
                    Intent PaymentActivity = new Intent(BikeDetailsActivity.this, PaymentActivity.class);
                    PaymentActivity.putExtra("bike_helmet_cost", bike_helmet_cost);
                    PaymentActivity.putExtra("bike_flashlight_cost",bike_flashlight_cost);
                    PaymentActivity.putExtra("bike_pump_cost",bike_pump_cost);
                    PaymentActivity.putExtra("bike_lock_cost",bike_lock_cost);
                    PaymentActivity.putExtra("number_of_days",number_of_days);
                    PaymentActivity.putExtra("accessories", (Serializable) accessories);
                    PaymentActivity.putExtra("start_date",startDate);
                    PaymentActivity.putExtra("bike_type",bike_type.getText().toString());
                    startActivity(PaymentActivity);
                }

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        number_of_days = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void receive_data() {
        //RECEIVE DATA
        Intent i = getIntent();
        String i_bike_type = i.getStringExtra("bike_type");
        String i_gear_type = i.getStringExtra("bike_gear");
        String i_gear_description = i.getStringExtra("bike_transmission");


        if (i_bike_type.equals("BMX bike")) {
            //SET THE DATA IN TEXT VIEWS
            bike_type.setText("Bmx");
            gear_type.setText(i_gear_type);
            gear_description.setText(i_gear_description);
        } else if (i_bike_type.equals("Mountain Bike")) {
            bike_type.setText("Mountain bike");
            gear_type.setText("9 x 4 gear");
            gear_description.setText(i_gear_description);
        } else if(i_bike_type.equals("Road Bike")){
            bike_type.setText("Road Bike");
            gear_type.setText("6 x 3 gear");
            gear_description.setText(i_gear_description);
        }
    }

    public void datePicker() {
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        BikeDetailsActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int from_day) {
                month = month + 1;
                startDate = year + "-"+ month + "-" + from_day;
                mDisplayDate.setText(startDate);
            }
        };

    }
}