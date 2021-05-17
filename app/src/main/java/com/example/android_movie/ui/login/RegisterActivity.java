package com.example.android_movie.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android_movie.MainActivity;
import com.example.android_movie.R;
import com.example.android_movie.ui.SessionClass;
import com.example.android_movie.ui.actor.ActorFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    EditText tboxUserName, tboxUEmail, tboxUPassword;
    Button btnSaveRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tboxUEmail = findViewById(R.id.tboxUEmail);
        tboxUserName = findViewById(R.id.tboxUserName);
        tboxUPassword = findViewById(R.id.tboxUPassword);
        btnSaveRegister = findViewById(R.id.btnSaveRegister);

        btnSaveRegister.setOnClickListener(v -> {
            storeRegister();
        });
    }

    private void storeRegister(){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", tboxUserName.getText().toString());
            jsonObject.put("email", tboxUEmail.getText().toString());
            jsonObject.put("password", tboxUPassword.getText().toString());
        }catch (JSONException e){
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
            Request.Method.POST,
            getString(R.string.URLAuth) + "register",
            jsonObject,
                response -> {
                    try {
                        Toast.makeText(RegisterActivity.this, "Register", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                    } catch (Error e){
                        e.printStackTrace();
                    }
                },
            (Response.ErrorListener) error -> {
                Log.e("VolleyError", error.toString());
            });
        requestQueue.add(jsonObjectRequest);
    }
}