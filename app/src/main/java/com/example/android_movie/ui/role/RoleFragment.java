package com.example.android_movie.ui.role;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

public class RoleFragment extends Fragment {
    ArrayList<Role> roles = new ArrayList<Role>();
    RoleRecyclerViewAdapter RoleAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_role, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstancesState){
        super.onViewCreated(view, savedInstancesState);

        RecyclerView RecyclerViewRole = view.findViewById(R.id.RecyclerViewRole);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        RecyclerViewRole.setLayoutManager(layoutManager);

        RoleAdapter = new RoleRecyclerViewAdapter(getActivity().getApplicationContext());
        RecyclerViewRole.setAdapter(RoleAdapter);

        FloatingActionButton fabCreateRole = view.findViewById(R.id.fabCreateRole);
        fabCreateRole.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), RoleFormActivity.class);
            startActivity(intent);
        });
        getRoles();
    }

    public void getRoles(){
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                getString(R.string.URLProject) + "role",
                null,
                response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("roles");
                        for (int x = 0; x < jsonArray.length(); x++){
                            JSONObject jsonObject = jsonArray.getJSONObject(x);
                            Role role = new Role(
                                    jsonObject.getJSONObject("actor").getString("firstname") + " " + jsonObject.getJSONObject("actor").getString("lastname"),
                                    jsonObject.getJSONObject("movie").getString("title"),
                                    jsonObject.getString("name"),
                                    String.valueOf(jsonObject.getInt("id"))
                            );
                            roles.add(role);
                        }
                        RoleAdapter.setRoles(roles);
                    } catch (JSONException e) {
                        Log.e("ERROR", e.toString());
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