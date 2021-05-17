package com.example.android_movie.ui.genre;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android_movie.MainActivity;
import com.example.android_movie.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GenreFormActivity extends AppCompatActivity {

    EditText tboxGenreName;
    Button btnSaveGenre;
    String id, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre_form);

        tboxGenreName = findViewById(R.id.tboxGenreName);
        btnSaveGenre = findViewById(R.id.btnSaveGenre);

        if(getIntent().hasExtra("id")) {
            id = getIntent().getStringExtra("id");
            name = getIntent().getStringExtra("name");
            tboxGenreName.setText(name);
        }
        btnSaveGenre.setOnClickListener(v -> storeGenreData());
    }

    private void storeGenreData() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", tboxGenreName.getText().toString());
        }catch (JSONException e){
            e.printStackTrace();
        }

        String url = getString(R.string.URLProject ) + "genre";
        if(id != null) {
            url += "/" + id;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                (id != null) ? Request.Method.PUT:Request.Method.POST,
                url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(GenreFormActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(GenreFormActivity.this, GenreFragment.class);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
