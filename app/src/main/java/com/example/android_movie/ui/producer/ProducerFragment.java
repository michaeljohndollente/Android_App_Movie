package com.example.android_movie.ui.producer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.example.android_movie.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProducerFragment extends Fragment {

    RecyclerView RecyclerViewProducer;
    ArrayList<Producer> producers = new ArrayList<Producer>();
    ProducerRecyclerViewAdapter ProducerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_producer, container, false);
    }

    @Override
    public  void onViewCreated(View view, @Nullable Bundle savedInstancesState){
        super.onViewCreated(view, savedInstancesState);

        RecyclerView RecyclerViewProducer = view.findViewById(R.id.RecyclerViewProducer);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        RecyclerViewProducer.setLayoutManager(layoutManager);

        ProducerAdapter = new ProducerRecyclerViewAdapter(getActivity().getApplicationContext());
        RecyclerViewProducer.setAdapter(ProducerAdapter);

        FloatingActionButton fabCreateProd = view.findViewById(R.id.fabCreateProd);
        fabCreateProd.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), ProducerFormActivity.class);
            startActivity(intent);
        });

        getProducers();
    }

    public void getProducers() {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
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
                            producers.add(producer);
                        }
                        ProducerAdapter.setProducers(producers);
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