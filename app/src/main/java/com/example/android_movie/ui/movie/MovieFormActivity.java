package com.example.android_movie.ui.movie;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.example.android_movie.ui.SessionClass;
import com.example.android_movie.ui.genre.Genre;
import com.example.android_movie.ui.producer.Producer;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MovieFormActivity extends AppCompatActivity {

    Spinner sprGenre, sprProd;
    EditText tboxTitle, tboxDescription, tboxRelease;
    Button btnSelectImgM, btnSaveMovie;
    String id, title, release, description;
    Integer genre_id, producer_id;
    ImageView imgMovieSelect;
    private static final int REQUEST_IMAGE = 2;
    Bitmap img;
    ArrayList<Genre> genreArrayList = new ArrayList<>();
    ArrayList<Producer> producerArrayList = new ArrayList<>();
    ArrayList<String> stringGenre = new ArrayList<>();
    ArrayList<String> stringProducer = new ArrayList<>();
    ArrayAdapter<String> stringAdapterGenre;
    ArrayAdapter<String> stringAdapterProducer; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_form);

        tboxTitle = findViewById(R.id.tboxTitle);
        tboxDescription = findViewById(R.id.tboxDescription);
        tboxRelease = findViewById(R.id.tboxRelease);
        sprGenre = findViewById(R.id.sprGenre);
        sprProd = findViewById(R.id.sprProd);
        imgMovieSelect = findViewById(R.id.imgMovieSelect);
        btnSelectImgM = findViewById(R.id.btnSelectImgM);
        btnSaveMovie = findViewById(R.id.btnSaveMovie);

        if(getIntent().hasExtra("id")){
            id = getIntent().getStringExtra("id");
            title = getIntent().getStringExtra("title");
            release = getIntent().getStringExtra("release");
            description = getIntent().getStringExtra("description");

            tboxTitle.setText(title);
            tboxRelease.setText(release);
            tboxDescription.setText(description);
        }

        sprGenre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                genre_id = Integer.parseInt(genreArrayList.get(position).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                genre_id = Integer.parseInt(genreArrayList.get(0).getId());
            }
        });

        sprProd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                producer_id = Integer.parseInt(producerArrayList.get(position).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                producer_id = Integer.parseInt(producerArrayList.get(0).getId());
            }
        });

        btnSaveMovie.setOnClickListener(v -> {
            storeMovieData();
        });

        btnSelectImgM.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"imgpath"),REQUEST_IMAGE);
        });

        getMProducers();
        getMGenres();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SessionClass.FRAGMENT = new MovieFragment();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE && resultCode == RESULT_OK && data != null && data.getData() !=null){
            Uri imageMovies = data.getData();
            try {
                img = MediaStore.Images.Media.getBitmap(getContentResolver(),imageMovies);
                Picasso.get().load(imageMovies).into(imgMovieSelect);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String imgToString(Bitmap imgs){
        ByteArrayOutputStream arrayMovies = new ByteArrayOutputStream();
        imgs.compress(Bitmap.CompressFormat.JPEG,100,arrayMovies);
        byte[] byteMovies = arrayMovies.toByteArray();
        String imgpath = Base64.encodeToString(byteMovies, Base64.DEFAULT);

        return imgpath;
    }

    private void getMGenres(){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                getString(R.string.URLProject) + "genre",
                null,
                (Response.Listener<JSONObject>) response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("genres");
                        for (int x = 0; x < jsonArray.length(); x++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(x);
                            Genre genre = new Genre(
                                    jsonObject.getString("name"),
                                    String.valueOf(jsonObject.getInt("id"))
                            );
                            genreArrayList.add(genre);
                            stringGenre.add(genre.getName());
                        }
                        stringAdapterGenre = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, stringGenre);
                        stringAdapterGenre.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sprGenre.setAdapter(stringAdapterGenre);
                    } catch (JSONException e) {
                        Log.e("ERROR", e.toString());
                    }

                },
                error -> Log.e("volleyError", error.toString()))
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

    private void getMProducers(){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                getString(R.string.URLProject) + "producer",
                null,
                response -> {
                    try{
                        JSONArray jsonArray = response.getJSONArray("producers");
                        for (int x = 0; x < jsonArray.length(); x++){
                            JSONObject jsonObject = jsonArray.getJSONObject(x);
                            Producer producer = new Producer(
                                    jsonObject.getString("name"),
                                    jsonObject.getString("email"),
                                    String.valueOf(jsonObject.getInt("id"))
                            );
                            producerArrayList.add(producer);
                            stringProducer.add(producer.getName());
                        }
                        stringAdapterProducer = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, stringProducer);
                        stringAdapterProducer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sprProd.setAdapter(stringAdapterProducer);
                    } catch (JSONException e){
                        Log.e("ERROR", e.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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

    private void storeMovieData(){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("title" , tboxTitle.getText().toString());
            jsonObject.put("description" , tboxDescription.getText().toString());
            jsonObject.put("release" , tboxRelease.getText().toString());
            jsonObject.put("producer_id" , producer_id);
            jsonObject.put("genre_id" , genre_id);
            jsonObject.put("imgpath" , imgToString(img));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = getString(R.string.URLProject) + "movie";
        if(id != null){
            url += "/"+ id;
        }
        // (statement) ? true:false
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                (id != null) ? Request.Method.PUT:Request.Method.POST,
                url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(MovieFormActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            SessionClass.FRAGMENT = new MovieFragment();
                            Intent intent = new Intent(MovieFormActivity.this , MainActivity.class);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("volleyError", error.toString());
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