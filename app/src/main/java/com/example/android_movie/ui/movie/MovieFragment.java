package com.example.android_movie.ui.movie;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android_movie.MainActivity;
import com.example.android_movie.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MovieFragment extends Fragment {

    ArrayList<Movie> movies = new ArrayList<Movie>();
    MovieRecyclerViewAdapter MovieAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movie, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstancesState){
        super.onViewCreated(view, savedInstancesState);

        RecyclerView RecyclerViewMovie = view.findViewById(R.id.RecyclerViewMovie);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        RecyclerViewMovie.setLayoutManager(layoutManager);

        MovieAdapter = new MovieRecyclerViewAdapter((MainActivity) getActivity());
        RecyclerViewMovie.setAdapter(MovieAdapter);

        FloatingActionButton fabCreateMovie = view.findViewById(R.id.fabCreateMovie);
        fabCreateMovie.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), MovieFormActivity.class);
            startActivity(intent);
        });

        getMovies();
    }

    public void getMovies(){
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
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
                                JSONArray rolesArray = jsonObject.getJSONArray("roles");
                                String rolesConcat = "";
                                for (int i = 0; i < rolesArray.length(); i++) {
                                    rolesConcat += rolesArray.getJSONObject(i).getJSONObject("actor").getString("firstname") +
                                            " " + rolesArray.getJSONObject(i).getJSONObject("actor").getString("lastname") +
                                            " as " + rolesArray.getJSONObject(i).getString("name")  + "\n";
                                }
                                movie.setRoles(rolesConcat);
                                movies.add(movie);
                            }
                            MovieAdapter.setMovies(movies);
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
                SharedPreferences sharedPreferences = getContext().getSharedPreferences(
                        "loginCredential", Context.MODE_PRIVATE
                );
                headers.put("Authorization", "Bearer "+ sharedPreferences.getString("access_token", null));
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
}