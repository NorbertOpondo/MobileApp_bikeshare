package com.example.application.bikeshare;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class LoginActivity extends AppCompatActivity {

    /**declare the widgets**/
    private EditText schoolIdET, passwordET;
    private Button login;
    private Button register;
    private ProgressBar loading;
    private static String URL_LOGIN = "https://kenchaadventures.com/android_api/process_user_login.php";
    public static  String MyPREFERENCES = "MyPrefs";
    public String student_id;
    public String first_name;
    public String bike_status;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        handleSSLHandshake();

        /**find the widgets by id**/
        loading = findViewById(R.id.loading);
        schoolIdET = findViewById(R.id.regNo);
        passwordET = findViewById(R.id.password);
        login = findViewById(R.id.loginButton);
        register = findViewById(R.id.registrationLink);

        sharedpreferences  = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        /**registration button listener**/
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });


        /**Login button listener**/
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String schoolId = schoolIdET.getText().toString().trim();
                String password = passwordET.getText().toString().trim();

                if (schoolId.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else {
                    /**call login function if all is okay**/
                    login(schoolId, password);
                }
            }
        });
    }

    private void login(final String schoolId, final String password) {
        loading.setVisibility(View.VISIBLE);
        login.setVisibility(View.GONE);
        /** Initialise String request for JSON responses**/
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("login");

                            if (success.equals("1")) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    student_id = object.getString("student_id").trim();
                                    first_name = object.getString("first_name").trim();
                                    bike_status = object.getString("bike_status");

                                    Toast.makeText(LoginActivity.this, "Login success", Toast.LENGTH_SHORT).show();

                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString("student_id", student_id);
                                    editor.putString("bike_status",bike_status);
                                    editor.commit();

                                    Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);
                                   /*mainActivity.putExtra("student_id",student_id);
                                    mainActivity.putExtra("first_name",first_name);*/
                                    startActivity(mainActivity);


                                    loading.setVisibility(View.GONE);
                                    login.setVisibility(View.VISIBLE);
                                }
                            }
                            else if(success.equals("0")) {
                                Toast.makeText(LoginActivity.this, "Wrong SchoolId or password",Toast.LENGTH_LONG).show();
                                loading.setVisibility(View.GONE);
                                login.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Error " + e.toString(), Toast.LENGTH_SHORT).show();
                            //Toast.makeText(LoginActivity.this,"An error occurred",Toast.LENGTH_LONG).show();
                            loading.setVisibility(View.GONE);
                            login.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, "Error " + error.toString(), Toast.LENGTH_SHORT).show();
                        //Toast.makeText(LoginActivity.this, "Network connection not available", Toast.LENGTH_LONG).show();
                        loading.setVisibility(View.GONE);
                        login.setVisibility(View.VISIBLE);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("schoolId", schoolId);
                params.put("password", password);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    /**
     * Enables https connections
     */
    @SuppressLint("TrulyRandom")
    public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @SuppressLint("TrustAllX509TrustManager")
                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @SuppressLint("TrustAllX509TrustManager")
                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }
    }
}