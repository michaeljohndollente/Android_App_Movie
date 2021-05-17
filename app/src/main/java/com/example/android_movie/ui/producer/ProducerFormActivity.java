package com.example.android_movie.ui.producer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android_movie.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProducerFormActivity extends AppCompatActivity {

    EditText tboxProdName, tboxEmail;
    Button btnSaveProd;
    String id, name, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producer_form);

        tboxProdName = findViewById(R.id.tboxProdName);
        tboxEmail = findViewById(R.id.tboxEmail);
        btnSaveProd = findViewById(R.id.btnSaveProd);

        if(getIntent().hasExtra("id")) {
            name = getIntent().getStringExtra("name");
            email = getIntent().getStringExtra("email");
            id = getIntent().getStringExtra("id");
            tboxProdName.setText(name);
            tboxEmail.setText(email);
        }
        btnSaveProd.setOnClickListener(v -> storeProdData());
    }

    private void storeProdData() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", tboxProdName.getText().toString());
            jsonObject.put("email", tboxEmail.getText().toString());
        }catch (JSONException e){
            e.printStackTrace();
        }

        String url = getString(R.string.URLProject ) + "producer";
        if(id != null) {
            url += "/" + id;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                (id != null) ? Request.Method.PUT:Request.Method.POST,
                url,
                jsonObject,
                response -> {
                    try {
                        Toast.makeText(ProducerFormActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ProducerFormActivity.this, ProducerFragment.class);
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Log.e("VolleyError", error.toString());
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(
                        "loginCredential", Context.MODE_PRIVATE
                );
                headers.put("Authorization", "Bearer "+ sharedPreferences.getString("access_token", null));
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
}