package com.example.android_movie.ui.role;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.android_movie.ui.actor.Actor;
import com.example.android_movie.ui.movie.Movie;
import com.example.android_movie.ui.movie.MovieFormActivity;
import com.example.android_movie.ui.movie.MovieFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RoleFormActivity extends AppCompatActivity {

    Spinner sprMovieID, sprActorID;
    EditText tboxRoleName;
    Button btnSaveRole;
    String name, id;
    Integer actorID, movieID;
    ArrayList<Actor> actorArrayList = new ArrayList<>();
    ArrayList<Movie> movieArrayList = new ArrayList<>();
    ArrayList<String> stringActor = new ArrayList<>();
    ArrayList<String> stringMovie = new ArrayList<>();
    ArrayAdapter<String> stringAdapterActor;
    ArrayAdapter<String> stringAdapterMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_form);

        tboxRoleName = findViewById(R.id.tboxRoleName);
        btnSaveRole = findViewById(R.id.btnSaveRole);
        sprActorID = findViewById(R.id.sprActorID);
        sprMovieID = findViewById(R.id.sprMovieID);

        if(getIntent().hasExtra("id")){
            id = getIntent().getStringExtra("id");
            name = getIntent().getStringExtra("name");
            tboxRoleName.setText(name);
        }

        sprMovieID.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                movieID = Integer.parseInt(movieArrayList.get(position).getId());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                actorID = Integer.parseInt(actorArrayList.get(0).getId());
            }
        });

        sprActorID.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                actorID = Integer.parseInt(actorArrayList.get(position).getId());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                actorID = Integer.parseInt(actorArrayList.get(0).getId());
            }
        });

        btnSaveRole.setOnClickListener(v -> {
            storeRoleData();
        });
        getRActors();
        getRMovies();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SessionClass.FRAGMENT = new RoleFragment();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    private void getRActors(){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                getString(R.string.URLProject) + "actor",
                null,
                response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("actor");
                        for (int x = 0; x < jsonArray.length(); x++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(x);
                            Actor actor = new Actor(
                                    jsonObject.getString("imgpath"),
                                    jsonObject.getString("firstname"),
                                    jsonObject.getString("lastname"),
                                    jsonObject.getString("note"),
                                    String.valueOf(jsonObject.getInt("id"))
                            );
                            actorArrayList.add(actor);
                            stringActor.add(actor.getFirstname() + " " + actor.getLastname());
                        }
                        stringAdapterActor = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, stringActor);
                        stringAdapterActor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sprActorID.setAdapter(stringAdapterActor);
                    } catch (JSONException e) {
                        Log.e("ERROR", e.toString());
                    }
                },
                error -> Log.e("VolleyError", error.toString()))
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

    private void getRMovies(){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                getString(R.string.URLProject) + "movie",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("movies");
                            for (int x = 0; x < jsonArray.length(); x++){
                                JSONObject jsonObject = jsonArray.getJSONObject(x);
                                JSONObject jsonGenre = jsonObject.getJSONObject("genre");
                                JSONObject jsonProducer = jsonObject.getJSONObject("producer");
                                Movie movie = new Movie(
                                        jsonObject.getString("title"),
                                        jsonObject.getString("description"),
                                        jsonObject.getString("release"),
                                        jsonProducer.getString("name"),
                                        jsonGenre.getString("name"),
                                        jsonObject.getString("imgpath"),
                                        String.valueOf(jsonObject.getInt("id"))
                                );
                                movieArrayList.add(movie);
                                stringMovie.add(movie.getTitle());
                            }
                            stringAdapterMovie = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, stringMovie);
                            stringAdapterMovie.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            sprMovieID.setAdapter(stringAdapterMovie);
                        } catch (JSONException e) {
                            Log.e("ERROR", e.toString());
                        }
                    }
                },
                error -> Log.e("VolleyError", error.toString()))
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                SharedPreferences sharedPreferences = getApplication().getSharedPreferences(
                        "loginCredential", Context.MODE_PRIVATE
                );
                headers.put("Authorization", "Bearer "+ sharedPreferences.getString("access_token", null));
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    private void storeRoleData(){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name" , tboxRoleName.getText().toString());
            jsonObject.put("movie_id" , movieID);
            jsonObject.put("actor_id" , actorID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = getString(R.string.URLProject) + "role";
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
                            Toast.makeText(RoleFormActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            SessionClass.FRAGMENT = new RoleFragment();
                            Intent intent = new Intent(RoleFormActivity.this , MainActivity.class);
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