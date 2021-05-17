package com.example.android_movie.ui.role;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android_movie.R;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RoleRecyclerViewAdapter extends RecyclerView.Adapter<RoleRecyclerViewAdapter.ViewHolder> {


    Context context;
    ArrayList<Role> roles = new ArrayList<Role>();

    public RoleRecyclerViewAdapter(Context context){
        this.context = context;
    }

    public void setRoles(ArrayList<Role> roles){
        this.roles = roles;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RoleRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_role_item,parent,false);
        return new RoleRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoleRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.textAName.setText(roles.get(position).getName());
        holder.textMovie.setText(roles.get(position).getMovie());
        holder.textActor.setText(roles.get(position).getActor());

        holder.btnEditRole.setOnClickListener(v -> {
            Intent intent = new Intent(context, RoleFormActivity.class);
            intent.putExtra("id", roles.get(position).getId());
            intent.putExtra("name", roles.get(position).getName());
            intent.putExtra("actor_id", roles.get(position).getActor());
            intent.putExtra("movie_id", roles.get(position).getMovie());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            Toast.makeText(context, "Edit", Toast.LENGTH_SHORT).show();
        });

        holder.btnDelRole.setOnClickListener(v -> deleteRole(roles.get(position).getId(), position));
    }

    private void deleteRole(String id, int position) {
        RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                context.getString(R.string.URLProject) + "role/" + id,
                null,
                response -> {
                    try {
                        Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                        roles.remove(position);
                        notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.e("volleyError", error.toString()))
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(
                        "loginCredential", Context.MODE_PRIVATE
                );
                headers.put("Authorization", "Bearer "+ sharedPreferences.getString("access_token", null));
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public int getItemCount() {
        return roles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textAName, textMovie, textActor;
        ImageButton btnEditRole, btnDelRole;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            textAName = itemView.findViewById(R.id.txtActorName);
            textActor = itemView.findViewById(R.id.txtActorID);
            textMovie = itemView.findViewById(R.id.txtMovieid);
            btnEditRole = itemView.findViewById(R.id.btnEditRole);
            btnDelRole = itemView.findViewById(R.id.btnDelRole);

        }
    }
}