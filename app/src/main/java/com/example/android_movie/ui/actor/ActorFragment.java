package com.example.android_movie.ui.actor;

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

public class ActorFragment extends Fragment {

    RecyclerView RecyclerViewActor;
    ArrayList<Actor> actors = new ArrayList<Actor>();
    ActorRecyclerViewAdapter ActorAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_actor, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstancesState){
        super.onViewCreated(view, savedInstancesState);

        RecyclerViewActor = view.findViewById(R.id.RecyclerViewActor);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        RecyclerViewActor.setLayoutManager(layoutManager);

        ActorAdapter = new ActorRecyclerViewAdapter((MainActivity) getActivity());
        RecyclerViewActor.setAdapter(ActorAdapter);

        FloatingActionButton fabCreateActor = view.findViewById(R.id.fabCreateActor);
        fabCreateActor.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), ActorFormActivity.class);
            startActivity(intent);
        });

        getActors();
    }

    public void getActors() {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
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
                            JSONArray rolesArray = jsonObject.getJSONArray("roles");
                            String rolesConcat = "";
                            for (int i = 0; i < rolesArray.length(); i++) {
                                rolesConcat += rolesArray.getJSONObject(i).getJSONObject("movie").getString("title") +
                                        " as " + rolesArray.getJSONObject(i).getString("name")  + "\n";
                            }
                            actor.setRole(rolesConcat);
                            actors.add(actor);
                        }
                        ActorAdapter.setActors(actors);
                    } catch (JSONException e) {
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