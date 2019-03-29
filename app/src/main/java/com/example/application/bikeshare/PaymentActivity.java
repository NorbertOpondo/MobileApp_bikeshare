package com.example.application.bikeshare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {
    private static String URL_SUBMIT = "http://192.168.100.24/android_api/book_bike.php";
    private static String URL_COST =   "http://192.168.100.24/android_api/cost_calculator.php";
    private TextView totalCost_ET, return_date_ET, start_date_ET, accessories_ET,my_bike_type;
    private Button submit_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        totalCost_ET = findViewById(R.id.total_cost_display);
        return_date_ET = findViewById(R.id.return_date_display);
        start_date_ET = findViewById(R.id.start_date_display);
        accessories_ET = findViewById(R.id.accessories_display);
        my_bike_type = findViewById(R.id.bike_type_display);
        submit_button = findViewById(R.id.submit);

        getCost();

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                book_bike();
            }
        });

        /*Log.d("cost","we "+bike_helmet_cost);
        Log.d("cost","we "+bike_lock_cost);
        Log.d("cost","we "+bike_flashlight_cost);
        Log.d("cost","we "+bike_pump_cost);
        Log.d("number_days ","duration "+number_of_days);

        for(int k = 0; k<my_accessories.length; k++ )

        Log.d("array","selected items are "+my_accessories[k]);*/

    }

    public void book_bike(){
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String my_student_id = sharedPreferences.getString("student_id","0");

        final String student_id = my_student_id;
        final String total_cost = this.totalCost_ET.getText().toString();
        final String start_date = this.start_date_ET.getText().toString();
        final String return_date = this.return_date_ET.getText().toString();
        final String accessories = this.accessories_ET.getText().toString();
        final String bike_type = this.my_bike_type.getText().toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SUBMIT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String no_bikes_left = jsonObject.optString("no_bikes_left");
                            String active = jsonObject.optString("active");
                            String booking = jsonObject.optString("booking");
                            String bike = jsonObject.optString("bikes");

                            if (bike.equals("true")){
                                if (active.equals("1")){
                                    Toast.makeText(PaymentActivity.this, "Only one bike per user is allowed", Toast.LENGTH_SHORT).show();
                                }
                                if (booking.equals("complete")){
                                    Toast.makeText(PaymentActivity.this, "Bike booking was successful", Toast.LENGTH_SHORT).show();
                                    Log.d("book success","book complete");
                                }
                            }else if (no_bikes_left.equals("NO BIKES ARE LEFT")){
                                Toast.makeText(PaymentActivity.this, "NO BIKES ARE LEFT", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PaymentActivity.this, "Booking error" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("student_id", student_id);
                params.put("total_cost", total_cost);
                params.put("start_date", start_date);
                params.put("return_date",return_date);
                params.put("accessories",accessories);
                params.put("bike_type",bike_type);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void getCost(){

        Intent i = getIntent();
        String bike_helmet_cost = i.getStringExtra("bike_helmet_cost");
        final String bike_lock_cost = i.getStringExtra("bike_lock_cost");
        final String bike_flashlight_cost = i.getStringExtra("bike_flashlight_cost");
        final String bike_pump_cost = i.getStringExtra("bike_pump_cost");
        final String bike_type = i.getStringExtra("bike_type");
        String number_of_days = i.getStringExtra("number_of_days");
        String my_start_date = i.getStringExtra("start_date");
        final ArrayList<String> my_accessories = (ArrayList<String>) getIntent().getSerializableExtra("accessories");
        //final String[] my_accessories = i.getStringArrayExtra("accessories");

        Log.d("cost","we "+bike_helmet_cost);
        Log.d("cost","we "+bike_lock_cost);
        Log.d("cost","we "+bike_flashlight_cost);
        Log.d("cost","we "+bike_pump_cost);
        Log.d("number_days ","duration "+number_of_days);


        start_date_ET.setText(my_start_date);
        my_bike_type.setText(bike_type);

        final String start_date = my_start_date;
        final String duration = number_of_days;
        final String bike_helmet_COST = bike_helmet_cost;
        final String bike_lock_COST = bike_lock_cost;
        final String bike_flashlight_COST = bike_flashlight_cost;
        final String bike_pump_COST = bike_pump_cost;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_COST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String my_total_cost = jsonObject.getString("total_cost");
                            String return_date = jsonObject.getString("return_date");
                            totalCost_ET.setText(my_total_cost);
                            return_date_ET.setText(return_date);
                            accessories_ET.setText((String.valueOf(my_accessories).replace("[","").replace("]","")));


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(PaymentActivity.this, "Cost Error" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PaymentActivity.this, "Calculation Error"+error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("start_date",start_date);
                params.put("duration", duration);
                params.put("bike_helmet_cost", bike_helmet_COST);
                params.put("bike_lock_cost", bike_lock_COST);
                params.put("bike_flashlight_cost", bike_flashlight_COST);
                params.put("bike_pump_cost", bike_pump_COST);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void submit(){
        final String school_id = "CIT-222-029/2015";
        final String amount_paid = "100";
        final String start_date = "14/3/2019";
        final String accessories = "Helmte,bike pump";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SUBMIT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
    }
}
