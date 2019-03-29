package com.example.application.bikeshare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Dashboard.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Dashboard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Dashboard extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String std_id;
    private TextView std_idTextView, totalBikesTextView, remainingBikesTextView, bikeStatusTextView;
    private TextView bmx_type, bmx_gear, bmx_transmission, mountain_gear, mountain_transmission;
    private TextView mountain_type, road_type,road_transmission,road_gear;
    private Button rent_bmx, rent_mountain_bike, rent_roadBike;
    private static String URL_get_bike_count = "https://kenchaadventures.com/android_api/total_bikes.php";
    RequestQueue req;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    SharedPreferences sharedPreferences;


    private OnFragmentInteractionListener mListener;

    public Dashboard() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Dashboard.
     */
    // TODO: Rename and change types and number of parameters
    public static Dashboard newInstance(String param1, String param2) {
        Dashboard fragment = new Dashboard();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        //initialise the widgets
        std_idTextView = rootView.findViewById(R.id.loggedInUser);
        rent_bmx = rootView.findViewById(R.id.rentBMX);
        rent_mountain_bike = rootView.findViewById(R.id.rent_mountain_bike);
        rent_roadBike = rootView.findViewById(R.id.rentRoadBike);

        totalBikesTextView = rootView.findViewById(R.id.totalBikes);
        remainingBikesTextView = rootView.findViewById(R.id.remaining_bikes);
        bikeStatusTextView = rootView.findViewById(R.id.bikeStatus);
        req = Volley.newRequestQueue(this.getActivity());
        bmx_type = rootView.findViewById(R.id.bmx_type);
        bmx_gear = rootView.findViewById(R.id.bmx_gear);
        bmx_transmission = rootView.findViewById(R.id.bmx_transmission_type);

        mountain_gear = rootView.findViewById(R.id.mountain_gear);
        mountain_transmission = rootView.findViewById(R.id.mountain_transmission_type);
        mountain_type = rootView.findViewById(R.id.mountain_type);

        road_type = rootView.findViewById(R.id.road_bike);
        road_transmission = rootView.findViewById(R.id.road_bike_transmission_type);
        road_gear = rootView.findViewById(R.id.road_bike_gear);

        //set the session student id in the text view
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String logged_in_user = sharedPreferences.getString("student_id", "0");
        String bike_status = sharedPreferences.getString("bike_status", "0");
        if (logged_in_user != null) {
            std_idTextView.setText(logged_in_user);
        }

        if (bike_status != null) {
            if (bike_status == "1") {
                bikeStatusTextView.setText("Bike Status : 1 bike rented");
            } else {
                bikeStatusTextView.setText("Bike status : 0 bikes rented");
            }
        }

        rent_bmx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bikeDetails = new Intent(getActivity(), BikeDetailsActivity.class);
                bikeDetails.putExtra("bike_type", bmx_type.getText().toString());
                bikeDetails.putExtra("bike_gear", bmx_gear.getText().toString());
                bikeDetails.putExtra("bike_transmission", bmx_transmission.getText().toString());

                startActivity(bikeDetails);
            }
        });

        rent_mountain_bike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bikeDetails = new Intent(getActivity(), BikeDetailsActivity.class);
                bikeDetails.putExtra("bike_type", mountain_type.getText().toString());
                bikeDetails.putExtra("bike_gear", mountain_gear.getText().toString());
                bikeDetails.putExtra("bike_transmission",mountain_transmission.getText().toString());
                startActivity(bikeDetails);
            }
        });

        rent_roadBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bikeDetails = new Intent(getActivity(), BikeDetailsActivity.class);
                bikeDetails.putExtra("bike_type",road_type.getText().toString());
                bikeDetails.putExtra("bike_gear",road_gear.getText().toString());
                bikeDetails.putExtra("bike_transmission",road_transmission.getText().toString());
                startActivity(bikeDetails);

            }
        });

        getBikeCount();

        /*
        //unpack data from bundle
        if (getArguments() != null) {
            String logged_in_user = this.getArguments().getString("student_id");

            //set studentId in text view
            std_idTextView.setText(logged_in_user);
        }
        // Inflate the layout for this fragment
        */
        return rootView;

    }

    public void getBikeCount() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL_get_bike_count, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String totalBikes = response.getString("total bikes");
                    String remaining_bikes = response.getString("bikes available");
                    totalBikesTextView.setText(totalBikes);
                    remainingBikesTextView.setText(remaining_bikes);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        req.add(jsonObjectRequest);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
