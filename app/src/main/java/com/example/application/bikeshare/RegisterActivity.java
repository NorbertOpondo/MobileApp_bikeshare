package com.example.application.bikeshare;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class RegisterActivity extends AppCompatActivity {

    private EditText first_nameEt, second_nameEt, schooIdEt, emailEt, passwordEt, c_passwordEt;
    private Button reg_button;
    private static String URL_REGIST = "https://kenchaadventures.com/android_api/register.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        handleSSLHandshake();
        first_nameEt = findViewById(R.id.firstName);
        second_nameEt = findViewById(R.id.secondName);
        schooIdEt = findViewById(R.id.schoolID);
        emailEt = findViewById(R.id.email);
        passwordEt = findViewById(R.id.password);
        c_passwordEt = findViewById(R.id.confirm_password);
        reg_button = findViewById(R.id.registerButton);


        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regist();
            }
        });
    }

    private void regist() {
        final String firstName = this.first_nameEt.getText().toString().trim();
        final String secondName = this.second_nameEt.getText().toString().trim();
        final String schoolId = this.schooIdEt.getText().toString().trim();
        final String email = this.emailEt.getText().toString().trim();
        final String password = this.passwordEt.getText().toString().trim();

        if (firstName.isEmpty() || secondName.isEmpty() || schoolId.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "All fields must be provided", Toast.LENGTH_SHORT).show();
        } else {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGIST,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String status = jsonObject.getString("status");

                                if (status.equals("success")) {
                                    Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_LONG).show();
                                    Intent loginActivity = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(loginActivity);
                                }
                                if (status.equals("THIS STUDENT ID HAS ALREADY BEEN USED")) {
                                    Toast.makeText(RegisterActivity.this, "This School Id has already been used", Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(RegisterActivity.this, "Registration error" + e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(RegisterActivity.this, "Registration error" + error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("firstName", firstName);
                    params.put("secondName", secondName);
                    params.put("schoolId", schoolId);
                    params.put("email", email);
                    params.put("password", password);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }

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

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

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
